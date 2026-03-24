package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;

/**
 * Healthy food: restores health, grants points and speeds up player.
 * OOP: Concrete subclass of Food (Template Method hook applyEffect).
 * FIX: adjustSpeed(+10f) instead of 0.1f – noticeable at game speed 150-300 px/s.
 */
public class HealthyFood extends Food {

    private final int healthPoints;

    public HealthyFood(String name, float x, float y, Texture texture,
                       int healthPoints, float lifespan, String funFact, float renderSize) {
        super(name, x, y, texture, FoodType.HEALTHY, lifespan, funFact, renderSize);
        this.healthPoints = healthPoints;
    }

    @Override
    public void applyEffect(Player player) {
        player.adjustHealth(healthPoints);
        player.adjustPoints(20);
        player.adjustSpeed(10f);
    }

    @Override public int getEffectValue() { return healthPoints; }
}
