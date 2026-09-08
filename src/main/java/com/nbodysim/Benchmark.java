package com.nbodysim;

import java.util.ArrayList;
import java.util.List;

import com.nbodysim.core.Simulation;
import com.nbodysim.core.Vector3D;
import com.nbodysim.physics.Body;
import com.nbodysim.physics.Gravity;
import com.nbodysim.scenes.BodySpec;
import com.nbodysim.scenes.RandomDisc;

/**
 * Headless harness. Measures how the Barnes-Hut step scales with body count and
 * what the opening angle costs in accuracy.
 *
 * <p>Run with: mvn -q compile exec:exec -Dexec.mainClass=com.nbodysim.Benchmark
 */
public class Benchmark {

    private static final long SEED = 42;
    private static final int WARMUP_STEPS = 20;
    private static final int MEASURED_STEPS = 30;

    public static void main(String[] args) {
        scaling();
        System.out.println();
        thetaSweep(2000);
    }

    private static void scaling() {
        System.out.println("N      barnes-hut ms/step   brute force ms/step   speedup");
        for (int n : new int[] { 100, 250, 500, 1000, 2000, 5000, 10000, 20000, 50000 }) {
            double bh = timeBarnesHut(n);
            String brute = "-";
            String speedup = "-";
            if (n <= 10000) {
                double bf = timeBruteForce(n);
                brute = String.format("%.3f", bf);
                speedup = String.format("%.1fx", bf / bh);
            }
            System.out.printf("%-6d %-20.3f %-21s %s%n", n, bh, brute, speedup);
        }
    }

    private static double timeBarnesHut(int n) {
        Simulation sim = newSimulation(n);
        double dt = new RandomDisc(n, SEED).dt();
        sim.initSim();
        for (int i = 0; i < WARMUP_STEPS; i++) {
            sim.updatePos(dt);
        }
        long start = System.nanoTime();
        for (int i = 0; i < MEASURED_STEPS; i++) {
            sim.updatePos(dt);
        }
        return (System.nanoTime() - start) / 1e6 / MEASURED_STEPS;
    }

    private static double timeBruteForce(int n) {
        List<Body> bodies = newSimulation(n).bodies;
        for (int i = 0; i < 3; i++) {
            bruteForceAll(bodies);
        }
        int steps = n > 2000 ? 3 : 10;
        long start = System.nanoTime();
        for (int i = 0; i < steps; i++) {
            bruteForceAll(bodies);
        }
        return (System.nanoTime() - start) / 1e6 / steps;
    }

    private static void thetaSweep(int n) {
        Simulation sim = newSimulation(n);
        sim.theta = 0;
        sim.initSim();
        List<Vector3D> exact = new ArrayList<>(sim.listOfForce);

        System.out.println("theta   rms rel. error   max rel. error   ms/step");
        for (double theta : new double[] { 0.0, 0.1, 0.2, 0.3, 0.5, 0.7, 1.0, 1.5 }) {
            Simulation run = newSimulation(n);
            run.theta = theta;
            run.initSim();

            double sumSquares = 0;
            double worst = 0;
            for (int i = 0; i < exact.size(); i++) {
                double reference = exact.get(i).magnitude();
                if (reference == 0) {
                    continue;
                }
                double error = run.listOfForce.get(i).sub(exact.get(i)).magnitude() / reference;
                sumSquares += error * error;
                worst = Math.max(worst, error);
            }
            System.out.printf("%-7.1f %-16.3e %-16.3e %.3f%n",
                    theta, Math.sqrt(sumSquares / exact.size()), worst, timeAt(n, theta));
        }
    }

    private static double timeAt(int n, double theta) {
        Simulation sim = newSimulation(n);
        sim.theta = theta;
        double dt = new RandomDisc(n, SEED).dt();
        sim.initSim();
        for (int i = 0; i < WARMUP_STEPS; i++) {
            sim.updatePos(dt);
        }
        long start = System.nanoTime();
        for (int i = 0; i < MEASURED_STEPS; i++) {
            sim.updatePos(dt);
        }
        return (System.nanoTime() - start) / 1e6 / MEASURED_STEPS;
    }

    private static Simulation newSimulation(int n) {
        Simulation sim = new Simulation();
        for (BodySpec spec : new RandomDisc(n, SEED).build()) {
            sim.bodies.add(spec.body());
        }
        return sim;
    }

    private static void bruteForceAll(List<Body> bodies) {
        Gravity gravity = new Gravity();
        Vector3D total = new Vector3D(0, 0, 0);
        for (Body a : bodies) {
            Vector3D sum = new Vector3D(0, 0, 0);
            for (Body b : bodies) {
                if (a != b) {
                    sum = sum.add(gravity.calculateForce(a, b));
                }
            }
            total = total.add(sum);
        }
        if (total.magnitude() < 0) {
            throw new IllegalStateException();
        }
    }
}
