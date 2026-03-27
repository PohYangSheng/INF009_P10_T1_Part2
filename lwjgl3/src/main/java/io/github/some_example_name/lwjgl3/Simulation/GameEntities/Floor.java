package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.StaticEntity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

// floor tile - just visual, no collision
public class Floor extends StaticEntity {

    // constructor
    public Floor(String name, float x, float y, Texture texture, float cellSize) {
        super(name, x, y, null);
        setTexture(texture);
        setSize(cellSize, cellSize);
    }

    @Override public void onCollision(iCollidable other) {
         
    }

    // draw everything to screen
    @Override
    public void render(SpriteBatch batch) {
        if (getTexture() != null)
            batch.draw(getTexture(), getX(), getY(), getRenderWidth(), getRenderHeight());
    }
}
