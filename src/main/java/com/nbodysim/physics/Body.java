package com.nbodysim.physics;

import com.nbodysim.core.Vector3D;

public class Body {
    public Vector3D position;
    public Vector3D velocity;
    public Vector3D acceleration;
    public final double mass;
    public final double radius;

    public Body(Vector3D position, Vector3D velocity, double mass, double radius) {
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
        this.radius = radius;
        this.acceleration = new Vector3D(0, 0, 0);
    }
}