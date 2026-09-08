package com.nbodysim.scenes;

import java.util.List;

import com.nbodysim.core.Vector3D;
import com.nbodysim.physics.Body;

public class SolarSystem implements Scene {

    @Override
    public String name() {
        return "solar";
    }

    @Override
    public double dt() {
        return 3600;
    }

    @Override
    public double renderScale() {
        return 1e10;
    }

    @Override
    public List<BodySpec> build() {
        return List.of(
                planet(0, 0, 1.989e30, 5, 5.0f, 1.00f, 0.93f, 0.55f, true),
                planet(5.79e10, 47400, 3.285e23, 0.5, 0.5f, 0.70f, 0.65f, 0.60f, false),
                planet(1.082e11, 35000, 4.867e24, 0.8, 1.3f, 0.90f, 0.75f, 0.40f, false),
                planet(1.496e11, 29800, 5.972e24, 1.0, 1.3f, 0.20f, 0.50f, 1.00f, false),
                planet(2.279e11, 24100, 6.390e23, 0.6, 1.1f, 0.80f, 0.30f, 0.15f, false),
                planet(7.786e11, 13100, 1.898e27, 3.0, 3.0f, 0.80f, 0.60f, 0.45f, false),
                planet(1.434e12, 9700, 5.683e26, 2.5, 2.5f, 0.85f, 0.75f, 0.50f, false),
                planet(2.871e12, 6800, 8.681e25, 2.0, 2.0f, 0.50f, 0.85f, 0.90f, false),
                planet(4.495e12, 5400, 1.024e26, 2.0, 2.0f, 0.20f, 0.40f, 0.90f, false));
    }

    private static BodySpec planet(double x, double vz, double mass, double bodyRadius,
            float renderRadius, float r, float g, float b, boolean emissive) {
        Body body = new Body(new Vector3D(x, 0, 0), new Vector3D(0, 0, vz), mass, bodyRadius);
        return new BodySpec(body, renderRadius, r, g, b, emissive);
    }
}