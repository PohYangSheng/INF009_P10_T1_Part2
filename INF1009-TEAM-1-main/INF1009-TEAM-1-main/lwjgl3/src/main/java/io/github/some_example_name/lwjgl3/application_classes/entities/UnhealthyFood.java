package io.github.some_example_name.lwjgl3.application_classes.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents an unhealthy food item in the game.
 */
public class UnhealthyFood extends Food {
    private final int damage;
    
    /**
     * Constructor for unhealthy food.
     */
    public UnhealthyFood(String name, Vector2 position, Texture texture, 
                         int damage, float lifespanInSeconds, String funFact) {
        super(name, position, texture, FoodType.UNHEALTHY, lifespanInSeconds, funFact);
        this.damage = damage;
    }
    
    /**
     * Alternate constructor with default values.
     */
    public UnhealthyFood(Vector2 position, Texture texture, String funFact) {
        super("UnhealthyFood", position, texture, FoodType.UNHEALTHY, 10f, funFact);
        this.damage = 10; // Default damage
    }
    
    /**
     * Get damage caused by this food.
     * 
     * @return Damage value
     */
    public int getDamage() {
        return damage;
    }
    
    @Override
    public int getEffectValue() {
        return -damage; // Negative to indicate damage
    }

    @Override
    public void applyEffect(Player player) {
        player.adjustHealth(-damage);
        player.adjustPoints(-10);
        player.adjustSpeed(-0.1f);
    }
}