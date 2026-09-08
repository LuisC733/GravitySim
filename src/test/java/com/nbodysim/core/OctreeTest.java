package com.nbodysim.core;

import com.nbodysim.Config;
import com.nbodysim.physics.Body;
import com.nbodysim.physics.Gravity;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

public class OctreeTest {

    // Mirrors Gravity's private softening constants so potential energy stays
    // consistent with the force law used by the simulation.

    private final Octree octree = new Octree();
    private final Gravity gravity = new Gravity();

    private void assertClose(double expected, double actual) {
        double delta = Config.TOLERANCE * Math.max(1.0, Math.max(Math.abs(expected), Math.abs(actual)));
        assertEquals(expected, actual, delta);
    }

    private Vector3D bruteForceForce(Body target, List<Body> bodies) {
        Vector3D sum = new Vector3D(0, 0, 0);
        for (Body other : bodies) {
            if (other != target) {
                sum = sum.add(gravity.calculateForce(target, other));
            }
        }
        return sum;
    }

    @Test
    void traverse_withThetaZero_matchesBruteForceExactly() {
        List<Body> bodies = List.of(
                new Body(new Vector3D(0, 0, 0), new Vector3D(0, 0, 0), 5e24, 1),
                new Body(new Vector3D(1e11, 0, 0), new Vector3D(0, 0, 0), 3e23, 1),
                new Body(new Vector3D(-6e10, 8e10, 0), new Vector3D(0, 0, 0), 7e22, 1),
                new Body(new Vector3D(2e10, -4e10, 5e10), new Vector3D(0, 0, 0), 1e23, 1),
                new Body(new Vector3D(-9e10, -3e10, -2e10), new Vector3D(0, 0, 0), 4e23, 1)
        );

        double halfWidth = 0;
        for (Body b : bodies) {
            halfWidth = Math.max(halfWidth, Math.abs(b.position.x));
            halfWidth = Math.max(halfWidth, Math.abs(b.position.y));
            halfWidth = Math.max(halfWidth, Math.abs(b.position.z));
        }
        Octree.Node root = new Octree.Node(new Vector3D(0, 0, 0), halfWidth * 1.1);
        for (Body b : bodies) {
            octree.insert(root, b);
        }

        for (Body body : bodies) {
            Vector3D expected = bruteForceForce(body, bodies);
            Vector3D actual = octree.traverse(root, body, 0.0);

            assertClose(expected.x, actual.x);
            assertClose(expected.y, actual.y);
            assertClose(expected.z, actual.z);
        }
    }
}