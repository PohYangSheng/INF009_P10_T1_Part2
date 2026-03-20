package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Base class for entities that can move.
 * Extends Entity with velocity and size properties.
 */
public abstract class MovableEntity extends Entity {
    protected float speed;
    protected Vector2 velocity;
    protected float width;
    protected float height;

    /**
     * Creates a movable entity with default properties.
     */
    public MovableEntity() {
        super();
        this.speed = 0;
        this.velocity = new Vector2(0, 0);
        this.width = 0;
        this.height = 0;
    }
    
    /**
     * Creates a movable entity with the specified properties and speed.
     *
     * @param name Entity name for identification
     * @param position Initial position in the game world
     * @param texture Visual representation of the entity
     * @param speed Initial movement speed
     */
    public MovableEntity(String name, Vector2 position, Texture texture, float speed) {
        super(name, position, texture);
        this.speed = speed;
        this.velocity = new Vector2(0, 0);
        
        // Initialize width and height from texture if available
        initializeDimensions(texture);
    }

    /**
     * Creates a movable entity with the specified properties and default speed.
     *
     * @param name Entity name for identification
     * @param position Initial position in the game world
     * @param texture Visual representation of the entity
     */
    public MovableEntity(String name, Vector2 position, Texture texture) {
        super(name, position, texture);
        this.speed = 0;
        this.velocity = new Vector2(0, 0);
        
        // Initialize width and height from texture if available
        initializeDimensions(texture);
    }

    /**
     * Gets the entity's current velocity.
     *
     * @return The velocity vector
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /**
     * Sets the entity's velocity.
     *
     * @param velocity New velocity vector
     */
    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }
    
    /**
     * Sets the size of the entity.
     * 
     * @param width The width of the entity
     * @param height The height of the entity
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Gets the current width of the entity.
     * 
     * @return The width
     */
    public float getWidth() {
        return width;
    }
    
    /**
     * Gets the current height of the entity.
     * 
     * @return The height
     */
    public float getHeight() {
        return height;
    }
    
    /**
     * Draws the entity with custom size if set.
     * Otherwise, uses the default behavior.
     * 
     * @param batch SpriteBatch to use for drawing
     */
    @Override
    public void draw(SpriteBatch batch) {
        // If custom size is set, use it, otherwise use texture's original size
        if (width > 0 && height > 0) {
            batch.draw(getTexture(), getPosition().x, getPosition().y, width, height);
        } else {
            // Fall back to original texture size
            super.draw(batch);
        }
    }


    /**
     * Initialise the dimension of the entity
     * @param texture The texture of the entity
     */
    private void initializeDimensions(Texture texture) {
        if (texture != null) {
            this.width = texture.getWidth();
            this.height = texture.getHeight();
        } else {
            this.width = 0;
            this.height = 0;
        }
    }
}