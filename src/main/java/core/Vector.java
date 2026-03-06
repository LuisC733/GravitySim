package core;

public class Vector{

    public final double x;
    public final double y;

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector other){
        return new Vector(this.x + other.x, this.y + other.y);
    }
    public Vector scale(double factor){
        return new Vector(this.x * factor, this.y * factor);
    }
    public double magnitude(){
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }
    public Vector normalize(){
        return new Vector(this.x / magnitude(), this.y / magnitude());
    }
    public Vector sub(Vector other){
        return new Vector(this.x - other.x, this.y - other.y);
    }
    public Vector invert(){
        
    }
}