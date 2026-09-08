package com.nbodysim.scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.nbodysim.core.Vector3D;
import com.nbodysim.physics.Body;


 // A central mass with n orbiting bodies in a thin disc, seeded for reproducibility.
 /*
  <p>Exists for the scaling benchmark, not for display: at the body counts this
  is used with, the renderer issues one draw call per body and becomes the
  bottleneck long before the physics does.
*/

public class RandomDisc implements Scene {

    private static final double G = 6.674e-11;
    private static final double CENTRAL_MASS = 1e33;
    private static final double INNER_RADIUS = 5e11;
    private static final double OUTER_RADIUS = 5e12;
    private static final double THICKNESS = 2e11;

    private final int n;
    private final long seed;

    public RandomDisc(int n, long seed) {
        this.n = n;
        this.seed = seed;
    }

    @Override
    public String name() {
        return "disc";
    }

    @Override
    public double dt() {
        return 86400;
    }

    @Override
    public double renderScale() {
        return 1e11;
    }

    @Override
    public List<BodySpec> build() {
        Random random = new Random(seed);
        List<BodySpec> specs = new ArrayList<>(n + 1);
        specs.add(new BodySpec(
                new Body(new Vector3D(0, 0, 0), new Vector3D(0, 0, 0), CENTRAL_MASS, 5),
                5.0f, 1.0f, 0.93f, 0.55f, true));

        for (int i = 0; i < n; i++) {
            double radius = INNER_RADIUS + random.nextDouble() * (OUTER_RADIUS - INNER_RADIUS);
            double angle = random.nextDouble() * 2 * Math.PI;
            double height = (random.nextDouble() - 0.5) * THICKNESS;
            double speed = Math.sqrt(G * CENTRAL_MASS / radius);

            Body body = new Body(
                    new Vector3D(radius * Math.cos(angle), height, radius * Math.sin(angle)),
                    new Vector3D(-speed * Math.sin(angle), 0, speed * Math.cos(angle)),
                    1e24 + random.nextDouble() * 9e24,
                    1);
            specs.add(new BodySpec(body, 0.4f, 0.75f, 0.78f, 0.9f));
        }
        return specs;
    }
}