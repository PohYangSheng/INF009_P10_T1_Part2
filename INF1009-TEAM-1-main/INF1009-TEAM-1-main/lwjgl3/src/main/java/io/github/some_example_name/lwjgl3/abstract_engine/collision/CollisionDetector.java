package io.github.some_example_name.lwjgl3.abstract_engine.collision;

/**
 * Base abstract class for all collision detection mechanisms.
 * Defines the core functionality needed for detecting and resolving collisions.
 */
public abstract class CollisionDetector {
    /**
     * Checks if a collision is currently occurring.
     * 
     * @return true if a collision is detected, false otherwise
     */
    public abstract boolean checkCollision();
    
    /**
     * Resolves a detected collision by modifying entity states.
     * Called after checkCollision() returns true.
     */
    public abstract void resolveCollision();
}