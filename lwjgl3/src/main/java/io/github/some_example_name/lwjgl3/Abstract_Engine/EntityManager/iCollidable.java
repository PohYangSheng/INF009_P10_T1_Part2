package io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager;

import com.badlogic.gdx.math.Rectangle;

// interface for things that can collide
public interface iCollidable {
    Rectangle getBounds();
    void      onCollision(iCollidable other);
}
