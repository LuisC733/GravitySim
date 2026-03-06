package core;

class Body{
    public Vector pos;
    public Vector velo;
    public Vector accl;
    public final double mass;
    public double radius;

    public Body(Vector pos, Vector velo, double mass, double radius){
        this.pos = pos;
        this.velo = velo;
        this.mass = mass;
        this.radius = radius;
        this.accl = new Vector(0,0);
    }
    void applyForce(Vector f){
        this.accl = f.scale(1.0/mass);
    }
}