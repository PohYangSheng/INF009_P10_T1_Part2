package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

import java.util.List;

/**
 * Abstract base for collision detection algorithms.
 *
 * Design Pattern: Strategy – each concrete subclass encapsulates a
 * different detection algorithm (AABB, circle, etc.).
 * SOLID: OCP – new detection strategies added without modifying this class.
 */
public abstract class CollisionDetector {

    /**
     * Detect all colliding pairs from the given list of collidables.
     * @param entities list of all collidable entities in the scene
     * @return list of CollisionPairs that are currently overlapping
     */
    public abstract List<CollisionPair> detectEntitiesList(List<iCollidable> entities);
}
