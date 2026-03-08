package core;

import java.util.ArrayList;
import physics.GravityCalc;

class Simulation{
    ArrayList<Body> objects = new ArrayList<>();
    ArrayList<Vector2D> listOfForce = new ArrayList<>();
    double dt = 0.01;
    
    void updatePos(){
        GravityCalc g = new GravityCalc();
        int length = objects.size();

        for(int i = 0; i < length; i++){
            listOfForce.add(new Vector2D(0, 0));
        }

        for(int i = 0; i < length; i++){
            for(int j = i + 1; j < length; j++){
                Vector2D force = g.calculateForce(objects.get(i), objects.get(j));
                Vector2D invertedForce = force.invert();

                Vector2D test = listOfForce.get(i).add(force);
                listOfForce.set(i, test);

                Vector2D invertedTest = listOfForce.get(j).add(invertedForce);
                listOfForce.set(j, invertedTest);
            }
        }

        for(int i = 0; i < length; i++){
            objects.get(i).applyForce(listOfForce.get(i));
        }

        for(int i = 0; i < length; i++){
            Vector2D newVelo = objects.get(i).velo.add(objects.get(i).accl.scale(dt));
            objects.get(i).setVelo(newVelo);
            Vector2D newPos = objects.get(i).pos.add(newVelo.scale(dt));
            objects.get(i).setPos(newPos);
        }
    }
}