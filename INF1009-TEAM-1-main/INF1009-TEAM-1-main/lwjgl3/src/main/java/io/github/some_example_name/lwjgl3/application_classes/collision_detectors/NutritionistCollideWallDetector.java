package io.github.some_example_name.lwjgl3.application_classes.collision_detectors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.EntityCollisionDetector;
import io.github.some_example_name.lwjgl3.application_classes.entities.Nutritionist;
import io.github.some_example_name.lwjgl3.application_classes.entities.Wall;

/**
 * Handles collision detection and resolution between a nutritionist 
 * and a wall in the game maze.
 */
public class NutritionistCollideWallDetector extends EntityCollisionDetector<Nutritionist, Wall> {
    private final Nutritionist nutritionist;
    private final Wall wall;

    public NutritionistCollideWallDetector(Nutritionist nutritionist, Wall wall) {
        super(nutritionist, wall);
        this.nutritionist = nutritionist;
        this.wall = wall;
    }

    @Override
    public void resolveCollision() {
        Rectangle nutritionistBounds = nutritionist.getBoundingBox();
        Rectangle wallBounds = wall.getBoundingBox();

        // Calculate collision overlap
        float overlapX = Math.min(nutritionistBounds.x + nutritionistBounds.width, wallBounds.x + wallBounds.width) - 
                         Math.max(nutritionistBounds.x, wallBounds.x);
        float overlapY = Math.min(nutritionistBounds.y + nutritionistBounds.height, wallBounds.y + wallBounds.height) - 
                         Math.max(nutritionistBounds.y, wallBounds.y);

        // Get current position and velocity
        Vector2 nutritionistPosition = nutritionist.getPosition();
        Vector2 nutritionistVelocity = nutritionist.getVelocity();

        // Resolve collision based on smaller overlap axis
        if (overlapX < overlapY) {
            // Horizontal collision
            if (nutritionistPosition.x < wallBounds.x) {
                // Left side collision
                nutritionist.setPosition(new Vector2(wallBounds.x - nutritionistBounds.width - 1, nutritionistPosition.y));
            } else {
                // Right side collision
                nutritionist.setPosition(new Vector2(wallBounds.x + wallBounds.width + 1, nutritionistPosition.y));
            }
            
            // Adjust vertical movement
            nutritionist.setVelocity(new Vector2(0, chooseVerticalDirection(nutritionistVelocity) * nutritionist.getBaseSpeed())); // Use base speed directly
        } else {
            // Vertical collision
            if (nutritionistPosition.y < wallBounds.y) {
                // Bottom side collision
                nutritionist.setPosition(new Vector2(nutritionistPosition.x, wallBounds.y - nutritionistBounds.height - 1));
            } else {
                // Top side collision
                nutritionist.setPosition(new Vector2(nutritionistPosition.x, wallBounds.y + wallBounds.height + 1));
            }
            
            // Adjust horizontal movement
            nutritionist.setVelocity(new Vector2(chooseHorizontalDirection(nutritionistVelocity) * nutritionist.getBaseSpeed(), 0)); // Use base speed directly
        }
    }

    /**
     * Chooses a vertical direction based on current velocity
     * @param currentVelocity Current velocity vector
     * @return -1 or 1 for vertical direction
     */
    private float chooseVerticalDirection(Vector2 currentVelocity) {
        // If not moving vertically, choose a random direction
        if (Math.abs(currentVelocity.y) < 0.1f) {
            return Math.random() > 0.5f ? 1f : -1f;
        }
        // Continue with current vertical movement
        return Math.signum(currentVelocity.y);
    }

    /**
     * Chooses a horizontal direction based on current velocity
     * @param currentVelocity Current velocity vector
     * @return -1 or 1 for horizontal direction
     */
    private float chooseHorizontalDirection(Vector2 currentVelocity) {
        // If not moving horizontally, choose a random direction
        if (Math.abs(currentVelocity.x) < 0.1f) {
            return Math.random() > 0.5f ? 1f : -1f;
        }
        // Continue with current horizontal movement
        return Math.signum(currentVelocity.x);
    }
}