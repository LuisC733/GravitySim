package com.gravitysim.renderer;

import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ShaderProgram {

    int CompileShader(int type, String sourceCode){
        int shaderObject = 0;
        if(type == GL_VERTEX_SHADER){
            shaderObject = glCreateShader(GL_VERTEX_SHADER);
        }
        else if(type == GL_FRAGMENT_SHADER){
             shaderObject = glCreateShader(GL_FRAGMENT_SHADER);
        }

        glShaderSource(shaderObject, sourceCode);
        glCompileShader(shaderObject);

        return shaderObject;
        }

    int CreateShaderProgram(String vertexSource, String fragmentSource){
        int programObject =  glCreateProgram();
        int VertexShader = CompileShader(GL_VERTEX_SHADER, vertexSource);
        int FragmentShader = CompileShader(GL_FRAGMENT_SHADER, fragmentSource);
        glAttachShader(programObject, VertexShader);
        glAttachShader(programObject, FragmentShader);
        glLinkProgram(programObject);

        glValidateProgram(programObject);

        return programObject;
    }
    String loadShader(String path) {
    try {
        return new String(Files.readAllBytes(Paths.get(path)));
    } 
    catch (IOException e) {
        throw new RuntimeException("Shader not found: " + path, e);
    }
}
    String vertexShader = loadShader("src/main/resources/shaders/vertexshader.glsl");
    String fragShader = loadShader("src/main/resources/shaders/fragmentshader.glsl");
}