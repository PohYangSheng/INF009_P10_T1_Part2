package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Base class for all game entities.
 * Provides core functionality for position, rendering, and identification.
 */
public abstract class Entity {
    private String name;
    private Vector2 position;
    private Texture texture;

    /**
     * Creates an entity with default properties.
     * Primarily used for subclasses that need to set properties later.
     */
    public Entity() {
        this.name = "Empty name";
        this.position = new Vector2(0, 0);
        this.texture = null;
    }

    /**
     * Creates an entity with the specified properties.
     *
     * @param name Entity name for identification
     * @param position Initial position in the game world
     * @param texture Visual representation of the entity
     */
    public Entity(String name, Vector2 position, Texture texture) {
        this.name = name;
        this.position = position;
        this.texture = texture;
    }

    /**
     * Gets the entity's name.
     *
     * @return The entity name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the entity's name.
     *
     * @param name New entity name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the entity's position.
     *
     * @return The position vector
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Sets the entity's position.
     *
     * @param position New position vector
     */
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    /**
     * Sets the entity's X position.
     *
     * @param x New X coordinate
     */
    public void setPositionX(float x) {
        position.x = x;
    }

    /**
     * Sets the entity's Y position.
     *
     * @param y New Y coordinate
     */
    public void setPositionY(float y) {
        position.y = y;
    }

    /**
     * Gets the entity's texture.
     *
     * @return The texture used for rendering
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Sets the entity's texture.
     *
     * @param texture New texture for rendering
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /**
     * Draws the entity using the provided SpriteBatch.
     * Can be overridden by subclasses for custom rendering.
     *
     * @param batch SpriteBatch to use for drawing
     */
    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }
}