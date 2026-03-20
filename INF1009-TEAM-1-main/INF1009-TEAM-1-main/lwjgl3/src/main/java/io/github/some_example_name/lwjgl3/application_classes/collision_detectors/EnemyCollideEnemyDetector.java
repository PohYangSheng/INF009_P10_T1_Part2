package io.github.some_example_name.lwjgl3.application_classes.collision_detectors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.EntityCollisionDetector;
import io.github.some_example_name.lwjgl3.application_classes.entities.Enemy;

/**
 * Handles collision detection and resolution between two enemy entities.
 * Ensures smooth separation and movement when enemies collide.
 */
public class EnemyCollideEnemyDetector extends EntityCollisionDetector<Enemy, Enemy> {
    private final Enemy enemy1;
    private final Enemy enemy2;

    public EnemyCollideEnemyDetector(Enemy enemy1, Enemy enemy2) {
        super(enemy1, enemy2);
        this.enemy1 = enemy1;
        this.enemy2 = enemy2;
    }

    @Override
    public void resolveCollision() {
        Rectangle enemy1Bounds = enemy1.getBoundingBox();
        Rectangle enemy2Bounds = enemy2.getBoundingBox();

        // Calculate collision overlap
        float overlapX = Math.min(enemy1Bounds.x + enemy1Bounds.width, enemy2Bounds.x + enemy2Bounds.width) - 
                         Math.max(enemy1Bounds.x, enemy2Bounds.x);
        float overlapY = Math.min(enemy1Bounds.y + enemy1Bounds.height, enemy2Bounds.y + enemy2Bounds.height) - 
                         Math.max(enemy1Bounds.y, enemy2Bounds.y);

        Vector2 pos1 = enemy1.getPosition();
        Vector2 pos2 = enemy2.getPosition();
        Vector2 collisionNormal = new Vector2(pos2).sub(pos1).nor();

        // Resolve collision based on smaller overlap axis
        if (overlapX < overlapY) {
            // Horizontal separation
            float pushAmount = overlapX / 2 + 1; // Add buffer to ensure separation
            
            enemy1.setPosition(new Vector2(pos1.x - pushAmount * collisionNormal.x, pos1.y));
            enemy2.setPosition(new Vector2(pos2.x + pushAmount * collisionNormal.x, pos2.y));
            
            setRepulsionVelocities(-collisionNormal.x, 0, collisionNormal.x, 0);
        } else {
            // Vertical separation
            float pushAmount = overlapY / 2 + 1; // Add buffer to ensure separation
            
            enemy1.setPosition(new Vector2(pos1.x, pos1.y - pushAmount * collisionNormal.y));
            enemy2.setPosition(new Vector2(pos2.x, pos2.y + pushAmount * collisionNormal.y));
            
            setRepulsionVelocities(0, -collisionNormal.y, 0, collisionNormal.y);
        }
        
        // Mark both enemies as in collision state
        enemy1.setCollisionState(true);
        enemy2.setCollisionState(true);
    }
    
    /**
     * Sets velocities for both enemies to create a smooth repulsion effect
     * @param x1 X direction for first enemy
     * @param y1 Y direction for first enemy
     * @param x2 X direction for second enemy
     * @param y2 Y direction for second enemy
     */
    private void setRepulsionVelocities(float x1, float y1, float x2, float y2) {
        float speed = Enemy.getBaseSpeed();
        
        // Set velocity for first enemy
        enemy1.setVelocity(new Vector2(
            x1 != 0 ? x1 * speed : 0, 
            y1 != 0 ? y1 * speed : 0
        ));
        
        // Set velocity for second enemy
        enemy2.setVelocity(new Vector2(
            x2 != 0 ? x2 * speed : 0, 
            y2 != 0 ? y2 * speed : 0
        ));
    }
}