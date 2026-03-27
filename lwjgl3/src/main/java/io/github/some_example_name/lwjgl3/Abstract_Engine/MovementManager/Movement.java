package io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iMovable;

// base class for all movement types
public abstract class Movement<T extends iMovable> {

    protected T entity;

    // constructor
    protected Movement(T entity) {
        this.entity = entity;
    }

    // getter for entity
    public T  getEntity() {
        return entity;
    }
    // setter for entity
    public void setEntity(T entity) {
        this.entity = entity;
    }

    public abstract void move(T entity, float deltaTime);
}
