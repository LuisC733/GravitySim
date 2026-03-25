package com.gravitysim.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    Matrix4f viewMatrix = new Matrix4f();
    Matrix4f projectionMatrix = new Matrix4f();
    Vector3f cameraPos = new Vector3f(2,2,3);
    Vector3f center = new Vector3f(0,0,0);
    Vector3f up = new Vector3f(0,1,0);
    Renderer renderer;

    void init(){
        projectionMatrix.perspective((float)(Math.toRadians(45)), 
        (float) Renderer.get().width / (float) Renderer.get().height, 
        0.1f, 100.0f);
        viewMatrix.lookAt(cameraPos, center, up);
    }
}