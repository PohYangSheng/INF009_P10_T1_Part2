package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.MovableEntity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

// the player character - has health, points, speed
public class Player extends MovableEntity {

    private static final int   DEFAULT_MAX_HEALTH   = 100;
    private static final int   DEFAULT_START_POINTS = 100;
    private static final float MIN_SPEED            = 60f;
    private static final float MAX_SPEED            = 500f;

    private final float renderSize;
    private int   maxHealth;
    private int   health;
    private int   points;

    // constructor
    public Player(String name, float x, float y, Texture texture, float speed, float renderSize) {
        super(name, x, y, null, speed);
        setTexture(texture);
        this.renderSize = renderSize;
        this.maxHealth  = DEFAULT_MAX_HEALTH;
        this.health     = DEFAULT_MAX_HEALTH;
        this.points     = DEFAULT_START_POINTS;
        this.bounds     = new Rectangle(x, y, renderSize, renderSize);
    }

    // getter for current health
    public int  getCurrentHealth() {
        return health;
    }
    // getter for health cap
    public int  getHealthCap() {
        return maxHealth;
    }

    // setter for current health
    public void setCurrentHealth(int value) {
        health = Math.max(0, Math.min(value, maxHealth));
    }
    // change hp (clamped to 0-max)
    public void modifyHealth(int amount) {
        setCurrentHealth(health + amount);
    }

    // getter for points
    public int  getPoints() {
        return points;
    }
    // change score
    public void modifyPoints(int amount) {
        points = Math.max(0, points + amount);
    }

    // change player speed (clamped)
    public void modifySpeed(float delta) {
        setSpeed(Math.max(MIN_SPEED, Math.min(MAX_SPEED, getSpeed() + delta)));
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
        if (getTexture() != null)
            batch.draw(getTexture(), getX(), getY(), renderSize, renderSize);
    }
}
