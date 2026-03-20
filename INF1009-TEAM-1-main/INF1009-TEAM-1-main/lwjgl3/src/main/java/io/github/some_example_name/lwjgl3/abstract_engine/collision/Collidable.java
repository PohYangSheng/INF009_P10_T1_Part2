package io.github.some_example_name.lwjgl3.abstract_engine.collision;

import com.badlogic.gdx.math.Rectangle;

/**
 * Interface for objects that can collide with other collidables.
 * Any entity that can participate in collisions should implement this.
 */
public interface Collidable {
    /**
     * Gets the bounding box used for collision detection.
     * 
     * @return Rectangle representing the collision bounds
     */
    Rectangle getBoundingBox();
}