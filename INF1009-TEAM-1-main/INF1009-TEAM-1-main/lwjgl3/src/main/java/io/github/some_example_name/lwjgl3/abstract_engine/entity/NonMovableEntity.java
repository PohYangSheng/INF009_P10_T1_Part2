package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Base class for entities that don't move.
 * Extends Entity without adding movement-related properties.
 */
public class NonMovableEntity extends Entity {
    /**
     * Creates a non-movable entity with default properties.
     */
    public NonMovableEntity() {
        super("No name", new Vector2(0, 0), null);
    }
    
    /**
     * Creates a non-movable entity with the specified properties.
     *
     * @param name Entity name for identification
     * @param position Initial (and permanent) position in the game world
     * @param texture Visual representation of the entity
     */
    public NonMovableEntity(String name, Vector2 position, Texture texture) {
        super(name, position, texture);
    }
}