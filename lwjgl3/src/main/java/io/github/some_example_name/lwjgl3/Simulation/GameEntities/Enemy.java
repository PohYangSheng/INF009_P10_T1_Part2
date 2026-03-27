package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.Entity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.MovableEntity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

// enemy that chases the player around the maze
public class Enemy extends MovableEntity {

    private final float renderSize;
    private boolean inCollision  = false;
    private boolean toBeRemoved  = false;
    private Entity  target       = null;

    // constructor
    public Enemy(String name, float x, float y, Texture texture, float speed, float renderSize) {
        super(name, x, y, null, speed);
        setTexture(texture);
        this.renderSize = renderSize;
        this.bounds     = new Rectangle(x, y, renderSize, renderSize);
    }

    // runs every frame
    @Override
    public void update(float dt) {
        if (!isActive()) return;
        super.update(dt);               
    }

    // set what this enemy is chasing
    public void   setTarget(Entity target) {
        this.target = target;
    }
    // getter for target
    public Entity getTarget() {
        return target;
    }

    // set collision flag
    public void    setInCollision(boolean v) {
        this.inCollision = v;
    }
    // check if in collision
    public boolean isInCollision() {
        return inCollision;
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

    // get the hitbox
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
