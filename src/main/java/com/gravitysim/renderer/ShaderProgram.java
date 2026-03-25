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

    String vertexShader = """
            #version 330 core
            layout(location = 0) in vec3 position;
            layout(location = 1) in vec3 normal;

            out vec3 fragNormal;
            out vec3 fragPos;

            uniform mat4 model;
            uniform mat4 view;
            uniform mat4 projection;

            void main(){
            gl_Position = projection * view * model * vec4(position, 1.0);
            fragNormal = mat3(transpose(inverse(model))) * normal;
            fragPos = vec3(model * vec4(position, 1.0));
            }
            """;
    String fragShader = """
            #version 330 core
            in vec3 fragNormal;
            in vec3 fragPos;

            out vec4 fragColor;

            uniform vec3 lightPos;
            uniform vec3 viewPos;
            uniform vec3 lightColor;

            void main(){
            fragColor = vec4(1.0, 0.5, 0.2, 1.0);
            vec3 ambient = lightColor * 0.1f;

            vec3 normal = normalize(fragNormal);
            vec3 lightDir = normalize(lightPos - fragPos);
            float diff = max(dot(normal, lightDir), 0.0);
            vec3 diffuse = lightColor * diff;

            vec3 viewDir = normalize(viewPos - fragPos);
            vec3 reflectDir = reflect(-lightDir, normal);
            float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
            vec3 specular = lightColor * spec * 0.5;

            vec3 finalColor = (ambient + diffuse + specular) * vec3(1.0, 0.5, 0.2);

            fragColor = vec4(finalColor, 1.0f);
            }
            """;
}