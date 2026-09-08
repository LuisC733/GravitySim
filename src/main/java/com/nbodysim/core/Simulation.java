package com.nbodysim.core;

import java.util.ArrayList;
import com.nbodysim.Config;
import com.nbodysim.physics.Body;

public class Simulation {

    public ArrayList<Body> bodies = new ArrayList<>();
    public ArrayList<Vector3D> listOfForce = new ArrayList<>();
    Octree octree = new Octree();
    Octree.Node root;

    public void initSim() {
        accumulateForces();
        for (int i = 0; i < bodies.size(); i++) {
            Body body = bodies.get(i);
            body.acceleration = listOfForce.get(i).scale(1.0 / body.mass);
        }
    }

    private void accumulateForces() {
        int length = bodies.size();
        double halfWidth = 0;
        for (Body b : bodies) {
            halfWidth = Math.max(halfWidth, Math.abs(b.position.x));
            halfWidth = Math.max(halfWidth, Math.abs(b.position.y));
            halfWidth = Math.max(halfWidth, Math.abs(b.position.z));
        }
        root = new Octree.Node(new Vector3D(0, 0, 0), halfWidth * 1.1);

        if (listOfForce.size() == 0) {
            for (int i = 0; i < length; i++) {
                listOfForce.add(new Vector3D(0, 0, 0));
            }
        } else {
            for (int i = 0; i < length; i++) {
                listOfForce.set(i, new Vector3D(0, 0, 0));
            }
        }
        for (Body body : bodies) {
            octree.insert(root, body);
        }
        for (int i = 0; i < length; i++) {
            listOfForce.set(i, (octree.traverse(root, bodies.get(i))));
        }
    }

    public void updatePos() {
        for (int i = 0; i < bodies.size(); i++) {
            Body body = bodies.get(i);
            body.velocity = body.velocity.add(body.acceleration.scale(Config.SIM_DT * 0.5));
            body.position = body.position.add(body.velocity.scale(Config.SIM_DT));
        }
        accumulateForces();
        for (int i = 0; i < bodies.size(); i++) {
            Body body = bodies.get(i);
            body.acceleration = listOfForce.get(i).scale(1.0 / body.mass);
            body.velocity = body.velocity.add(body.acceleration.scale(0.5 * Config.SIM_DT));
        }
    }
}