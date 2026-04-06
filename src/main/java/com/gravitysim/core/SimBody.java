package com.gravitysim.core;

import com.gravitysim.renderer.BodyRenderer;
import com.gravitysim.physics.Body;

public class SimBody {
    public Body body;
    public BodyRenderer renderer;

    public SimBody(Body body, BodyRenderer renderer){
        this.body = body;
        this.renderer = renderer;
    }
}
