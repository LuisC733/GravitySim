package physics;

import core.Vector2D;
import core.Body;

public class GravityCalc{
    private final double G = 6.674 * Math.pow(10, -11);

    public Vector2D calculateForce(Body a, Body b){
        Vector2D vecDisp = a.pos.sub(b.pos);
        double r = vecDisp.magnitude();
        double F = G * (a.mass * b.mass) / Math.pow(r, 2);
        Vector2D vecUnit = vecDisp.normalize();

        return vecUnit.scale(F);
    }
}