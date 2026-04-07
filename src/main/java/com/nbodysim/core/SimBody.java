package com.nbodysim.core;

import com.nbodysim.physics.Body;
import com.nbodysim.renderer.BodyRenderer;

public class SimBody {
    public Body body;
    public BodyRenderer renderer;

    public SimBody(Body body, BodyRenderer renderer){
        this.body = body;
        this.renderer = renderer;
    }
}
