package com.gravitysim.renderer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.GL;

public class Renderer{
    int GraphicsPipelineShaderP = 0;
    BodyRenderer body;
    ShaderProgram program;
    Camera camera;

    int modelLoc;
    int viewLoc;
    int projectionLoc;

    int lightPosLoc;
    int viewPosLoc;
    int lightColorLoc;

    public ArrayList<BodyRenderer> bodies = new ArrayList<>();

    int height, width;
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

		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);

		glfwTerminate();
		glfwSetErrorCallback(null).free();
    }
    public void init(){
        GLFWErrorCallback.createPrint(System.err).set();
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

        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
        if(glfwWindow == NULL){
            throw new RuntimeException("Failed to create Window");
        }

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);

        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetFramebufferSize(glfwWindow, w, h);
        width = w[0];
        height = h[0];

        GL.createCapabilities();
        camera = new Camera();
        camera.init();
        CreateGraphicsPipeline();
        modelLoc = glGetUniformLocation(GraphicsPipelineShaderP, "model");
        viewLoc = glGetUniformLocation(GraphicsPipelineShaderP, "view");
        projectionLoc = glGetUniformLocation(GraphicsPipelineShaderP, "projection");
        lightPosLoc = glGetUniformLocation(GraphicsPipelineShaderP, "lightPos"); 
        viewPosLoc = glGetUniformLocation(GraphicsPipelineShaderP, "viewPos");
        lightColorLoc = glGetUniformLocation(GraphicsPipelineShaderP, "lightColor");
        for(BodyRenderer body : bodies){
            body.VertexSpecifications();
        }
    }
    public void loop(){
        while(!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Draw();

            glfwSwapBuffers(glfwWindow);
        }
    }
    void Draw(){
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glViewport(0, 0, width, height);

        glUseProgram(GraphicsPipelineShaderP);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer bufferP = stack.mallocFloat(16);
            FloatBuffer bufferV = stack.mallocFloat(16);
            camera.projectionMatrix.get(bufferP);
            camera.viewMatrix.get(bufferV);
            glUniformMatrix4fv(projectionLoc, false, bufferP);
            glUniformMatrix4fv(viewLoc, false, bufferV);
        }
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer bufferLight = stack.mallocFloat(3);
            FloatBuffer bufferView = stack.mallocFloat(3);
            FloatBuffer bufferColor = stack.mallocFloat(3);
            camera.lightPos.get(bufferLight);
            camera.cameraPos.get(bufferView);
            camera.lightColor.get(bufferColor);
            glUniform3fv(lightPosLoc, bufferLight);
            glUniform3fv(viewPosLoc, bufferView);
            glUniform3fv(lightColorLoc, bufferColor);
        }

        for(BodyRenderer body : bodies){
            try(MemoryStack stack = MemoryStack.stackPush()){
                FloatBuffer bufferM = stack.mallocFloat(16);
                body.modelMatrix.get(bufferM);
                glUniformMatrix4fv(modelLoc, false, bufferM);
            }
            glBindVertexArray(body.VAO);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glDrawElements(GL_TRIANGLES, body.stacks * body.slices * 6, GL_UNSIGNED_INT, 0);
        }
    }
    void CreateGraphicsPipeline(){
        program = new ShaderProgram();
        GraphicsPipelineShaderP = program.CreateShaderProgram(program.vertexShader, program.fragShader);
    }
}