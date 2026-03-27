package com.gravitysim;

import org.joml.Vector3f;
import com.gravitysim.core.*;
import com.gravitysim.physics.*;
import com.gravitysim.renderer.*;

class Main{
    static void main(String[] args){
        Renderer window = Renderer.get();
        BodyRenderer body1 = new BodyRenderer(new Vector3f(0,0,0), 1.0f);
        BodyRenderer body2 = new BodyRenderer(new Vector3f(3,0,0), 0.3f);
        BodyRenderer body3 = new BodyRenderer(new Vector3f(7,0,2), 0.3f);
        BodyRenderer body4 = new BodyRenderer(new Vector3f(11,0,10), 0.3f);
        BodyRenderer body5 = new BodyRenderer(new Vector3f(5,0,4), 0.3f);
        Renderer.get().bodies.add(body1);
        Renderer.get().bodies.add(body2);
        Renderer.get().bodies.add(body3);
        Renderer.get().bodies.add(body4);
        Renderer.get().bodies.add(body5);
        window.run();
    }
}