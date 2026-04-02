package com.gravitysim;

import static org.lwjgl.glfw.GLFW.*;
import java.util.ArrayList;

import org.joml.Vector3f;

import com.gravitysim.core.*;
import com.gravitysim.renderer.*;

public class Main{
    public static void main(String[] args){
        Simulation sim = new Simulation();
        Renderer renderer = Renderer.get();
        ArrayList<SimBody> bodies = new ArrayList<>();
        double SCALE = 1e10;

        // sun
        bodies.add(new SimBody(new Body(new Vector3D(0,0,0), new Vector3D(0,0,0), 1.989e30, 5), 
                               new BodyRenderer(new Vector3f(0,0,0), 5.0f)));
        // mercury
        bodies.add(new SimBody(new Body(new Vector3D(5.79e10,0,0), new Vector3D(0,0,47400), 3.285e23, 0.5),
                               new BodyRenderer(new Vector3f(5.79e10f,0,0), 0.5f)));
        // venus
        bodies.add(new SimBody(new Body(new Vector3D(1.082e11,0,0), new Vector3D(0,0,35000), 4.867e24, 0.8), 
                               new BodyRenderer(new Vector3f(1.082e11f,0,0), 1.3f)));
        // earth
        bodies.add(new SimBody(new Body(new Vector3D(1.496e11,0,0), new Vector3D(0,0,29800), 5.972e24,  1.0),
                               new BodyRenderer(new Vector3f(1.496e11f,0,0), 1.3f)));
        // mars
        bodies.add(new SimBody(new Body(new Vector3D(2.279e11,0,0), new Vector3D(0,0,24100), 6.390e23, 0.6), 
                               new BodyRenderer(new Vector3f(2.279e11f,0,0), 1.1f)));
        // jupiter
        bodies.add(new SimBody(new Body(new Vector3D(7.786e11,0,0), new Vector3D(0,0,13100), 1.898e27, 3.0),
                               new BodyRenderer(new Vector3f(7.786e11f,0,0), 3.0f)));
        // saturn
        bodies.add(new SimBody(new Body(new Vector3D(1.434e12,0,0), new Vector3D(0,0,9700), 5.683e26,  2.5),
                               new BodyRenderer(new Vector3f(1.434e12f,0,0), 2.5f)));
        // uranus
        bodies.add(new SimBody(new Body(new Vector3D(2.871e12,0,0), new Vector3D(0,0,6800), 8.681e25,  2.0),
                               new BodyRenderer(new Vector3f(2.871e12f,0,0), 2.0f)));
        // neptun
        bodies.add(new SimBody(new Body(new Vector3D(4.495e12,0,0), new Vector3D(0,0,5400), 1.024e26, 2.0),
                               new BodyRenderer(new Vector3f(4.495e12f,0,0), 2.0f)));
       
        for (SimBody sb : bodies) {
            sim.bodies.add(sb.body);
            Renderer.get().bodies.add(sb.renderer);
        }

        sim.initSim();
        renderer.init();

        double lastFrameTime = glfwGetTime();
        while(!glfwWindowShouldClose(Renderer.get().glfwWindow)){
            double now       = glfwGetTime();
            float  dt = (float) (now - lastFrameTime);
            lastFrameTime    = now;

            sim.updatePos();
            for(SimBody sb : bodies){
                sb.renderer.setPosition((float) (sb.body.position.x / SCALE), 
                                        (float) (sb.body.position.y / SCALE), 
                                        (float) (sb.body.position.z / SCALE));
            }
            renderer.drawFrame(dt);
        }
        renderer.cleanup();
    }
}