package io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager;

import com.badlogic.gdx.math.Rectangle;

/**
 * Interface for entities that participate in collision detection.
 * Follows Interface Segregation Principle (ISP).
 */
public interface iCollidable {
    Rectangle getBounds();
    void      onCollision(iCollidable other);
}
