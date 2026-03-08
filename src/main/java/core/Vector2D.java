package core;

public class Vector2D{

    public final double x;
    public final double y;

    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D other){
        return new Vector2D(this.x + other.x, this.y + other.y);
    }
    public Vector2D scale(double factor){
        return new Vector2D(this.x * factor, this.y * factor);
    }
    public double magnitude(){
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }
    public Vector2D normalize(){
        return new Vector2D(this.x / magnitude(), this.y / magnitude());
    }
    public Vector2D sub(Vector2D other){
        return new Vector2D(this.x - other.x, this.y - other.y);
    }
    public Vector2D invert(){
        return new Vector2D(this.x * -1, this.y * -1);
    }
}