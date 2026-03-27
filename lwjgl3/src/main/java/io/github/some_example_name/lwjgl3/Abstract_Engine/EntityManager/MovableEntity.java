package io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

// base class for anything that moves (player, enemies etc)
public class MovableEntity extends Entity implements iMovable, iCollidable {

    private float speed;
    private float vx;
    private float vy;

    // constructor
    public MovableEntity(String name, float x, float y, String texturePath, float speed) {
        super(name, x, y, texturePath);
        this.speed = speed;
    }

    @Override public void  setVelocity(float vx, float vy) {
        this.vx = vx; this.vy = vy;
    }
    @Override public void  stop() {
        vx = 0f; vy = 0f;
    }
    @Override public float getSpeed() {
        return speed;
    }
    @Override public float getVx() {
        return vx;
    }
    @Override public float getVy() {
        return vy;
    }

    // set the speed
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    // runs every frame
    @Override
    public void update(float deltaTime) {
        if (vx != 0f || vy != 0f) moveBy(vx * deltaTime, vy * deltaTime);
    }

    // getter for collision bounds for this movable entity
    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    // called when this thing collides with something
    @Override
    public void onCollision(iCollidable other) {
        
    }

    // draw everything to screen
    public void render(SpriteBatch batch, float w, float h) {
        if (getTexture() != null) batch.draw(getTexture(), getX(), getY(), w, h);
    }
}
