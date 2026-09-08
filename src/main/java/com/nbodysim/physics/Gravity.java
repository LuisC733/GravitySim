package com.nbodysim.physics;

import com.nbodysim.core.Vector3D;

public class Gravity {
    private static final double G = 6.674e-11;
    private static final double epsilon = 1e9;

    public Vector3D calculateForce(Body a, Body b) {
        Vector3D vecDisp = b.position.sub(a.position);
        double s = vecDisp.x * vecDisp.x + vecDisp.y * vecDisp.y + vecDisp.z * vecDisp.z + epsilon * epsilon;
        double F = G * (a.mass * b.mass) / (s * Math.sqrt(s));
        return vecDisp.scale(F);
    }
}