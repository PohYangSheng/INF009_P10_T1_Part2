package io.github.some_example_name.lwjgl3.application_classes.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.Collidable;
import io.github.some_example_name.lwjgl3.abstract_engine.entity.NonMovableEntity;

/**
 * Represents a wall tile in the game maze.
 */
public class Wall extends NonMovableEntity implements Collidable {
    private float width;
    private float height;

    /**
     * Constructor for wall tile.
     * 
     * @param name Wall name
     * @param position Position in the maze
     * @param texture Wall texture
     */
    public Wall(String name, Vector2 position, Texture texture) {
        super(name, position, texture);
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    /**
     * Sets the size of the wall tile.
     * 
     * @param width The width of the wall
     * @param height The height of the wall
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Get the bounding box of Entity
     * 
     * @return Rectangle Bounding Box
     */
    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(getPosition().x, getPosition().y, width, height);
    }
    
    /**
     * Draws Wall entity
     *
     * @param batch SpriteBatch to use for drawing
     */
    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(getTexture(), getPosition().x, getPosition().y, width, height);
    }
}