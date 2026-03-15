package com.gravitysim.core;

public class Body{
    public Vector3D pos;
    public Vector3D velo;
    public Vector3D accl;
    public final double mass;
    public double radius;

    public Body(Vector3D pos, Vector3D velo, double mass, double radius){
        this.pos = pos;
        this.velo = velo;
        this.mass = mass;
        this.radius = radius;
        this.accl = new Vector3D(0,0,0);
    }
    void applyForce(Vector3D f){  
        this.accl = f.scale(1.0/mass);
    }

    void setPos(Vector3D pos){
        this.pos = pos;
    }
    public Vector3D getPos(){
        return pos;
    }
    void setVelo(Vector3D velo){
        this.velo = velo;
    }
    Vector3D getVelo(){
        return velo;
    }
    public double getRadius(){
        return radius;
    }
}