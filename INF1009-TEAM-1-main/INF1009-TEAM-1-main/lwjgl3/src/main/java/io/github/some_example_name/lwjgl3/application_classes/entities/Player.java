package io.github.some_example_name.lwjgl3.application_classes.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.Direction;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.UserMovable;

/**
 * Represents the player character in the game.
 * Manages player movement, health, and points.
 */
public class Player extends MovableEntity implements Collidable, UserMovable {
    private static final float FRICTION = 0.85f;
    private static final int DEFAULT_MAX_HEALTH = 100;
    private static final int DEFAULT_STARTING_POINTS = 100;

    private int maxHealth;
    private int health;
    private int points;

    private Vector2 lastDirection;

    /**
     * Constructs a new Player.
     * 
     * @param name Player's name
     * @param position Initial position
     * @param texture Player's texture
     * @param speed Player's movement speed
     */
    public Player(String name, Vector2 position, Texture texture, float speed) {
        super(name, position, texture, speed);
        initializePlayerStats();
    }

    /**
     * Initialize player's default stats.
     */
    private void initializePlayerStats() {
        this.velocity = new Vector2(0, 0);
        this.lastDirection = new Vector2(0, 0);
        this.maxHealth = DEFAULT_MAX_HEALTH;
        this.health = DEFAULT_MAX_HEALTH;
        this.points = DEFAULT_STARTING_POINTS;
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
     * Move the Player Entity
     * 
     * @param Direction to move Entity
     */
    @Override
    public void move(Direction direction) {
        move(direction, 1.0f);
    }

    // Overload: lets the caller slow down or speed up movement
    public void move(Direction direction, float speedMultiplier) {

        float deltaTime = Gdx.graphics.getDeltaTime();
        
        // Set velocity based on direction
        velocity.x = direction.getX() * speed * 200 * speedMultiplier;
        velocity.y = direction.getY() * speed * 200 * speedMultiplier;
        lastDirection.set(direction.getX(), direction.getY());
        
        // Move the character
        Vector2 position = getPosition();
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        
        // Apply friction to gradually slow down
        velocity.scl(FRICTION);
        }

    
    /**
     * Gets the last movement direction vector.
     * 
     * @return Direction vector
     */
    public Vector2 getLastDirection() {
        return lastDirection;
    }
    
    /**
     * Gets the current health of the player.
     * 
     * @return Current health
     */
    public int getHealth() {
        return health;
    }
    
    /**
     * Gets the maximum health of the player.
     * 
     * @return Maximum health
     */
    public int getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * Sets the player's health to a specific value.
     * 
     * @param newHealth The new health value
     */
    public void setHealth(int newHealth) {
        health = Math.max(0, Math.min(newHealth, maxHealth));
        
        if (health <= 0) {
            onPlayerDeath();
        }
    }
    
    /**
     * Adjusts the player's health by the specified amount.
     * 
     * @param amount Amount to adjust health by (positive heals, negative damages)
     */
    public void adjustHealth(int amount) {
        setHealth(health + amount);
    }
    
    /**
     * Handles player death logic.
     */
    private void onPlayerDeath() {
        // Potential future expansion for death effects
        System.out.println("Player has died!");
    }

    /**
     * Adjusts the player's points.
     * 
     * @param amount Amount to adjust points by
     */
    public void adjustPoints(int amount) {
        points = Math.max(0, points + amount);
    }

    /**
     * Adjusts the player's points.
     * 
     * @param amount Amount to adjust speed by
     */
    public void adjustSpeed(float amount) {
        // Adjust speed only if the result will be within acceptable range
        if (speed + amount >= 0.5f && speed + amount <= 1.5f) {
            speed += amount;
        } 
    }

    /**
     * Gets the current player points.
     * 
     * @return Current points
     */
    public int getPoints() {
        return points;
    }
}