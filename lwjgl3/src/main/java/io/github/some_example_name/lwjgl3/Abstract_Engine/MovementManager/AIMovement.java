package io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iMovable;

// base class for AI movement
public class AIMovement<T extends iMovable> extends Movement<T> {

    private float vx;
    private float vy;

    // constructor
    public AIMovement(T entity, float vx, float vy) {
        super(entity);
        this.vx = vx;
        this.vy = vy;
    }

    // set velocity
    public void setVelocity(float vx, float vy) {
        this.vx = vx; this.vy = vy;
    }
    // getter for vx
    public float getVx() {
        return vx;
    }
    // getter for vy
    public float getVy() {
        return vy;
    }

    // update movement for this frame
    @Override
    public void move(T entity, float deltaTime) {
        if (entity == null) return;
        entity.setVelocity(vx, vy);
    }
}
