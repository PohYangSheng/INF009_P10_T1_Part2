package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

// holds two things that collided with each other
public class CollisionPair {

    private final iCollidable entityA;
    private final iCollidable entityB;

    // constructor
    public CollisionPair(iCollidable entityA, iCollidable entityB) {
        if (entityA == null || entityB == null)
            throw new IllegalArgumentException("Collision entities cannot be null");
        this.entityA = entityA;
        this.entityB = entityB;
    }

    // getter for entity a
    public iCollidable getEntityA() {
        return entityA;
    }
    // getter for entity b
    public iCollidable getEntityB() {
        return entityB;
    }
}
