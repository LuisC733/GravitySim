package com.gravitysim.renderer;

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
import java.nio.FloatBuffer;
import java.util.ArrayList;
import com.gravitysim.physics.Body;
import static java.lang.Math.sqrt;
import static java.lang.Math.log;

public class SpacetimeGrid {
    int gridShaderProgram;
    ShaderProgram shader;
    int VBO;
    int VAO;
    int EBO;
    int gridSize = 1000;
    int N = 600;
    float[] vertices = new float[N * N * 3];
    int[] indices = new int[2 * N * (N-1) * 2];

    void initSTG(){
        shader = new ShaderProgram();
        String vert = shader.loadShader("src/main/resources/shaders/vertexGrid.glsl");
        String frag = shader.loadShader("src/main/resources/shaders/fragGrid.glsl");
        gridShaderProgram = shader.createShaderProgram(vert, frag);

        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                int index = (j * N + i) * 3;
                vertices[index++] = -gridSize/2 + i * (gridSize / (float) (N-1)); // x
                vertices[index++] = 0;                                    // y
                vertices[index++] = -gridSize/2 + j * (gridSize / (float) (N-1)); // z
            }
        }
        int k = 0;
        for (int j = 0; j < N; j++){
            for (int i = 0; i < N - 1; i++){
                indices[k++] = j * N + i;
                indices[k++] = j * N + (i + 1);
            }
        }
        for (int j = 0; j < N - 1; j++){
            for (int i = 0; i < N; i++){
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
    void updateGrid(ArrayList<Body> bodies){
        float x, z;
        float G_VISUAL = 1.0f;
        float epsilon = 10.0f;
        double SCALE = 1e10;

        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                int index = (j * N + i) * 3;
                x = vertices[index];
                z = vertices[index + 2];

                float ySum = 0;

                for(Body body : bodies){
                    float dx = (float) (x - body.position.x / SCALE);
                    float dz = (float) (z - body.position.z / SCALE);
                    float r = (float) (sqrt(dx*dx + dz*dz) + epsilon);
                    ySum -= G_VISUAL * log(body.mass) / r;
                }
                vertices[index + 1] = ySum;
            }
        }
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        FloatBuffer vb = MemoryUtil.memAllocFloat(vertices.length);
        vb.put(vertices).flip();
        glBufferSubData(GL_ARRAY_BUFFER, 0, vb);
        MemoryUtil.memFree(vb);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}