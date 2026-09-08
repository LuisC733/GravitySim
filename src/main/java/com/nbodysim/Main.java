package com.nbodysim;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;

import org.joml.Vector3f;

import com.nbodysim.core.SimBody;
import com.nbodysim.core.Simulation;
import com.nbodysim.renderer.BodyRenderer;
import com.nbodysim.renderer.Renderer;
import com.nbodysim.scenes.BodySpec;
import com.nbodysim.scenes.Scene;
import com.nbodysim.scenes.Scenes;

public class Main {

    public static void main(String[] args) {
        Scene scene = Scenes.byName(args.length > 0 ? args[0] : "solar");

        // The spacetime grid reads Config.SCALE directly, so it has to be set
        // before the renderer starts. See README for why this is still a global.
        Config.SCALE = scene.renderScale();

        Simulation sim = new Simulation();
        Renderer renderer = Renderer.get();
        ArrayList<SimBody> bodies = new ArrayList<>();
        boolean lightSourceSet = false;

        for (BodySpec spec : scene.build()) {
            Vector3f color = new Vector3f(spec.red(), spec.green(), spec.blue());
            Vector3f start = new Vector3f(
                    (float) (spec.body().position.x / scene.renderScale()),
                    (float) (spec.body().position.y / scene.renderScale()),
                    (float) (spec.body().position.z / scene.renderScale()));

            BodyRenderer bodyRenderer = spec.emissive()
                    ? new BodyRenderer(start, spec.renderRadius(), color, true)
                    : new BodyRenderer(start, spec.renderRadius(), color);
            if (spec.emissive() && !lightSourceSet) {
                renderer.setLightSource(bodyRenderer);
                lightSourceSet = true;
            }

            SimBody simBody = new SimBody(spec.body(), bodyRenderer);
            bodies.add(simBody);
            sim.bodies.add(simBody.body);
            renderer.bodies.add(simBody.renderer);
            renderer.physicsBodies.add(simBody.body);
        }

        sim.initSim();
        renderer.init();

        double lastFrameTime = glfwGetTime();
        while (!glfwWindowShouldClose(renderer.glfwWindow)) {
            double now = glfwGetTime();
            float frameTime = (float) (now - lastFrameTime);
            lastFrameTime = now;

            sim.updatePos(scene.dt());
            for (SimBody sb : bodies) {
                sb.renderer.setPosition(
                        (float) (sb.body.position.x / scene.renderScale()),
                        (float) (sb.body.position.y / scene.renderScale()),
                        (float) (sb.body.position.z / scene.renderScale()));
            }
            renderer.drawFrame(frameTime);
        }
        renderer.cleanup();
    }
}