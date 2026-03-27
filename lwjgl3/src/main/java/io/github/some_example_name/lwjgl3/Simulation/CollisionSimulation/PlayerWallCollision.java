package io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation;

import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Player;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Wall;

// handles player bumping into walls
public class PlayerWallCollision {

    // handle what happens after the collision
    public static void resolve(Player player, Wall wall) {
        Rectangle pb = player.getBounds();
        Rectangle wb = wall.getBounds();

        float overlapX = Math.min(pb.x + pb.width,  wb.x + wb.width)  - Math.max(pb.x, wb.x);
        float overlapY = Math.min(pb.y + pb.height, wb.y + wb.height) - Math.max(pb.y, wb.y);

        if (overlapX < overlapY) {
            
            float newX = pb.x < wb.x ? wb.x - pb.width : wb.x + wb.width;
            player.setPosition(newX, player.getY());
            player.setVelocity(0, player.getVy());   
        } else {
            
            float newY = pb.y < wb.y ? wb.y - pb.height : wb.y + wb.height;
            player.setPosition(player.getX(), newY);
            player.setVelocity(player.getVx(), 0);   
        }
    }
}
