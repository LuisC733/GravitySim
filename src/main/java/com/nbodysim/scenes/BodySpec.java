package com.nbodysim.scenes;

import com.nbodysim.physics.Body;

public record BodySpec(Body body, float renderRadius, float red, float green, float blue,
        boolean emissive) {

    public BodySpec(Body body, float renderRadius, float red, float green, float blue) {
        this(body, renderRadius, red, green, blue, false);
    }
}
