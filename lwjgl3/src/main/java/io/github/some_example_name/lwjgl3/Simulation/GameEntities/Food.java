package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.StaticEntity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

/**
 * Abstract base for all food items in the maze.
 *
 * BUG FIX: markForRemoval() previously only set toBeRemoved = true but never
 * called setActive(false). EntityManager.updateEntities() removes entities where
 * isActive() == false, so food was NEVER removed from the entity map – it stayed
 * visible and kept colliding forever.
 *
 * FIX: markForRemoval() now also calls setActive(false) so EntityManager
 * prunes the entity on the next updateEntities() call.
 *
 * OOP: Abstract class with Template Method pattern.
 * SOLID: OCP – new food types added by subclassing only.
 */
public abstract class Food extends StaticEntity {

    public enum FoodType { HEALTHY, UNHEALTHY }

    protected final FoodType foodType;
    protected final String   funFact;
    protected final float    lifespanSeconds;
    protected       float    elapsedTime  = 0f;
    protected       boolean  expired      = false;
    protected       boolean  toBeRemoved  = false;
    protected       float    renderSize;

    protected Food(String name, float x, float y, Texture texture,
                   FoodType foodType, float lifespanSeconds, String funFact, float renderSize) {
        super(name, x, y, null);
        setTexture(texture);
        this.foodType        = foodType;
        this.lifespanSeconds = lifespanSeconds;
        this.funFact         = funFact;
        this.renderSize      = renderSize;
        this.bounds          = new Rectangle(x, y, renderSize, renderSize);
        setSize(renderSize, renderSize);
    }

    // ── Per-frame ──────────────────────────────────────────────────────────

    @Override
    public void update(float deltaTime) {
        if (!toBeRemoved && !expired) {
            elapsedTime += deltaTime;
            if (elapsedTime >= lifespanSeconds) {
                expired = true;
                markForRemoval();   // use markForRemoval so setActive(false) is always called
            }
        }
    }

    // ── Abstract hook – Template Method ───────────────────────────────────

    public abstract void applyEffect(Player player);
    public abstract int  getEffectValue();

    // ── State ──────────────────────────────────────────────────────────────

    public FoodType getFoodType()    { return foodType; }
    public String   getFunFact()     { return funFact; }
    public boolean  isExpired()      { return expired; }
    public boolean  shouldBeRemoved(){ return toBeRemoved; }

    /**
     * BUG FIX: also call setActive(false) so EntityManager removes the entity.
     * Previously only toBeRemoved was set, leaving the entity permanently in the map.
     */
    public void markForRemoval() {
        toBeRemoved = true;
        setActive(false);   // ← KEY FIX: tells EntityManager to remove this entity
    }

    public float getRemainingLifePercentage() {
        return Math.max(0f, 1f - elapsedTime / lifespanSeconds);
    }

    // ── iCollidable ────────────────────────────────────────────────────────

    @Override
    public Rectangle getBounds() {
        bounds.setPosition(getX(), getY());
        return bounds;
    }

    @Override
    public void onCollision(iCollidable other) { /* handled by collision handlers */ }

    // ── Rendering ──────────────────────────────────────────────────────────

    @Override
    public void render(SpriteBatch batch) {
        if (!toBeRemoved && !expired && getTexture() != null)
            batch.draw(getTexture(), getX(), getY(), renderSize, renderSize);
    }
}
