package com.gravitysim.physics;

import com.gravitysim.core.Vector3D;
import com.gravitysim.core.Body;

public class Gravity{
    private final double G = 6.674e-11;

    public Vector3D calculateForce(Body a, Body b){
        Vector3D vecDisp = b.pos.sub(a.pos);
        double r = vecDisp.magnitude();
        double F = G * (a.mass * b.mass) / Math.pow(r, 2);
        Vector3D vecUnit = vecDisp.normalize();

        return vecUnit.scale(F);
    }
}