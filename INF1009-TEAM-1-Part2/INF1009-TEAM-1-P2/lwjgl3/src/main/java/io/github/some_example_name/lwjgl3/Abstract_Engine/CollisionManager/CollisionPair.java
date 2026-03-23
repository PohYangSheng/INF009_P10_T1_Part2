package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

/**
 * Immutable value object representing two entities in collision.
 * OOP: Generics – typed pair ensures compile-time safety.
 */
public class CollisionPair {

    private final iCollidable entityA;
    private final iCollidable entityB;

    public CollisionPair(iCollidable entityA, iCollidable entityB) {
        if (entityA == null || entityB == null)
            throw new IllegalArgumentException("Collision entities cannot be null");
        this.entityA = entityA;
        this.entityB = entityB;
    }

    public iCollidable getEntityA() { return entityA; }
    public iCollidable getEntityB() { return entityB; }
}
