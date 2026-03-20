package io.github.some_example_name.lwjgl3.abstract_engine.collision;

/**
 * Base class for detecting collisions between two collidable entities.
 * 
 * @param <T> Type of the first collidable entity
 * @param <U> Type of the second collidable entity
 */
public abstract class EntityCollisionDetector<T extends Collidable, U extends Collidable> extends CollisionDetector {
    private final T entityA;
    private final U entityB;
    
    /**
     * Creates a new collision detector for the specified entities.
     * 
     * @param a The first entity
     * @param b The second entity
     */
    protected EntityCollisionDetector(T a, U b) {
        this.entityA = a;
        this.entityB = b;
    }
    
    /**
     * Checks for collision between the two entities.
     * 
     * @return true if the entities' bounding boxes overlap, false otherwise
     */
    @Override
    public boolean checkCollision() {
        return entityA.getBoundingBox().overlaps(entityB.getBoundingBox());
    }
    
    /**
     * Resolves the collision between the two entities.
     * Subclasses should implement specific collision resolution behavior.
     */
    @Override
    public abstract void resolveCollision();
    
    /**
     * Gets the first entity involved in this collision.
     * 
     * @return The first entity
     */
    public T getEntityA() {
        return entityA;
    }
    
    /**
     * Gets the second entity involved in this collision.
     * 
     * @return The second entity
     */
    public U getEntityB() {
        return entityB;
    }
}