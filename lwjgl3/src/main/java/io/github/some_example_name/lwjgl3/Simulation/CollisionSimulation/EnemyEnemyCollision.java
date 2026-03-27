package io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Enemy;

// handles two enemies bumping into each other
public class EnemyEnemyCollision {

    // handle what happens after the collision
    public static void resolve(Enemy e1, Enemy e2) {
        Rectangle b1 = e1.getBounds(), b2 = e2.getBounds();

        float overlapX = Math.min(b1.x + b1.width,  b2.x + b2.width)  - Math.max(b1.x, b2.x);
        float overlapY = Math.min(b1.y + b1.height, b2.y + b2.height) - Math.max(b1.y, b2.y);

        Vector2 normal = new Vector2(e2.getX() - e1.getX(), e2.getY() - e1.getY()).nor();
        float spd = e1.getSpeed();

        if (overlapX < overlapY) {
            float push = overlapX / 2f + 1f;
            e1.setPosition(e1.getX() - push * normal.x, e1.getY());
            e2.setPosition(e2.getX() + push * normal.x, e2.getY());
            e1.setVelocity(-normal.x * spd, 0);
            e2.setVelocity( normal.x * spd, 0);
        } else {
            float push = overlapY / 2f + 1f;
            e1.setPosition(e1.getX(), e1.getY() - push * normal.y);
            e2.setPosition(e2.getX(), e2.getY() + push * normal.y);
            e1.setVelocity(0, -normal.y * spd);
            e2.setVelocity(0,  normal.y * spd);
        }
        e1.setInCollision(true);
        e2.setInCollision(true);
    }
}
