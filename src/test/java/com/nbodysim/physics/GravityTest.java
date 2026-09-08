package com.nbodysim.physics;

import com.nbodysim.Config;
import com.nbodysim.core.Vector3D;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class GravityTest {

    private final Gravity gravity = new Gravity();

    private Body bodyAt(double x, double mass) {
        return bodyAt(x, 0, mass);
    }

    private Body bodyAt(double x, double y, double mass) {
        return new Body(new Vector3D(x, y, 0), new Vector3D(0, 0, 0), mass, 1);
    }

    private void assertClose(double expected, double actual) {
        double delta = Config.TOLERANCE * Math.max(1.0, Math.max(Math.abs(expected), Math.abs(actual)));
        assertEquals(expected, actual, delta);
    }

    private void assertVectorEquals(Vector3D exp, Vector3D vecActual) {
        assertClose(exp.x, vecActual.x);
        assertClose(exp.y, vecActual.y);
        assertClose(exp.z, vecActual.z);
    }

    @Test
    void calculateForce_bodyToTheRight_forcePointsRight() {
        var body = bodyAt(0, 2e10);
        var bodyToTheRight = bodyAt(1000, 3e10);

        var force = gravity.calculateForce(body, bodyToTheRight);

        assertTrue(force.x > 0);
        assertClose(0, force.y);
        assertClose(0, force.z);
    }

    @Test
    void calculateForce_bodyToTheLeft_forcePointsLeft() {
        var body = bodyAt(0, 2e10);
        var bodyToTheLeft = bodyAt(-1000, 3e10);

        var force = gravity.calculateForce(body, bodyToTheLeft);

        assertTrue(force.x < 0);
        assertClose(0, force.y);
        assertClose(0, force.z);
    }

    @Test
    void calculateForce_diagonalDirectionHasBothComponents() {
        var body = bodyAt(0, 0, 2e10);
        var diagonalBody = bodyAt(1000, 1000, 3e10);

        var force = gravity.calculateForce(body, diagonalBody);

        assertTrue(force.x > 0);
        assertTrue(force.y > 0);
        assertClose(force.x, force.y);
        assertClose(0, force.z);
    }

    @Test
    void calculateForce_forcesAreOppositeWhenBodiesAreSwapped() {
        var bodyA = bodyAt(0, 2e10);
        var bodyB = bodyAt(1000, 3e10);

        var forceOnA = gravity.calculateForce(bodyA, bodyB);
        var forceOnB = gravity.calculateForce(bodyB, bodyA);

        assertVectorEquals(forceOnA.invert(), forceOnB);
    }

    @Test
    void calculateForce_doublingMassOfSecondBodyDoublesForceMagnitude() {
        var bodyA = bodyAt(0, 2e10);
        var bodyB = bodyAt(1000, 3e10);
        var bodyBWithDoubleMass = bodyAt(1000, 6e10);

        var force = gravity.calculateForce(bodyA, bodyB);
        var forceWithDoubleMass = gravity.calculateForce(bodyA, bodyBWithDoubleMass);

        assertClose(force.magnitude() * 2, forceWithDoubleMass.magnitude());
    }

    @Test
    void calculateForce_doublingMassOfFirstBodyDoublesForceMagnitude() {
        var bodyA = bodyAt(0, 2e10);
        var bodyAWithDoubleMass = bodyAt(0, 4e10);
        var bodyB = bodyAt(1000, 3e10);

        var force = gravity.calculateForce(bodyA, bodyB);
        var forceWithDoubleMass = gravity.calculateForce(bodyAWithDoubleMass, bodyB);

        assertClose(force.magnitude() * 2, forceWithDoubleMass.magnitude());
    }
}
