package com.gravitysim.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static java.lang.Math.sin;
import static java.lang.Math.cos;

public class Camera {

    float yaw = -90.0f;
    float pitch = 0.0f;
    Matrix4f viewMatrix = new Matrix4f();
    Matrix4f projectionMatrix = new Matrix4f();
    Vector3f frontVector;
    Vector3f center = new Vector3f(0,0,0);
    Vector3f cameraPos = new Vector3f(0,3,15);
    Vector3f up = new Vector3f(0,1,0);
    Vector3f lightPos = new Vector3f(0, 0, 0);
    Vector3f lightColor = new Vector3f(1,1,1);
    Renderer renderer;

    void init(){
        projectionMatrix.perspective((float)(Math.toRadians(45)), 
        (float) Renderer.get().width / (float) Renderer.get().height, 
        0.1f, 100.0f);
        viewMatrix.lookAt(cameraPos, center, up);
    }
    void updateVectors(){
        frontVector = new Vector3f ((float)(cos(Math.toRadians(yaw)) * cos(Math.toRadians(pitch))), 
                                   (float)(sin(Math.toRadians(pitch))), 
                                   (float)(sin(Math.toRadians(yaw)) * cos(Math.toRadians(pitch)))).normalize();
        center = new Vector3f(cameraPos).add(frontVector);
        viewMatrix.lookAt(cameraPos, center, up);
    }
}