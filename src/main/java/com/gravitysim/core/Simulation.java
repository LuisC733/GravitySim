package com.gravitysim.core;

import java.util.ArrayList;
import com.gravitysim.physics.Gravity;

public class Simulation{
    public ArrayList<Body> objects = new ArrayList<>();
    public ArrayList<Vector3D> listOfForce = new ArrayList<>();
    double dt = 10.0;
    
    public void updatePos(){
        Gravity g = new Gravity();
        int length = objects.size();

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


        // Accumulate gravitational forces for each body pair
        for(int i = 0; i < length; i++){
            for(int j = i + 1; j < length; j++){
                Vector3D force = g.calculateForce(objects.get(i), objects.get(j));
                Vector3D invertedForce = force.invert();

                listOfForce.set(i, listOfForce.get(i).add(force));
                listOfForce.set(j, listOfForce.get(j).add(invertedForce));
            }
        }

        for(int i = 0; i < length; i++){
            objects.get(i).accleration(listOfForce.get(i));
        }

        // symplectic euler
        for(int i = 0; i < length; i++){
            Vector3D newVelo = objects.get(i).velo.add(objects.get(i).accl.scale(dt));
            objects.get(i).setVelo(newVelo);
            Vector3D newPos = objects.get(i).pos.add(newVelo.scale(dt));
            objects.get(i).setPos(newPos);
        }
    }
}