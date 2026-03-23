package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.StaticEntity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

/**
 * Impassable maze wall tile.
 * Extends StaticEntity (engine) and overrides onCollision with wall-specific behaviour.
 */
public class Wall extends StaticEntity {

    public Wall(String name, float x, float y, Texture texture, float cellSize) {
        super(name, x, y, null);
        setTexture(texture);
        setSize(cellSize, cellSize);
        bounds = new Rectangle(x, y, cellSize, cellSize);
    }

    @Override
    public void onCollision(iCollidable other) { /* walls don't react */ }

    @Override
    public void render(SpriteBatch batch) {
        if (getTexture() != null)
            batch.draw(getTexture(), getX(), getY(), getRenderWidth(), getRenderHeight());
    }

    /** Swap texture (used for BAD/SECRET mode visual change). */
    public void swapTexture(Texture newTexture) { setTexture(newTexture); }
}
