package io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation;

import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Dietitian;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Wall;

// handles dietitian bumping into walls
public class DietitianWallCollision {

    // handle what happens after the collision
    public static void resolve(Dietitian n, Wall wall) {
        Rectangle nb = n.getBounds(), wb = wall.getBounds();

        float overlapX = Math.min(nb.x + nb.width,  wb.x + wb.width)  - Math.max(nb.x, wb.x);
        float overlapY = Math.min(nb.y + nb.height, wb.y + wb.height) - Math.max(nb.y, wb.y);
        float spd      = n.getSpeed(); 

        if (overlapX < overlapY) {
            float newX = nb.x < wb.x ? wb.x - nb.width - 1 : wb.x + wb.width + 1;
            n.setPosition(newX, n.getY());
            float yDir = Math.abs(n.getVy()) < 0.1f
                    ? (Math.random() > 0.5 ? 1f : -1f) : Math.signum(n.getVy());
            n.setVelocity(0, yDir * spd);
        } else {
            float newY = nb.y < wb.y ? wb.y - nb.height - 1 : wb.y + wb.height + 1;
            n.setPosition(n.getX(), newY);
            float xDir = Math.abs(n.getVx()) < 0.1f
                    ? (Math.random() > 0.5 ? 1f : -1f) : Math.signum(n.getVx());
            n.setVelocity(xDir * spd, 0);
        }
    }
}
