package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.StaticEntity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

// base class for food items - healthy and unhealthy extend this
public abstract class Food extends StaticEntity {

    public enum FoodType { HEALTHY, UNHEALTHY }

    protected final FoodType foodType;
    protected final String   funFact;
    protected final float    lifespanSeconds;
    protected       float    elapsedTime  = 0f;
    protected       boolean  expired      = false;
    protected       boolean  toBeRemoved  = false;
    protected       float    renderSize;

    // constructor
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

    // runs every frame
    @Override
    public void update(float deltaTime) {
        if (!toBeRemoved && !expired) {
            elapsedTime += deltaTime;
            if (elapsedTime >= lifespanSeconds) {
                expired = true;
                markForRemoval();   
            }
        }
    }

    public abstract void activateEffect(Player player);
    public abstract int  getEffectMagnitude();

    public abstract String getCollisionSfx();

    // getter for food type
    public FoodType getFoodType() {
        return foodType;
    }
    // getter for dietitian tip
    public String   getDietitianTip() {
        return funFact;
    }
    // check if this food item has reached the end of its lifespan
    public boolean  hasExpired() {
        return expired;
    }
    // check if pending removal
    public boolean  isPendingRemoval() {
        return toBeRemoved;
    }

    // flag for removal
    public void markForRemoval() {
        toBeRemoved = true;
        setActive(false);   
    }

    // get remaining lifetime (0 to 1)
    public float getLifeRemaining() {
        return Math.max(0f, 1f - elapsedTime / lifespanSeconds);
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
        if (!toBeRemoved && !expired && getTexture() != null)
            batch.draw(getTexture(), getX(), getY(), renderSize, renderSize);
    }
}
