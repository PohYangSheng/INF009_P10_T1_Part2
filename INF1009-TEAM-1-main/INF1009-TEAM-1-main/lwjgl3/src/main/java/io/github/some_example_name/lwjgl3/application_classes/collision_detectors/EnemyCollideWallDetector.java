package io.github.some_example_name.lwjgl3.application_classes.collision_detectors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.EntityCollisionDetector;
import io.github.some_example_name.lwjgl3.application_classes.entities.Enemy;
import io.github.some_example_name.lwjgl3.application_classes.entities.Wall;

/**
 * Detects and resolves collisions between enemies and walls.
 * Prevents enemies from passing through walls and adjusts their movement direction.
 */
public class EnemyCollideWallDetector extends EntityCollisionDetector<Enemy, Wall> {
    private final Enemy enemy;
    private final Wall wall;

    /**
     * Creates a new detector for enemy-wall collisions.
     *
     * @param enemy The enemy entity
     * @param wall The wall entity
     */
    public EnemyCollideWallDetector(Enemy enemy, Wall wall) {
        super(enemy, wall);
        this.enemy = enemy;
        this.wall = wall;
    }

    /**
     * Resolves enemy-wall collisions by repositioning the enemy
     * and changing its movement direction.
     */
    @Override
    public void resolveCollision() {
        // Get bounding boxes
        Rectangle enemyBounds = enemy.getBoundingBox();
        Rectangle wallBounds = wall.getBoundingBox();

        // Calculate overlap
        float overlapX = Math.min(enemyBounds.x + enemyBounds.width, wallBounds.x + wallBounds.width) - 
                         Math.max(enemyBounds.x, wallBounds.x);
        float overlapY = Math.min(enemyBounds.y + enemyBounds.height, wallBounds.y + wallBounds.height) - 
                         Math.max(enemyBounds.y, wallBounds.y);

        // Get enemy position and velocity
        Vector2 enemyVelocity = enemy.getVelocity();
        Vector2 enemyPosition = enemy.getPosition();

        // Determine which side the collision happened on and resolve it
        if (overlapX < overlapY) {
            // Horizontal collision
            if (enemyPosition.x < wallBounds.x) {
                // Left side collision - slight adjustment to prevent sticking
                enemy.setPosition(new Vector2(wallBounds.x - enemyBounds.width - 1, enemyPosition.y));
                
                // Set new velocity - move vertically
                if (Math.abs(enemyVelocity.y) < 0.1f) {
                    // If not already moving vertically, choose a vertical direction
                    float yDir = Math.random() > 0.5f ? 1f : -1f;
                    enemy.setVelocity(new Vector2(0, yDir * Enemy.getBaseSpeed()));
                } else {
                    // Continue with current vertical movement
                    enemy.setVelocity(new Vector2(0, enemyVelocity.y));
                }
            } else {
                // Right side collision
                enemy.setPosition(new Vector2(wallBounds.x + wallBounds.width + 1, enemyPosition.y));
                
                // Set new velocity - move vertically
                if (Math.abs(enemyVelocity.y) < 0.1f) {
                    // If not already moving vertically, choose a vertical direction
                    float yDir = Math.random() > 0.5f ? 1f : -1f;
                    enemy.setVelocity(new Vector2(0, yDir * Enemy.getBaseSpeed()));
                } else {
                    // Continue with current vertical movement
                    enemy.setVelocity(new Vector2(0, enemyVelocity.y));
                }
            }
        } else {
            // Vertical collision
            if (enemyPosition.y < wallBounds.y) {
                // Bottom side collision
                enemy.setPosition(new Vector2(enemyPosition.x, wallBounds.y - enemyBounds.height - 1));
                
                // Set new velocity - move horizontally
                if (Math.abs(enemyVelocity.x) < 0.1f) {
                    // If not already moving horizontally, choose a horizontal direction
                    float xDir = Math.random() > 0.5f ? 1f : -1f;
                    enemy.setVelocity(new Vector2(xDir * Enemy.getBaseSpeed(), 0));
                } else {
                    // Continue with current horizontal movement
                    enemy.setVelocity(new Vector2(enemyVelocity.x, 0));
                }
            } else {
                // Top side collision
                enemy.setPosition(new Vector2(enemyPosition.x, wallBounds.y + wallBounds.height + 1));
                
                // Set new velocity - move horizontally
                if (Math.abs(enemyVelocity.x) < 0.1f) {
                    // If not already moving horizontally, choose a horizontal direction
                    float xDir = Math.random() > 0.5f ? 1f : -1f;
                    enemy.setVelocity(new Vector2(xDir * Enemy.getBaseSpeed(), 0));
                } else {
                    // Continue with current horizontal movement
                    enemy.setVelocity(new Vector2(enemyVelocity.x, 0));
                }
            }
        }
        
        // Put the enemy in collision state to ensure smooth transition
        enemy.setCollisionState(true);
    }
}