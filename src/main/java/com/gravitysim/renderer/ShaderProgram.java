package com.gravitysim.renderer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ShaderProgram {

    int compileShader(int type, String sourceCode){
        int shaderObject = 0;
        if(type == GL_VERTEX_SHADER){
            shaderObject = glCreateShader(GL_VERTEX_SHADER);
        }
        else if(type == GL_FRAGMENT_SHADER){
             shaderObject = glCreateShader(GL_FRAGMENT_SHADER);
        }

        glShaderSource(shaderObject, sourceCode);
        glCompileShader(shaderObject);

        if (glGetShaderi(shaderObject, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderObject);
            throw new RuntimeException("Shader compile error:\n" + log);
        }

        return shaderObject;
        }

    int createShaderProgram(String vertexSource, String fragmentSource){
        int programObject =  glCreateProgram();
        int VertexShader = compileShader(GL_VERTEX_SHADER, vertexSource);
        int FragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSource);
        glAttachShader(programObject, VertexShader);
        glAttachShader(programObject, FragmentShader);
        glLinkProgram(programObject);

        if (glGetProgrami(programObject, GL_LINK_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(programObject);
            throw new RuntimeException("Shader link error:\n" + log);
        }
        
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