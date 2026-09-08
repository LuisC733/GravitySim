package com.nbodysim.scenes;

/** Maps command line names to scenes. */
public final class Scenes {

    private Scenes() {
    }

    public static Scene byName(String name) {
        return switch (name) {
            case "solar" -> new SolarSystem();
            case "eight" -> new FigureEight();
            case "disc" -> new RandomDisc(500, 42);
            default -> throw new IllegalArgumentException(
                    "unknown scene: " + name + " (known: solar, eight, disc)");
        };
    }
}