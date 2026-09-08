package com.nbodysim.core;

import com.nbodysim.physics.Body;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class SimulationTest {

    private static final double G = 6.674e-11;
    private static final double EPSILON = 1e9;
    private static final double SUN_MASS = 1.989e30;
    private static final double ORBIT_RADIUS = 1.496e11;
    private static final int STEPS_PER_ORBIT = 8766; // ein Erdjahr bei SIM_DT = 3600

    @Test
    void updatePos_circularOrbit_keepsSeparationConstant() {
        var sim = new Simulation();
        double circularSpeed = Math.sqrt(G * SUN_MASS / ORBIT_RADIUS);
        sim.bodies.add(new Body(new Vector3D(0, 0, 0), new Vector3D(0, 0, 0), SUN_MASS, 1));
        sim.bodies.add(new Body(new Vector3D(ORBIT_RADIUS, 0, 0),
                new Vector3D(0, circularSpeed, 0), 5.972e24, 1));
        sim.initSim();

        double minSeparation = Double.MAX_VALUE;
        double maxSeparation = 0;
        for (int step = 0; step < STEPS_PER_ORBIT; step++) {
            sim.updatePos();
            double separation = sim.bodies.get(1).position
                    .sub(sim.bodies.get(0).position).magnitude();
            minSeparation = Math.min(minSeparation, separation);
            maxSeparation = Math.max(maxSeparation, separation);
        }

        double spread = (maxSeparation - minSeparation) / ORBIT_RADIUS;
        assertTrue(spread < 3e-4,
                "orbit radius varied by relative " + spread + " over one orbit");
    }
    private double totalEnergy(List<Body> bodies) {
        double kinetic = 0;
        for (Body b : bodies) {
            double v = b.velocity.magnitude();
            kinetic += 0.5 * b.mass * v * v;
        }
        double potential = 0;
        for (int i = 0; i < bodies.size(); i++) {
            for (int j = i + 1; j < bodies.size(); j++) {
                Body a = bodies.get(i);
                Body b = bodies.get(j);
                double r = a.position.sub(b.position).magnitude();
                potential -= G * a.mass * b.mass / Math.sqrt(r * r + EPSILON * EPSILON);
            }
        }
        return kinetic + potential;
    }

    @Test
    void updatePos_over1000Steps_conservesTotalEnergyWithinBound() {
        Simulation sim = new Simulation();
        sim.bodies.add(new Body(new Vector3D(0, 0, 0), new Vector3D(0, 0, 0), 1.989e30, 1));
        sim.bodies.add(new Body(new Vector3D(1.5e11, 0, 0), new Vector3D(0, 29780, 0), 5.97e24, 1));
        sim.bodies.add(new Body(new Vector3D(-2.28e11, 0, 0), new Vector3D(0, -24070, 0), 6.42e23, 1));
        sim.initSim();

        double initialEnergy = totalEnergy(sim.bodies);
        double maxRelativeDrift = 0;

        for (int step = 0; step < 1000; step++) {
            sim.updatePos();
            double drift = Math.abs((totalEnergy(sim.bodies) - initialEnergy) / initialEnergy);
            maxRelativeDrift = Math.max(maxRelativeDrift, drift);
        }

        assertTrue(maxRelativeDrift < 1e-4,
                "energy drifted by relative " + maxRelativeDrift + " over 1000 steps");
    }
}
