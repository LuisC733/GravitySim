package core;

public class Body{
    public Vector2D pos;
    public Vector2D velo;
    public Vector2D accl;
    public final double mass;
    public double radius;

    public Body(Vector2D pos, Vector2D velo, double mass, double radius){
        this.pos = pos;
        this.velo = velo;
        this.mass = mass;
        this.radius = radius;
        this.accl = new Vector2D(0,0);
    }
    void applyForce(Vector2D f){  
        this.accl = f.scale(1.0/mass);
    }

    void setPos(Vector2D pos){
        this.pos = pos;
    }
    Vector2D getPos(){
        return pos;
    }
    void setVelo(Vector2D velo){
        this.velo = velo;
    }
    Vector2D getVelo(){
        return velo;
    }
}