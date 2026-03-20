package io.github.some_example_name.lwjgl3.application_classes.collision_detectors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.EntityCollisionDetector;
import io.github.some_example_name.lwjgl3.application_classes.entities.Player;
import io.github.some_example_name.lwjgl3.application_classes.entities.Wall;

/**
 * Detects and resolves collisions between the player and walls.
 * Prevents the player from passing through wall obstacles.
 */
public class PlayerCollideWallDetector extends EntityCollisionDetector<Player, Wall> {
    private final Player player;
    private final Wall wall;

    /**
     * Creates a new collision detector for player-wall interactions.
     *
     * @param player The player entity
     * @param wall The wall entity
     */
    public PlayerCollideWallDetector(Player player, Wall wall) {
        super(player, wall);
        this.player = player;
        this.wall = wall;
    }

    /**
     * Resolves player-wall collisions by repositioning the player
     * and adjusting their velocity to prevent wall penetration.
     */
    @Override
    public void resolveCollision() {
        // Get bounding boxes
        Rectangle playerBounds = player.getBoundingBox();
        Rectangle wallBounds = wall.getBoundingBox();

        // Calculate overlap
        float overlapX = Math.min(playerBounds.x + playerBounds.width, wallBounds.x + wallBounds.width) - 
                         Math.max(playerBounds.x, wallBounds.x);
        float overlapY = Math.min(playerBounds.y + playerBounds.height, wallBounds.y + wallBounds.height) - 
                         Math.max(playerBounds.y, wallBounds.y);

        // Get player position and velocity
        Vector2 playerVelocity = player.getVelocity();
        Vector2 playerPosition = player.getPosition();

        // Determine which side the collision happened on and resolve it
        if (overlapX < overlapY) {
            // Horizontal collision
            if (playerPosition.x < wallBounds.x) {
                player.setPosition(new Vector2(wallBounds.x - playerBounds.width, playerPosition.y)); // Left side
            } else {
                player.setPosition(new Vector2(wallBounds.x + wallBounds.width, playerPosition.y)); // Right side
            }
            player.setVelocity(new Vector2(-playerVelocity.x, playerVelocity.y)); // Reverse X movement
        } else {
            // Vertical collision
            if (playerPosition.y < wallBounds.y) {
                player.setPosition(new Vector2(playerPosition.x, wallBounds.y - playerBounds.height)); // Bottom side
            } else {
                player.setPosition(new Vector2(playerPosition.x, wallBounds.y + wallBounds.height)); // Top side
            }
            player.setVelocity(new Vector2(playerVelocity.x, -playerVelocity.y)); // Reverse Y movement
        }
    }
}