package io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager;

/**
 * Interface for entities that can move.
 *
 * Interface Segregation Principle (ISP) – only movement-related contract.
 *
 * OOP: Abstraction – consumers (MovementManager, BoundaryCollisionDetector)
 *   depend on this interface, never on concrete entity classes.
 *
 * SOLID DIP – BoundaryCollisionDetector<T extends iMovable> can query
 *   position and velocity through this interface without casting to
 *   MovableEntity, keeping the engine free of concrete dependencies.
 */
public interface iMovable {
    // ── Velocity ──────────────────────────────────────────────────────────
    void  setVelocity(float vx, float vy);
    void  stop();
    float getSpeed();
    float getVx();
    float getVy();

    // ── Position ──────────────────────────────────────────────────────────
    float getX();
    float getY();
    void  setPosition(float x, float y);
}
