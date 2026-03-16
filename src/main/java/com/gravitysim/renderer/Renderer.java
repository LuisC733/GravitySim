package com.gravitysim.renderer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer{

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
    }
    public void loop(){
        while(!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glfwSwapBuffers(glfwWindow);
        }
    }
}