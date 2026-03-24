package io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation;

import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Player;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Wall;

/**
 * Resolves Player ↔ Wall collisions.
 *
 * BUG FIX: Previously the code reversed the player's velocity after pushing
 * the player out. In a maze game this is wrong – reversing velocity means next
 * frame the player moves back into the same wall and the cycle repeats, causing
 * stuttering and occasional tunnelling.
 *
 * FIX: After the position correction the player's velocity is zeroed (stop())
 * on the axis that caused the collision. The perpendicular velocity component
 * is kept so the player can slide along a wall corner.
 *
 * Design Pattern: Strategy – concrete collision resolution algorithm.
 */
public class PlayerWallCollision {

    public static void resolve(Player player, Wall wall) {
        Rectangle pb = player.getBounds();
        Rectangle wb = wall.getBounds();

        float overlapX = Math.min(pb.x + pb.width,  wb.x + wb.width)  - Math.max(pb.x, wb.x);
        float overlapY = Math.min(pb.y + pb.height, wb.y + wb.height) - Math.max(pb.y, wb.y);

        if (overlapX < overlapY) {
            // Horizontal collision – push out and zero the X velocity
            float newX = pb.x < wb.x ? wb.x - pb.width : wb.x + wb.width;
            player.setPosition(newX, player.getY());
            player.setVelocity(0, player.getVy());   // BUG FIX: zero X, keep Y
        } else {
            // Vertical collision – push out and zero the Y velocity
            float newY = pb.y < wb.y ? wb.y - pb.height : wb.y + wb.height;
            player.setPosition(player.getX(), newY);
            player.setVelocity(player.getVx(), 0);   // BUG FIX: zero Y, keep X
        }
    }
}
