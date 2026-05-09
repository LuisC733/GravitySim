package com.nbodysim.renderer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import com.nbodysim.Config;
import com.nbodysim.physics.Body;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class SpacetimeGrid {
    private static final float G_VISUAL = 1.0f;
    private static final float GRID_EPSILON = 10.0f;
    private static final float HEIGHT_SCALE = 45.0f;
    private static final float POTENTIAL_STRENGTH = 0.04f;

    int gridShaderProgram;
    ShaderProgram shader;
    int VBO;
    int VAO;
    int EBO;
    int gridSize = 1000;
    int N = 600;
    float[] vertices = new float[N * N * 3];
    int[] indices = new int[2 * N * (N - 1) * 2];
    private final float[] heights = new float[N * N];
    private final float[] smoothedHeights = new float[N * N];
    private final GridPotentialTree potentialTree = new GridPotentialTree((float) Config.THETA, GRID_EPSILON);

    void initSTG() {
        shader = new ShaderProgram();
        String vert = shader.loadShader("src/main/resources/shaders/vertexGrid.glsl");
        String frag = shader.loadShader("src/main/resources/shaders/fragGrid.glsl");
        gridShaderProgram = shader.createShaderProgram(vert, frag);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int index = (j * N + i) * 3;
                vertices[index++] = -gridSize / 2 + i * (gridSize / (float) (N - 1)); // x
                vertices[index++] = 0; // y
                vertices[index++] = -gridSize / 2 + j * (gridSize / (float) (N - 1)); // z
            }
        }
        int k = 0;
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < N - 1; i++) {
                indices[k++] = j * N + i;
                indices[k++] = j * N + (i + 1);
            }
        }
        for (int j = 0; j < N - 1; j++) {
            for (int i = 0; i < N; i++) {
                indices[k++] = j * N + i;
                indices[k++] = (j + 1) * N + i;
            }
        }

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 12, 0);

        EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    void render(Matrix4f view, Matrix4f projection) {
        glUseProgram(gridShaderProgram);

        int modelLoc = glGetUniformLocation(gridShaderProgram, "model");
        int viewLoc = glGetUniformLocation(gridShaderProgram, "view");
        int projLoc = glGetUniformLocation(gridShaderProgram, "projection");
        int colorLoc = glGetUniformLocation(gridShaderProgram, "gridColor");

        float[] modelArr = new Matrix4f().get(new float[16]);
        float[] viewArr = view.get(new float[16]);
        float[] projArr = projection.get(new float[16]);

        glUniformMatrix4fv(modelLoc, false, modelArr);
        glUniformMatrix4fv(viewLoc, false, viewArr);
        glUniformMatrix4fv(projLoc, false, projArr);
        glUniform4f(colorLoc, 0.0f, 0.8f, 1.0f, 1.0f);

        glBindVertexArray(VAO);
        glDrawElements(GL_LINES, indices.length, GL_UNSIGNED_INT, 0L);
        glBindVertexArray(0);
    }

    void updateGrid(ArrayList<Body> bodies) {
        float x, z;
        potentialTree.rebuild(bodies);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int index = (j * N + i) * 3;
                x = vertices[index];
                z = vertices[index + 2];

                heights[j * N + i] = mapPotentialToHeight(potentialTree.potentialAt(x, z));
            }
        }
        smoothHeights();
        uploadHeights();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        FloatBuffer vb = MemoryUtil.memAllocFloat(vertices.length);
        vb.put(vertices).flip();
        glBufferSubData(GL_ARRAY_BUFFER, 0, vb);
        MemoryUtil.memFree(vb);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private float mapPotentialToHeight(float potential) {
        float compressedPotential = 1.0f - (float) Math.exp(-G_VISUAL * potential * POTENTIAL_STRENGTH);
        return -HEIGHT_SCALE * compressedPotential;
    }

    private void smoothHeights() {
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < N; i++) {
                float weightedSum = 0.0f;
                float weightSum = 0.0f;

                for (int dz = -1; dz <= 1; dz++) {
                    int sampleJ = j + dz;
                    if (sampleJ < 0 || sampleJ >= N) {
                        continue;
                    }

                    for (int dx = -1; dx <= 1; dx++) {
                        int sampleI = i + dx;
                        if (sampleI < 0 || sampleI >= N) {
                            continue;
                        }

                        float weight = smoothingWeight(dx, dz);
                        weightedSum += heights[sampleJ * N + sampleI] * weight;
                        weightSum += weight;
                    }
                }

                smoothedHeights[j * N + i] = weightedSum / weightSum;
            }
        }
    }

    private float smoothingWeight(int dx, int dz) {
        if (dx == 0 && dz == 0) {
            return 0.4f;
        }
        if (dx == 0 || dz == 0) {
            return 0.1f;
        }
        return 0.05f;
    }

    private void uploadHeights() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int gridIndex = j * N + i;
                int vertexIndex = gridIndex * 3;
                vertices[vertexIndex + 1] = smoothedHeights[gridIndex];
            }
        }
    }
}
