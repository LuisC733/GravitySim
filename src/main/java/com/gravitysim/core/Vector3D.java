package com.gravitysim.core;

public class Vector3D{

    public final double x;
    public final double y;
    public final double z;

    public Vector3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D add(Vector3D other){
        return new Vector3D(this.x + other.x, this.y + other.y, this.z + other.z);
    }
    public Vector3D scale(double factor){
        return new Vector3D(this.x * factor, this.y * factor, this.z * factor);
    }
    public double magnitude(){
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    public Vector3D normalize(){
        return new Vector3D(this.x / magnitude(), this.y / magnitude(), this.z / magnitude());
    }
    public Vector3D sub(Vector3D other){
        return new Vector3D(this.x - other.x, this.y - other.y, this.z - other.z);
    }
    public Vector3D invert(){
        return new Vector3D(this.x * -1, this.y * -1, this.z * -1);
    }
}