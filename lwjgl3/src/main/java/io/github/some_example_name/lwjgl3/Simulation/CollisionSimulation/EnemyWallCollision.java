package io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation;

import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Enemy;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Wall;

// handles enemy bumping into walls - makes them change direction
public class EnemyWallCollision {

    // handle what happens after the collision
    public static void resolve(Enemy enemy, Wall wall) {
        Rectangle eb = enemy.getBounds();
        Rectangle wb = wall.getBounds();

        float overlapX = Math.min(eb.x + eb.width,  wb.x + wb.width)  - Math.max(eb.x, wb.x);
        float overlapY = Math.min(eb.y + eb.height, wb.y + wb.height) - Math.max(eb.y, wb.y);
        float spd = enemy.getSpeed();

        if (overlapX < overlapY) {
            float newX = eb.x < wb.x ? wb.x - eb.width - 1 : wb.x + wb.width + 1;
            enemy.setPosition(newX, enemy.getY());
            float yDir = Math.abs(enemy.getVy()) < 0.1f ? (Math.random() > 0.5 ? 1f : -1f) : Math.signum(enemy.getVy());
            enemy.setVelocity(0, yDir * spd);
        } else {
            float newY = eb.y < wb.y ? wb.y - eb.height - 1 : wb.y + wb.height + 1;
            enemy.setPosition(enemy.getX(), newY);
            float xDir = Math.abs(enemy.getVx()) < 0.1f ? (Math.random() > 0.5 ? 1f : -1f) : Math.signum(enemy.getVx());
            enemy.setVelocity(xDir * spd, 0);
        }
        enemy.setInCollision(true);
    }
}
