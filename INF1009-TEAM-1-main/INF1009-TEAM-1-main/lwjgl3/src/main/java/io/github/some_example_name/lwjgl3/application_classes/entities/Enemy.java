package io.github.some_example_name.lwjgl3.application_classes.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Removable;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.MovableEntity;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.AIMovable;

/**
 * Enemy entity with complex movement behaviors and chasing mechanics.
 */
public class Enemy extends MovableEntity implements Collidable, AIMovable, Removable {
    // Movement constants
    private static final float DIRECTION_CHANGE_INTERVAL = 1.0f;
    private static final long COLLISION_COOLDOWN = 500;
    private static final float COLLISION_STATE_MAX_DURATION = 0.5f;
    private static final float BEHAVIOR_CHANGE_INTERVAL = 5.0f;

    // Configurable speed
    private static float CHEF_SPEED = 150f;

    // Removal flag (used when enemy is eaten in SECRET mode)
    private boolean toBeRemoved = false;

    // Movement tracking
    private float directionChangeTimer = 0;
    private float behaviorTimer = 0;
    private Vector2 previousDirection;
    private long lastCollisionTime = 0;

    // Collision management
    private boolean inCollisionState = false;
    private float collisionStateDuration = 0;

    // Behavior management
    private Entity targetEntity;
    private final float chaseChance = 0.9f;
    private ChefBehavior currentBehavior = ChefBehavior.RANDOM;

    /**
     * Defines different movement strategies for the enemy.
     */
    public enum ChefBehavior {
        RANDOM,   // Unpredictable movement
        CHASE,    // Pursue the player
        SCATTER   // Move to a predefined area
    }

    /**
     * Default constructor initializes enemy with random movement.
     */
    public Enemy() {
        super();
        initializeMovement();
    }

    /**
     * Parameterized constructor for creating an enemy with specific attributes.
     */
    public Enemy(String name, Vector2 position, Texture texture, float speed) {
        super(name, position, texture, speed);
        setBaseSpeed(speed);
        initializeMovement();
    }

    /**
     * Initialize enemy movement with a random direction.
     */
    private void initializeMovement() {
        this.velocity = getRandomCardinalDirection().scl(CHEF_SPEED);
        this.previousDirection = new Vector2(velocity).nor();
    }

    /**
     * Generates a random cardinal direction for movement.
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

    /**
     * Get the current base speed for enemies.
     */
    public static float getBaseSpeed() {
        return CHEF_SPEED;
    }

    /**
     * Set the base speed for enemies.
     */
    public static void setBaseSpeed(float speed) {
        CHEF_SPEED = speed;
    }

    /** Marks this enemy to be removed from the entity manager (eaten in SECRET mode). */
    @Override
    public void setToBeRemoved() {
        toBeRemoved = true;
    }

    /** Returns true if this enemy should be removed from the entity manager. */
    @Override
    public boolean shouldBeRemoved() {
        return toBeRemoved;
    }

    /**
     * Set the target entity for chasing.
     */
    public void setTargetEntity(Entity target) {
        this.targetEntity = target;
    }

    @Override
    public void moveAIControlled() {
        float deltaTime = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        
        // Update timers
        behaviorTimer += deltaTime;
        directionChangeTimer += deltaTime;
        
        // Handle collision state
        if (handleCollisionState(deltaTime)) return;
        
        // Periodically decide behavior
        if (behaviorTimer >= BEHAVIOR_CHANGE_INTERVAL) {
            decideBehavior();
            behaviorTimer = 0;
        }
        
        // Move based on current behavior
        switch (currentBehavior) {
            case CHASE:
                if (targetEntity != null) {
                    moveTowardsTarget(deltaTime);
                } else {
                    moveRandomly(deltaTime);
                }
                break;
            case SCATTER:
            case RANDOM:
                moveRandomly(deltaTime);
                break;
        }
        
        // Update previous direction
        previousDirection.set(velocity).nor();
    }

    /**
     * Manage collision state duration.
     * @return Whether to skip normal movement
     */
    private boolean handleCollisionState(float deltaTime) {
        if (inCollisionState) {
            collisionStateDuration += deltaTime;
            if (collisionStateDuration >= COLLISION_STATE_MAX_DURATION) {
                // Exit collision state
                inCollisionState = false;
                collisionStateDuration = 0;
                decideBehavior();
            } else {
                // Continue with current velocity
                Vector2 position = getPosition();
                position.add(velocity.x * deltaTime, velocity.y * deltaTime);
                return true;
            }
        }
        return false;
    }

    /**
     * Decide the next behavior strategy.
     */
    private void decideBehavior() {
        float roll = MathUtils.random();
        
        if (targetEntity != null && roll < chaseChance) {
            currentBehavior = ChefBehavior.CHASE;
        } else if (roll < 0.85f) {
            currentBehavior = ChefBehavior.SCATTER;
        } else {
            currentBehavior = ChefBehavior.RANDOM;
        }
    }

    /**
     * Move towards the target entity.
     */
    private void moveTowardsTarget(float deltaTime) {
        if (targetEntity == null) return;
        
        Vector2 targetPos = targetEntity.getPosition();
        Vector2 myPos = getPosition();
        
        Vector2 direction = new Vector2();
        
        float dx = targetPos.x - myPos.x;
        float dy = targetPos.y - myPos.y;
        
        // Change direction periodically
        if (directionChangeTimer >= DIRECTION_CHANGE_INTERVAL || velocity.isZero()) {
            // Prioritize movement along the axis with greater distance
            if (Math.abs(dx) > Math.abs(dy)) {
                direction.set(Math.signum(dx), 0);
            } else {
                direction.set(0, Math.signum(dy));
            }
            
            // Prevent 180-degree turns
            if (previousDirection.x != 0 && direction.x == -previousDirection.x ||
                previousDirection.y != 0 && direction.y == -previousDirection.y) {
                // Try alternative axis
                direction.set(
                    Math.abs(dx) > Math.abs(dy) ? 0 : Math.signum(dx),
                    Math.abs(dx) > Math.abs(dy) ? Math.signum(dy) : 0
                );
            }
            
            velocity.set(direction.nor().scl(CHEF_SPEED));
            directionChangeTimer = 0;
        }
        
        // Update position
        Vector2 position = getPosition();
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
    }

    /**
     * Move randomly throughout the maze.
     */
    private void moveRandomly(float deltaTime) {
        // Change direction periodically
        if (directionChangeTimer >= DIRECTION_CHANGE_INTERVAL || velocity.isZero()) {
            changeDirection();
            directionChangeTimer = 0;
        }
        
        // Update position
        Vector2 position = getPosition();
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
    }

    /**
     * Change direction while avoiding 180-degree turns.
     */
    public void changeDirection() {
        Vector2[] cardinalDirections = {
            new Vector2(1, 0),  // Right
            new Vector2(-1, 0), // Left
            new Vector2(0, 1),  // Up
            new Vector2(0, -1)  // Down
        };
        
        // Filter out 180-degree turns
        Array<Vector2> validDirections = new Array<>();
        for (Vector2 dir : cardinalDirections) {
            if (!(dir.x == -previousDirection.x && dir.y == -previousDirection.y)) {
                validDirections.add(new Vector2(dir));
            }
        }
        
        // Choose a random valid direction
        if (validDirections.size > 0) {
            int randomIndex = MathUtils.random(validDirections.size - 1);
            Vector2 newDirection = validDirections.get(randomIndex);
            velocity.set(newDirection.x * CHEF_SPEED, newDirection.y * CHEF_SPEED);
        }
    }

    /**
     * Force a direction change after collision.
     */
    public void forceDirectionChange() {
        long currentTime = TimeUtils.millis();
        if (currentTime - lastCollisionTime < COLLISION_COOLDOWN) {
            return;
        }
        
        lastCollisionTime = currentTime;
        
        Vector2 currentDir = new Vector2(velocity).nor();
        
        Vector2[] cardinalDirections = {
            new Vector2(1, 0),  // Right
            new Vector2(-1, 0), // Left
            new Vector2(0, 1),  // Up
            new Vector2(0, -1)  // Down
        };
        
        // Find perpendicular directions
        Array<Vector2> perpendicularDirs = new Array<>();
        for (Vector2 dir : cardinalDirections) {
            if (dir.x * currentDir.x == 0 && dir.y * currentDir.y == 0 &&
                !(dir.x == -currentDir.x && dir.y == -currentDir.y)) {
                perpendicularDirs.add(new Vector2(dir));
            }
        }
        
        // Choose a random perpendicular direction
        if (perpendicularDirs.size > 0) {
            int randomIndex = MathUtils.random(perpendicularDirs.size - 1);
            Vector2 newDirection = perpendicularDirs.get(randomIndex);
            
            velocity.set(newDirection.x * CHEF_SPEED, newDirection.y * CHEF_SPEED);
            previousDirection.set(newDirection);
            directionChangeTimer = 0;
        }
    }

    /**
     * Get bounding box for collision detection.
     */
    @Override
    public Rectangle getBoundingBox() {
        float boxWidth = getWidth() > 0 ? getWidth() : getTexture().getWidth();
        float boxHeight = getHeight() > 0 ? getHeight() : getTexture().getHeight();
        return new Rectangle(getPosition().x, getPosition().y, boxWidth, boxHeight);
    }

    /**
     * Set the collision state of the enemy.
     */
    public void setCollisionState(boolean inCollision) {
        this.inCollisionState = inCollision;
        this.collisionStateDuration = 0;
    }
    
    /**
     * Check if the enemy is in a collision state.
     */
    public boolean isInCollisionState() {
        return inCollisionState;
    }
}