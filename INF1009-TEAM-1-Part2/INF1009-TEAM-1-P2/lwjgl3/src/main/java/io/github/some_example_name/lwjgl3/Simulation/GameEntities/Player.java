package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.MovableEntity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

/**
 * Player-controlled character.
 * Extends MovableEntity (engine) – movement driven by UserMovement (Strategy).
 * OOP: Polymorphic onCollision override; iCollidable via MovableEntity.
 * FIX: adjustSpeed() now uses absolute px/s values (range 60-500) so food
 *      effects are actually noticeable at game speed of 150-300 px/s.
 */
public class Player extends MovableEntity {

    private static final int   DEFAULT_MAX_HEALTH   = 100;
    private static final int   DEFAULT_START_POINTS = 100;
    private static final float MIN_SPEED            = 60f;
    private static final float MAX_SPEED            = 500f;

    private final float renderSize;
    private int   maxHealth;
    private int   health;
    private int   points;

    public Player(String name, float x, float y, Texture texture, float speed, float renderSize) {
        super(name, x, y, null, speed);
        setTexture(texture);
        this.renderSize = renderSize;
        this.maxHealth  = DEFAULT_MAX_HEALTH;
        this.health     = DEFAULT_MAX_HEALTH;
        this.points     = DEFAULT_START_POINTS;
        this.bounds     = new Rectangle(x, y, renderSize, renderSize);
    }

    public int  getHealth()    { return health; }
    public int  getMaxHealth() { return maxHealth; }

    public void setHealth(int value) {
        health = Math.max(0, Math.min(value, maxHealth));
    }
    public void adjustHealth(int amount) { setHealth(health + amount); }

    public int  getPoints()              { return points; }
    public void adjustPoints(int amount) { points = Math.max(0, points + amount); }

    /** Adjust speed by absolute px/s delta, clamped to playable range. */
    public void adjustSpeed(float delta) {
        setSpeed(Math.max(MIN_SPEED, Math.min(MAX_SPEED, getSpeed() + delta)));
    }

    @Override
    public Rectangle getBounds() {
        bounds.setPosition(getX(), getY());
        return bounds;
    }

    @Override
    public void onCollision(iCollidable other) { /* handled by collision handlers */ }

    @Override
    public void render(SpriteBatch batch) {
        if (getTexture() != null)
            batch.draw(getTexture(), getX(), getY(), renderSize, renderSize);
    }
}
