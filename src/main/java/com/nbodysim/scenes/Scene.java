package com.nbodysim.scenes;

import java.util.List;

public interface Scene {

    // Identifier used on the command line
    String name();

    // Integration step in seconds
    double dt();

    // Divisor mapping metres to OpenGL world units
    double renderScale();

    List<BodySpec> build();
}