package physics;

public class GravityCalc{
     private final double G = 6,674 * Math.pow(10, -11);

    public Vector calculateForce(Body a, Body b){
        Vector vec_disp = a.pos - b.pos
        double r = vec_disp.magnitude();
        double F = G * (a.mass * b.mass) / Math.pow(r, 2);
        Vector vec_unit = vec_disp.normalize();

        return Vector vec_force = vec_unit.scale(F);
    }
}