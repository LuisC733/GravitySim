package com.gravitysim.renderer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;

public class BodyRenderer {
    Vector3f position;
    float radius;
    Matrix4f modelMatrix;

    public BodyRenderer(Vector3f position, float radius){
        this.position = position;
        this.radius = radius;
        this.modelMatrix = new Matrix4f().translation(position).scale(radius);
    }
    
    int VAO = 0;
    int VBO = 0;
    int EBO = 0;
    int stacks = 32;
    int slices = 32;

    void VertexSpecifications(){
        float[] vertexPositions = new float[((stacks + 1) * (slices + 1) * 3) * 2];
        int[] indices = new int[stacks * slices * 6];

        int v = 0;
        for(int i = 0; i <= stacks; i++){
            double phi = i * (PI / stacks);
            for(int j = 0; j <= slices; j++){
                double theta = j * (2 * PI / slices);
                vertexPositions[v++] = (float) (sin(phi) * cos(theta)); // x
                vertexPositions[v++] = (float) (cos(phi));              // y
                vertexPositions[v++] = (float) (sin(phi) * sin(theta)); // z
                vertexPositions[v++] = (float) (sin(phi) * cos(theta)); // nx
                vertexPositions[v++] = (float) (cos(phi));              // ny
                vertexPositions[v++] = (float) (sin(phi) * sin(theta)); // nz
            }
        }
        int index = 0;

        for(int i = 0; i < stacks; i++){
            for(int j = 0; j < slices; j++){
                int A = i * (slices + 1) + j;
                int B = (i + 1) * (slices + 1) + j;
                int C = i * (slices + 1) + j + 1;
                int D = (i + 1) * (slices + 1) + j + 1;

                // Triangle 1
                indices[index++] = A; 
                indices[index++] = B; 
                indices[index++] = C; 
                
                // Triangle 2
                indices[index++] = B;
                indices[index++] = C;
                indices[index++] = D;
            }
        }

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexPositions, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 24, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 24, 12);

        EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
        glDisableVertexAttribArray(0);
    }
}