package com.nbodysim.core;

import java.util.ArrayList;
import com.nbodysim.physics.Body;

public class Simulation{

    public ArrayList<Body> bodies = new ArrayList<>();
    public ArrayList<Vector3D> listOfForce = new ArrayList<>();
    Octree octree = new Octree();
    Octree.Node root = octree.new Node(new Vector3D(0, 0, 0), 4000);
    double dt = 3600;

    public void initSim(){
        accumulateForces();
        for(int i = 0; i < bodies.size(); i++){
            Body body = bodies.get(i);
            body.acceleration = listOfForce.get(i).scale(1.0 / body.mass);
            body.velocity = body.velocity.sub(body.acceleration.scale(0.5 * dt));
        }
    }
    private void accumulateForces(){
        int length = bodies.size();
        root = octree.new Node(new Vector3D(0, 0, 0), 4000);
        
        if(listOfForce.size() == 0){
            for(int i = 0; i < length; i++){
                listOfForce.add(new Vector3D(0, 0, 0));
            }
        }
        else{
            for(int i = 0; i < length; i++){
                listOfForce.set(i, new Vector3D(0, 0, 0));
            }
        }
        for(Body body : bodies){
            octree.insert(root, body);
        }
        for(int i = 0; i < length; i++){
            listOfForce.set(i, (octree.traverse(root, bodies.get(i))));
        }
    }
    public void updatePos(){
        accumulateForces();
        for(int i = 0; i < bodies.size(); i++){
            Body body = bodies.get(i);
            body.velocity = body.velocity.add(body.acceleration.scale(dt));
            body.position = body.position.add(body.velocity.scale(dt));
            body.acceleration(listOfForce.get(i));
        }
    }
}