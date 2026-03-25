package com.gravitysim.renderer;

import static org.lwjgl.opengl.GL20.*;

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

    int CreateShaderProgram(String vs, String fs){
        int programObject =  glCreateProgram();
        int VertexShader = CompileShader(GL_VERTEX_SHADER, vs);
        int FragmentShader = CompileShader(GL_FRAGMENT_SHADER, fs);
        glAttachShader(programObject, VertexShader);
        glAttachShader(programObject, FragmentShader);
        glLinkProgram(programObject);

        glValidateProgram(programObject);

        return programObject;
    }

    String vertexShader = """
            #version 330 core
            layout(location = 0) in vec3 position;
            void main(){
            gl_Position = vec4(position, 1.0);
            }
            """;
    String fragShader = """
            #version 330 core
            out vec4 fragColor;
            void main(){
            fragColor = vec4(1.0, 0.5, 0.2, 1.0);
            }
            """;
}