package io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iMovable;

/**
 * Abstract base for all movement strategies.
 *
 * Design Pattern: Strategy – each subclass encapsulates a different movement
 * algorithm (user-controlled vs AI-controlled).
 *
 * OOP: Generics – T must implement iMovable, ensuring type safety.
 *
 * @param <T> the type of movable entity this movement controls
 */
public abstract class Movement<T extends iMovable> {

    protected T entity;

    protected Movement(T entity) {
        this.entity = entity;
    }

    public T  getEntity()          { return entity; }
    public void setEntity(T entity){ this.entity = entity; }

    /**
     * Apply movement logic to the entity for this frame.
     * @param entity    the entity to move
     * @param deltaTime seconds since last frame
     */
    public abstract void move(T entity, float deltaTime);
}
