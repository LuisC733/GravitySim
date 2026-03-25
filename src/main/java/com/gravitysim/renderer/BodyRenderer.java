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
import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;

public class BodyRenderer {
    int VAO = 0;
    int VBO = 0;
    int EBO = 0;
    int n = 36;

    void VertexSpecifications(){
        double radius = 0.5;
        float[] vertexPositions = new float[(n + 1) * 3];
        int[] indices = new int[n * 3];

        for(int i = 1; i <= n; i++){
            double theta = i * (2 * PI / n);
            vertexPositions[i*3] = (float)(radius * cos(theta));
            vertexPositions[i*3+1] = (float)(radius * sin(theta));
            vertexPositions[i*3+2] = 0;

            indices[(i-1)*3] = 0;
            indices[(i-1)*3+1] = i;
            indices[(i-1)*3+2] = i == n ? 1 : i + 1;
        }

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexPositions, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
        glDisableVertexAttribArray(0);
    }
}
