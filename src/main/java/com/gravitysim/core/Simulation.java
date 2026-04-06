package com.gravitysim.core;

import java.util.ArrayList;
import com.gravitysim.physics.Gravity;
import com.gravitysim.physics.Body;

public class Simulation{

    public ArrayList<Body> bodies = new ArrayList<>();
    public ArrayList<Vector3D> listOfForce = new ArrayList<>();
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
        Gravity g = new Gravity();
        int length = bodies.size();

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
        for(int i = 0; i < length; i++){
            for(int j = i + 1; j < length; j++){
                Vector3D force = g.calculateForce(bodies.get(i), bodies.get(j));
                Vector3D invertedForce = force.invert();

                listOfForce.set(i, listOfForce.get(i).add(force));
                listOfForce.set(j, listOfForce.get(j).add(invertedForce));
            }
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