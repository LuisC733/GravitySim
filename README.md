# N-Body Gravitational Simulation

A 2D gravitational simulation written in Java, built as a self-directed learning project to explore Newtonian mechanics, numerical integration, and object-oriented design.

---

## About

This project simulates the gravitational interaction between multiple bodies in 2D space using Newton's law of universal gravitation. Each body exerts a force on every other body, and their positions and velocities are updated over time using the **symplectic Euler method** — a simple but energy-conserving integration scheme well-suited for orbital mechanics.

The project is planned in two stages:

- **Stage 1 (current):** 2D simulation rendered with Java2D
- **Stage 2 (planned):** Extension to 3D using LWJGL (OpenGL binding for Java)

---

## Architecture

The codebase is organized into three packages with strict separation of concerns:

```
src/main/java/
├── core/
│   ├── Body.java          # Data container: position, velocity, acceleration, mass
│   └── Simulation.java    # Main loop: applies forces and integrates motion
|   |__ Vector2D.java      # Immutable 2D vector math (add, sub, scale, magnitude, normalize)
├── physics/
│   └── GravityCalculator.java  # Computes pairwise gravitational forces (Newton's law)
└── renderer/
    └── SimulationPanel.java    # Java2D rendering (Stage 1)
```

### Key design decisions

- **`Vector2D` is immutable** — all operations return a new vector, avoiding accidental mutation
- **`GravityCalculator.calculateForce(a, b)`** returns the force on `a` from `b`; Newton's third law (equal and opposite force on `b`) is derived via vector inversion
- **Symplectic Euler integration** — velocity is updated before position, which conserves energy better than standard Euler for oscillatory systems like orbits
- **`G` as a class attribute** on `GravityCalculator` — makes it easy to tune for simulation scale

---

## Build & Run

**Requirements:** Java 17+, Maven

```bash
# Compile
mvn compile

# Run
mvn exec:java
```

---

## What I Learned

This project was built alongside learning both physics and Java OOP from scratch:

- **Newtonian gravity** — deriving force from the displacement vector between two bodies; the same vector gives both distance (`magnitude()`) and direction (`normalize()`)
- **Newton's third law** in code — computing force once and inverting it avoids redundant calculation
- **Numerical integration** — the difference between Euler and symplectic Euler, and why it matters for stable orbits
- **Immutable value objects** — why `Vector2D` being immutable makes the simulation logic safer and easier to reason about
- **Java pitfalls** — e.g. integer division (`1/mass` vs `1.0/mass`) causing silent bugs

---