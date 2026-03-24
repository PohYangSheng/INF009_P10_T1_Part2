package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

/**
 * Abstract Strategy for resolving detected collisions.
 *
 * WHY ABSTRACT: The engine cannot know HOW a game resolves a collision
 * (push-back, damage, pickup). The game layer overrides resolve() with
 * game-specific dispatch while the engine calls it generically.
 *
 * Design Pattern: Strategy – CollisionManager holds a CollisionResolution
 *   reference; the game layer supplies a concrete subclass at runtime via
 *   CollisionManager.setResolution(). Engine is open for extension,
 *   closed for modification (OCP).
 *
 * OOP Polymorphism – CollisionManager calls resolution.resolve(pair)
 *   without knowing the concrete type. Runtime dispatch decides behaviour.
 *
 * SOLID:
 *   SRP – defines only the resolution contract.
 *   OCP – new behaviours added by subclassing, not editing this class.
 *   DIP – CollisionManager depends on this abstraction, not concrete class.
 */
public abstract class CollisionResolution {

    /**
     * Resolve a detected collision between the two entities in the pair.
     * Subclasses decide the outcome (push-back, damage, pickup, etc.).
     *
     * @param pair the two colliding entities
     */
    public abstract void resolve(CollisionPair pair);
}
