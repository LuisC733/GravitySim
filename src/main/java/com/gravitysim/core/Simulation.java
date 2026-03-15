package com.gravitysim.core;

import java.util.ArrayList;
import com.gravitysim.physics.GravityCalc;

public class Simulation{
    public ArrayList<Body> objects = new ArrayList<>();
    public ArrayList<Vector3D> listOfForce = new ArrayList<>();
    double dt = 10000.0;
    
    public void updatePos(){
        GravityCalc g = new GravityCalc();
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

        for(int i = 0; i < length; i++){
            for(int j = i + 1; j < length; j++){
                Vector3D force = g.calculateForce(objects.get(i), objects.get(j));
                Vector3D invertedForce = force.invert();

                Vector3D test = listOfForce.get(i).add(force);
                listOfForce.set(i, test);

                Vector3D invertedTest = listOfForce.get(j).add(invertedForce);
                listOfForce.set(j, invertedTest);
            }
        }

        for(int i = 0; i < length; i++){
            objects.get(i).applyForce(listOfForce.get(i));
        }

        for(int i = 0; i < length; i++){
            Vector3D newVelo = objects.get(i).velo.add(objects.get(i).accl.scale(dt));
            objects.get(i).setVelo(newVelo);
            Vector3D newPos = objects.get(i).pos.add(newVelo.scale(dt));
            objects.get(i).setPos(newPos);
        }
    }
}