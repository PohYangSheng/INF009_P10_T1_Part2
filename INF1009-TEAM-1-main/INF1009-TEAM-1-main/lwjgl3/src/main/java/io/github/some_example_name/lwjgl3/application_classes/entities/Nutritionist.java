package io.github.some_example_name.lwjgl3.application_classes.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Removable;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.AIMovable;

/**
 * Represents a Nutritionist entity in the game maze.
 * Manages nutritionist movement and behavior.
 */
public class Nutritionist extends MovableEntity implements Collidable, AIMovable, Removable {
    // Movement constants
    private static final float DIRECTION_CHANGE_INTERVAL = 2.0f;
    private static final float BASE_SPEED = 100f;

    // Movement tracking
    private float directionChangeTimer = 0;
    private Vector2 previousDirection;
    private boolean toBeRemoved = false;

    // Nutritionist-specific property
    private final String funFact;

    /**
     * Constructor for Nutritionist.
     * 
     * @param name Nutritionist name
     * @param position Initial position
     * @param texture Nutritionist texture
     * @param speed Movement speed
     * @param funFact Interesting nutritional fact
     */
    public Nutritionist(String name, Vector2 position, Texture texture, 
                        float speed, String funFact) {
        super(name, position, texture, speed);
        this.funFact = funFact;
        initializeMovement();
    }

    /**
     * Initialize initial movement direction.
     */
    private void initializeMovement() {
        this.velocity = getRandomCardinalDirection().scl(BASE_SPEED);
        this.previousDirection = new Vector2(velocity).nor();
    }

    /**
     * Generate a random cardinal direction.
     * 
     * @return Random cardinal direction vector
     */
    private Vector2 getRandomCardinalDirection() {
        switch (MathUtils.random(3)) {
            case 0: return new Vector2(1, 0);   // Right
            case 1: return new Vector2(-1, 0);  // Left
            case 2: return new Vector2(0, 1);   // Up
            case 3: return new Vector2(0, -1);  // Down
            default: return new Vector2(1, 0);  // Default right
        }
    }

    @Override
    public void moveAIControlled() {
        float deltaTime = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        
        // Update direction change timer
        directionChangeTimer += deltaTime;
        
        // Change direction periodically
        if (directionChangeTimer >= DIRECTION_CHANGE_INTERVAL) {
            changeDirection();
            directionChangeTimer = 0;
        }
        
        // Move based on current velocity
        Vector2 position = getPosition();
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        
        // Update previous direction
        previousDirection.set(velocity).nor();
    }

    /**
     * Change the nutritionist's movement direction.
     */
    private void changeDirection() {
        Vector2[] cardinalDirections = {
            new Vector2(1, 0),   // Right
            new Vector2(-1, 0),  // Left
            new Vector2(0, 1),   // Up
            new Vector2(0, -1)   // Down
        };
        
        // Filter out 180-degree turns
        com.badlogic.gdx.utils.Array<Vector2> validDirections = new com.badlogic.gdx.utils.Array<>();
        for (Vector2 dir : cardinalDirections) {
            if (!(dir.x == -previousDirection.x && dir.y == -previousDirection.y)) {
                validDirections.add(new Vector2(dir));
            }
        }
        
        // Choose a random valid direction
        if (!validDirections.isEmpty()) {
            int randomIndex = MathUtils.random(validDirections.size - 1);
            Vector2 newDirection = validDirections.get(randomIndex);
            velocity.set(newDirection.x * BASE_SPEED, newDirection.y * BASE_SPEED);
        }
    }

    /**
     * Get the bounding box of Entity
     * 
     * @return Rectangle Bounding Box
     */
    @Override
    public Rectangle getBoundingBox() {
        float boxWidth = getWidth() > 0 ? getWidth() : getTexture().getWidth();
        float boxHeight = getHeight() > 0 ? getHeight() : getTexture().getHeight();
        return new Rectangle(getPosition().x, getPosition().y, boxWidth, boxHeight);
    }

    /**
     * Get the nutritionist's fun fact.
     * 
     * @return Nutritional fact
     */
    public String getFunFact() {
        return funFact;
    }

    /**
     * Marks this nutritionist to be removed.
     */
    @Override
    public void setToBeRemoved() {
        toBeRemoved = true;
    }

    /**
     * Checks if nutritionist should be removed.
     * 
     * @return true if it should be removed, false otherwise
     */
    @Override
    public boolean shouldBeRemoved() {
        return toBeRemoved;
    }

    /**
     * Get the nutritionist's fun fact.
     * 
     * @return Speed of nutritionist (a constant as it wont be modified, is fixed)
     */
    public float getBaseSpeed(){
        return BASE_SPEED;
    }
}