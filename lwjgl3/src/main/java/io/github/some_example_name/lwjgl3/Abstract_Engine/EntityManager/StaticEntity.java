package io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

// for stuff that doesnt move like walls and floors
public class StaticEntity extends Entity implements iCollidable {

    private float renderWidth;
    private float renderHeight;

    // constructor
    public StaticEntity(String name, float x, float y, String texturePath) {
        super(name, x, y, texturePath);
        this.renderWidth  = getWidth();
        this.renderHeight = getHeight();
    }

    // runs every frame
    @Override
    public void update(float deltaTime) {}

    // set size
    public void  setSize(float w, float h) {
        renderWidth = w; renderHeight = h;
    }
    // getter for render width
    public float getRenderWidth() {
        return renderWidth;
    }
    // getter for render height
    public float getRenderHeight() {
        return renderHeight;
    }

    // getter for collision bounds for this static entity
    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    // called when this thing collides with something
    @Override
    public void onCollision(iCollidable other) {
         
    }

    // draw everything to screen
    @Override
    public void render(SpriteBatch batch) {
        if (getTexture() != null)
            batch.draw(getTexture(), getX(), getY(), renderWidth, renderHeight);
    }
}
