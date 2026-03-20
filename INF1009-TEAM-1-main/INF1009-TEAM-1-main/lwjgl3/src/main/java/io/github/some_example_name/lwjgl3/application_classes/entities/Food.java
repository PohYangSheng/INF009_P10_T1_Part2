package io.github.some_example_name.lwjgl3.application_classes.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Removable;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.NonMovableEntity;

/**
 * Base abstract class for food items in the game.
 */
public abstract class Food extends NonMovableEntity implements Collidable, Removable {
    protected float width;
    protected float height;
    protected boolean toBeRemoved;
    protected FoodType type;
    protected String funFact;
    
    /**
     * Enumeration for different food types.
     */
    public enum FoodType {
        HEALTHY, UNHEALTHY
    }
    
    // Expiration related fields
    protected float lifespanInSeconds;
    protected float elapsedTime;
    protected boolean expired;
    
    /**
     * Constructor for food entity.
     * 
     * @param name Food name
     * @param position Position in the maze
     * @param texture Food texture
     * @param type Food type (healthy or unhealthy)
     * @param lifespanInSeconds How long the food will exist before expiring
     * @param funFact Interesting fact about the food
     */
    public Food(String name, Vector2 position, Texture texture, FoodType type, 
                float lifespanInSeconds, String funFact) {
        super(name, position, texture);
        initializeFoodProperties(texture, type, lifespanInSeconds, funFact);
    }
    
    /**
     * Initialize common food properties.
     */
    private void initializeFoodProperties(Texture texture, FoodType type, 
                                          float lifespanInSeconds, String funFact) {
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        this.toBeRemoved = false;
        this.type = type;
        this.lifespanInSeconds = lifespanInSeconds;
        this.elapsedTime = 0f;
        this.expired = false;
        this.funFact = funFact;
    }
    
    /**
     * Get the food type.
     * 
     * @return FoodType of this food
     */
    public FoodType getType() {
        return type;
    }
    
    /**
     * Get the effect value when consumed.
     * 
     * @return Effect value when consumed
     */
    public abstract int getEffectValue();
    
    @Override
    public boolean shouldBeRemoved() {
        return toBeRemoved;
    }
    
    /**
     * Set the size of the food.
     * 
     * @param width Width of the food
     * @param height Height of the food
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Update food's elapsed time and check for expiration.
     * 
     * @param deltaTime Time elapsed since last update
     */
    public void update(float deltaTime) {
        if (!toBeRemoved && !expired) {
            elapsedTime += deltaTime;
            
            // Check for expiration
            if (elapsedTime >= lifespanInSeconds) {
                expired = true;
                setToBeRemoved();
            }
        }
    }
    
    /**
     * Check if the food has expired.
     * 
     * @return True if the food has expired
     */
    public boolean isExpired() {
        return expired;
    }
    
    /**
     * Get remaining lifetime as a percentage.
     * 
     * @return Percentage of life remaining (0-1)
     */
    public float getRemainingLifePercentage() {
        return Math.max(0, 1 - (elapsedTime / lifespanInSeconds));
    }
    

    /**
     * Get the bounding box of Entity
     * 
     * @return Rectangle Bounding Box
     */
    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(getPosition().x, getPosition().y, width, height);
    }
    
    /**
     * Get the fun fact associated with the food.
     * 
     * @return Fun fact string
     */
    public String getFunFact() {
        return funFact;
    }

    /**
     * Apply food effect to Player Entity
     * 
     * @param Player Player entity to apply effect to
     */
    public abstract void applyEffect(Player player);
    
    /**
     * Draws food entity
     *
     * @param batch SpriteBatch to use for drawing
     */
    @Override
    public void draw(SpriteBatch batch) {
        if (!toBeRemoved && !expired) {
            batch.draw(getTexture(), getPosition().x, getPosition().y, width, height);
        }
    }

    /**
     * Marks this Food to be removed.
     */
    @Override
    public void setToBeRemoved() {
        toBeRemoved = true;
    }
}