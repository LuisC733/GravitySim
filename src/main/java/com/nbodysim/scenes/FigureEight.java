package com.nbodysim.scenes;

import java.util.List;

import com.nbodysim.core.Vector3D;
import com.nbodysim.physics.Body;

// The Chenciner-Montgomery figure-eight: three equal masses chasing each other along a single closed curve.

public class FigureEight implements Scene {

    private static final double G = 6.674e-11;
    private static final double MASS_UNIT = 1e30;
    private static final double LENGTH_UNIT = 1e12;

    private static final double TIME_UNIT = Math.sqrt(LENGTH_UNIT * LENGTH_UNIT * LENGTH_UNIT / (G * MASS_UNIT));
    private static final double VELOCITY_UNIT = LENGTH_UNIT / TIME_UNIT;

    // One full period in normalised units
    private static final double PERIOD = 6.32591398;

    private static final double X = 0.97000436;
    private static final double Y = -0.24308753;
    private static final double VX = 0.93240737;
    private static final double VY = 0.86473146;

    // One period in seconds, useful for benchmarks and for choosing dt
    public static double periodSeconds() {
        return PERIOD * TIME_UNIT;
    }

    @Override
    public String name() {
        return "eight";
    }

    @Override
    public double dt() {
        return periodSeconds() / 4000;
    }

    @Override
    public double renderScale() {
        return LENGTH_UNIT / 3;
    }

    @Override
    public List<BodySpec> build() {
        return List.of(
                body(X, Y, VX / 2, VY / 2, 1.0f, 0.45f, 0.35f, false),
                body(-X, -Y, VX / 2, VY / 2, 0.40f, 0.75f, 1.0f, false),
                body(0, 0, -VX, -VY, 0.65f, 1.0f, 0.55f, true));
    }

    private static BodySpec body(double x, double y, double vx, double vy, float r, float g, float b, boolean emissive) {
        Body body = new Body(
                new Vector3D(x * LENGTH_UNIT, y * LENGTH_UNIT, 0),
                new Vector3D(vx * VELOCITY_UNIT, vy * VELOCITY_UNIT, 0),
                MASS_UNIT, 1.0);
        return new BodySpec(body, 0.6f, r, g, b, emissive);
    }
}