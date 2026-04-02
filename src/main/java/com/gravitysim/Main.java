package com.gravitysim;

import static org.lwjgl.glfw.GLFW.*;
import java.util.ArrayList;

import org.joml.Vector3f;

import com.gravitysim.core.*;
import com.gravitysim.renderer.*;

class Main{
    static void main(String[] args){
        Simulation sim = new Simulation();
        Renderer renderer = Renderer.get();
        ArrayList<SimBody> bodies = new ArrayList<>();

        // sun
        bodies.add(new SimBody(new Body(new Vector3D(0,0,0), new Vector3D(0,0,0), 1e12, 1.0), new BodyRenderer(new Vector3f(0,0,0), 1.0f)));
        // mercury
        bodies.add(new SimBody(new Body(new Vector3D(5,0,0), new Vector3D(0,0,1), 1e6,  0.3f), new BodyRenderer(new Vector3f(0,0,-5), 0.3f)));
        // venus
        bodies.add(new SimBody(new Body(new Vector3D(0,0,0), new Vector3D(0,0,0), 1e12, 1.0), new BodyRenderer(new Vector3f(0,0,0), 1.0f)));
        // earth
        bodies.add(new SimBody(new Body(new Vector3D(5,0,0), new Vector3D(0,0,1), 1e6,  0.3f), new BodyRenderer(new Vector3f(0,0,-5), 0.3f)));
        // mars
        bodies.add(new SimBody(new Body(new Vector3D(0,0,0), new Vector3D(0,0,0), 1e12, 1.0), new BodyRenderer(new Vector3f(0,0,0), 1.0f)));
        // saturn
        bodies.add(new SimBody(new Body(new Vector3D(5,0,0), new Vector3D(0,0,1), 1e6,  0.3f), new BodyRenderer(new Vector3f(0,0,-5), 0.3f)));
        // jupiter
        bodies.add(new SimBody(new Body(new Vector3D(0,0,0), new Vector3D(0,0,0), 1e12, 1.0), new BodyRenderer(new Vector3f(0,0,0), 1.0f)));
        // uranus
        bodies.add(new SimBody(new Body(new Vector3D(5,0,0), new Vector3D(0,0,1), 1e6,  0.3f), new BodyRenderer(new Vector3f(0,0,-5), 0.3f)));
        // neptun
        bodies.add(new SimBody(new Body(new Vector3D(0,0,0), new Vector3D(0,0,0), 1e12, 1.0), new BodyRenderer(new Vector3f(0,0,0), 1.0f)));
       
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
                sb.renderer.setPosition((float) sb.body.position.x, (float) sb.body.position.y, (float) sb.body.position.z);
            }
            renderer.drawFrame(dt);
        }
        renderer.cleanup();
    }
}