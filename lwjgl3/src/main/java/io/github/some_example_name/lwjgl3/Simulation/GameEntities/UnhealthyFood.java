package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;

/**
 * Unhealthy food: deals damage, deducts points and slows player.
 * OOP: Concrete subclass of Food (Template Method hook applyEffect).
 * FIX: adjustSpeed(-10f) instead of -0.1f – noticeable at game speed 150-300 px/s.
 */
public class UnhealthyFood extends Food {

    private final int damage;

    public UnhealthyFood(String name, float x, float y, Texture texture,
                         int damage, float lifespan, String funFact, float renderSize) {
        super(name, x, y, texture, FoodType.UNHEALTHY, lifespan, funFact, renderSize);
        this.damage = damage;
    }

    @Override
    public void applyEffect(Player player) {
        player.adjustHealth(-damage);
        player.adjustPoints(-10);
        player.adjustSpeed(-10f);
    }

    @Override public int getEffectValue() { return -damage; }
}
