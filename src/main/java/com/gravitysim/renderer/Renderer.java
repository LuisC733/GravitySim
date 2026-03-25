package com.gravitysim.renderer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL20.*;

public class Renderer{
    int GraphicsPipelineShaderP = 0;
    BodyRenderer body;
    ShaderProgram program;

    int height, width;
    private String title;
    private long glfwWindow;

    private static Renderer window = null;

    Renderer(){
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

        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetFramebufferSize(glfwWindow, w, h);
        width = w[0];
        height = h[0];

        GL.createCapabilities();
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
    void Draw(){
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glViewport(0, 0, width, height);

        glUseProgram(GraphicsPipelineShaderP);

        glBindVertexArray(body.VAO);

        glDrawElements(GL_TRIANGLES, body.n * 3, GL_UNSIGNED_INT, 0);
    }
    void CreateGraphicsPipeline(){
        program = new ShaderProgram();
        GraphicsPipelineShaderP = program.CreateShaderProgram(program.vertexShader, program.fragShader);
    }
}