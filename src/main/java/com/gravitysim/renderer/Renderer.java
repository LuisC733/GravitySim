package com.gravitysim.renderer;

import org.joml.Vector3f;
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

public class Renderer {

    // Renderer Singleton
    private static Renderer instance = null;

    private Renderer() {
        this.width  = 1280;
        this.height = 800;
        this.title  = "N-Body Simulation";
        this.mouseLastX = width  / 2.0;
        this.mouseLastY = height / 2.0;
    }

    public static Renderer get() {
        if (instance == null) {
            instance = new Renderer();
        }
        return instance;
    }

    // Window
    int width;
    int height;
    private String title;
    public long glfwWindow;

    
    public ArrayList<BodyRenderer> bodies = new ArrayList<>();
    private Camera camera;

    
    // Shader
    private ShaderProgram shaderProgram;
    private int shaderProgramId;
    private int LocModel;
    private int LocView;
    private int LocProjection;
    private int LocLightPos;
    private int LocViewPos;
    private int LocLightColor;

    
    // mouse movement 
    private double mouseLastX;
    private double mouseLastY;
    private boolean mouseFirstMove = true;
    private static final float mouseSensitivity = 0.05f;
    private static final float cameraSpeed = 10.0f;

    public void init() {
        initGlfw();
        initOpenGL();
        initCamera();
        initShaderPipeline();
        initUniforms();
        initBodies();
        initCallbacks();
    }

    // Initialise GLFW and create the window
    private void initGlfw() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE,   GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE,        GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);

        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetFramebufferSize(glfwWindow, w, h);
        width  = w[0];
        height = h[0];
    }

    private void initOpenGL() {
        GL.createCapabilities();
    }

    private void initCamera() {
        camera = new Camera();
        camera.init();
        camera.updateVectors();
    }

    private void initShaderPipeline() {
        shaderProgram   = new ShaderProgram();
        shaderProgramId = shaderProgram.createShaderProgram(
                shaderProgram.vertexShader, shaderProgram.fragShader);
    }

    // Locations
    private void initUniforms() {
        LocModel      = glGetUniformLocation(shaderProgramId, "model");
        LocView       = glGetUniformLocation(shaderProgramId, "view");
        LocProjection = glGetUniformLocation(shaderProgramId, "projection");
        LocLightPos   = glGetUniformLocation(shaderProgramId, "lightPos");
        LocViewPos    = glGetUniformLocation(shaderProgramId, "viewPos");
        LocLightColor = glGetUniformLocation(shaderProgramId, "lightColor");
    }

    // Upload vertices
    private void initBodies() {
        for (BodyRenderer body : bodies) {
            body.VertexSpecifications();
        }
    }

    private void initCallbacks() {
        glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwSetCursorPosCallback(glfwWindow, (win, xPos, yPos) -> {
            if (mouseFirstMove) {
                mouseLastX    = xPos;
                mouseLastY    = yPos;
                mouseFirstMove = false;
                return;
            }

            double deltaX = xPos - mouseLastX;
            double deltaY = mouseLastY - yPos; // inverted: screen-Y grows downward

            camera.yaw   += (float) (deltaX * mouseSensitivity);
            camera.pitch += (float) (deltaY * mouseSensitivity);

            // clamp
            if (camera.pitch >  89.0f) camera.pitch =  89.0f;
            if (camera.pitch < -89.0f) camera.pitch = -89.0f;

            camera.updateVectors();
            mouseLastX = xPos;
            mouseLastY = yPos;
        });
    }
    public void drawFrame(float deltaTime){
            glfwPollEvents();
            processCameraInput(deltaTime);

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            draw();

            glfwSwapBuffers(glfwWindow);
    }

    private void processCameraInput(float deltaTime) {
        float step = cameraSpeed * deltaTime;

        if (glfwGetKey(glfwWindow, GLFW_KEY_W) == GLFW_PRESS) {
            camera.cameraPos.add(new Vector3f(camera.frontVector).mul(step));
        }
        if (glfwGetKey(glfwWindow, GLFW_KEY_S) == GLFW_PRESS) {
            camera.cameraPos.sub(new Vector3f(camera.frontVector).mul(step));
        }
        if (glfwGetKey(glfwWindow, GLFW_KEY_D) == GLFW_PRESS) {
            camera.cameraPos.add(new Vector3f(camera.rightVector).mul(step));
        }
        if (glfwGetKey(glfwWindow, GLFW_KEY_A) == GLFW_PRESS) {
            camera.cameraPos.sub(new Vector3f(camera.rightVector).mul(step));
        }
        boolean moving = glfwGetKey(glfwWindow, GLFW_KEY_W) == GLFW_PRESS
                      || glfwGetKey(glfwWindow, GLFW_KEY_S) == GLFW_PRESS
                      || glfwGetKey(glfwWindow, GLFW_KEY_D) == GLFW_PRESS
                      || glfwGetKey(glfwWindow, GLFW_KEY_A) == GLFW_PRESS;
        if (moving) {
            camera.updateVectors();
        }
    }

    private void draw() {
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glViewport(0, 0, width, height);

        glUseProgram(shaderProgramId);

        uploadCameraUniforms();

        for (BodyRenderer body : bodies) {
            uploadModelUniform(body);
            glBindVertexArray(body.VAO);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glDrawElements(GL_TRIANGLES, body.stacks * body.slices * 6, GL_UNSIGNED_INT, 0);
        }
    }

    private void uploadCameraUniforms() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer projection = stack.mallocFloat(16);
            FloatBuffer view       = stack.mallocFloat(16);
            camera.projectionMatrix.get(projection);
            camera.viewMatrix.get(view);
            glUniformMatrix4fv(LocProjection, false, projection);
            glUniformMatrix4fv(LocView,       false, view);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer lightPos   = stack.mallocFloat(3);
            FloatBuffer viewPos    = stack.mallocFloat(3);
            FloatBuffer lightColor = stack.mallocFloat(3);
            camera.lightPos.get(lightPos);
            camera.cameraPos.get(viewPos);
            camera.lightColor.get(lightColor);
            glUniform3fv(LocLightPos,   lightPos);
            glUniform3fv(LocViewPos,    viewPos);
            glUniform3fv(LocLightColor, lightColor);
        }
    }

    private void uploadModelUniform(BodyRenderer body) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer model = stack.mallocFloat(16);
            body.modelMatrix.get(model);
            glUniformMatrix4fv(LocModel, false, model);
        }
    }

    public void cleanup() {
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}