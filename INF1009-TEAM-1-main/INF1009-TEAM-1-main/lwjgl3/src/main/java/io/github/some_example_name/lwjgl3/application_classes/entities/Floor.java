package io.github.some_example_name.lwjgl3.application_classes.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.NonMovableEntity;

/**
 * Represents a floor tile in the game maze.
 */
public class Floor extends NonMovableEntity {
    private float width;
    private float height;

    /**
     * Constructor for floor tile.
     * 
     * @param name Floor name
     * @param position Position in the maze
     * @param texture Floor texture
     */
    public Floor(String name, Vector2 position, Texture texture) {
        super(name, position, texture);
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    /**
     * Sets the size of the floor tile.
     * 
     * @param width The width of the floor
     * @param height The height of the floor
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Draws floor entity
     *
     * @param batch SpriteBatch to use for drawing
     */
    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(getTexture(), getPosition().x, getPosition().y, width, height);
    }
}