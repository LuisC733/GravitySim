package com.gravitysim.renderer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.system.MemoryUtil.*;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Renderer{
    int GraphicsPipelineShaderP = 0;
    BodyRenderer body;

    private int height, width;
    private String title;
    private long glfwWindow;

    private static Renderer window = null;

    private Renderer(){
        this.height = 1280;
        this.width = 800;
        this.title = "N-Body Simulation";
    }

    public static Renderer get(){
        if(Renderer.window == null){
            Renderer.window = new Renderer();
        }
        return Renderer.window;
    }
    public void run(){
        System.out.println("N-Body Simulation" + Version.getVersion());

        init();
        loop();

        // Free the window callbacks and destroy the window
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
    }
    public void init(){
        // setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // initialize GLFW
        if(!glfwInit()){
            throw new IllegalStateException("Unable to intitialize GLFW");
        }

        // configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // create window
        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
        if(glfwWindow == NULL){
            throw new RuntimeException("Failed to create Window");
        }

        // make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        // make v-sync
        glfwSwapInterval(1);

        // make window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();
        System.out.println("Capabilities created on: " + Thread.currentThread().getName());
        CreateGraphicsPipeline();
        body = new BodyRenderer();
        body.VertexSpecifications();
    }
    public void loop(){
        while(!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            Draw();

            glfwSwapBuffers(glfwWindow);
        }
    }
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
    void Draw(){
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glViewport(0, 0, width, height);
        glClearColor(1.f, 1.f, 0.f, 1.f);

        glUseProgram(GraphicsPipelineShaderP);

        glBindVertexArray(body.VAO);
        glBindBuffer(GL_ARRAY_BUFFER, body.VBO);

        glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    String vs = """
            #version 330 core
            layout(location = 0) in vec3 position;
            void main(){
            gl_Position = vec4(position, 1.0);
            }
            """;
    String fs = """
            #version 330 core
            out vec4 fragColor;
            void main(){
            fragColor = vec4(1.0, 0.5, 0.2, 1.0);
            }
            """;

    void CreateGraphicsPipeline(){
        GraphicsPipelineShaderP = CreateShaderProgram(vs, fs);
    }
}