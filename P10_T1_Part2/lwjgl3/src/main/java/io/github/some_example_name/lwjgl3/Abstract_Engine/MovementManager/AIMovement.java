package io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iMovable;

/**
 * Simple AI movement strategy: moves the entity at a fixed velocity.
 * Game-specific AI (chasing, wandering) subclasses this.
 *
 * Design Pattern: Strategy – alternative movement algorithm to UserMovement.
 */
public class AIMovement<T extends iMovable> extends Movement<T> {

    private float vx;
    private float vy;

    public AIMovement(T entity, float vx, float vy) {
        super(entity);
        this.vx = vx;
        this.vy = vy;
    }

    public void setVelocity(float vx, float vy) { this.vx = vx; this.vy = vy; }
    public float getVx() { return vx; }
    public float getVy() { return vy; }

    @Override
    public void move(T entity, float deltaTime) {
        if (entity == null) return;
        entity.setVelocity(vx, vy);
    }
}
