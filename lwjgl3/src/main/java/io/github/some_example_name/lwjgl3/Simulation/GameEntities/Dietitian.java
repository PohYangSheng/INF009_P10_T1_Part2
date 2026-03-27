package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.MovableEntity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

// friendly NPC that gives health tips when you touch it
public class Dietitian extends MovableEntity {

    private final float  renderSize;
    private final String funFact;
    private       boolean toBeRemoved = false;

    // constructor
    public Dietitian(String name, float x, float y, Texture texture,
                     float speed, float renderSize, String funFact) {
        super(name, x, y, null, speed);
        setTexture(texture);
        this.renderSize = renderSize;
        this.funFact    = funFact;
        this.bounds     = new Rectangle(x, y, renderSize, renderSize);
    }

    // runs every frame
    @Override
    public void update(float dt) {
        if (!isActive()) return;
        super.update(dt);               
    }

    // getter for dietitian tip
    public String  getDietitianTip() {
        return funFact;
    }
    // check if pending removal
    public boolean isPendingRemoval() {
        return toBeRemoved;
    }

    // flag for removal
    public void markForRemoval() {
        toBeRemoved = true;
        setActive(false);
        stop();
    }

    // getter for bounds
    @Override
    public Rectangle getBounds() {
        bounds.setPosition(getX(), getY());
        return bounds;
    }

    // on collision
    @Override
    public void onCollision(iCollidable other) {
         
    }

    // draw everything to screen
    @Override
    public void render(SpriteBatch batch) {
        if (isActive() && getTexture() != null)
            batch.draw(getTexture(), getX(), getY(), renderSize, renderSize);
    }
}
