package io.github.some_example_name.lwjgl3.application_classes.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents a healthy food item in the game.
 */
public class HealthyFood extends Food {
    private final int healthPoints;
    
    /**
     * Constructor for healthy food.
     */
    public HealthyFood(String name, Vector2 position, Texture texture, 
                       int healthPoints, float lifespanInSeconds, String funFact) {
        super(name, position, texture, FoodType.HEALTHY, lifespanInSeconds, funFact);
        this.healthPoints = healthPoints;
    }
    
    /**
     * Alternate constructor with default values.
     */
    public HealthyFood(Vector2 position, Texture texture, String funFact) {
        super("HealthyFood", position, texture, FoodType.HEALTHY, 10f, funFact);
        this.healthPoints = 10; // Default health restoration
    }
    
    /**
     * Get health points restored by this food.
     * 
     * @return Health points value
     */
    public int getHealthPoints() {
        return healthPoints;
    }
    
    @Override
    public int getEffectValue() {
        return healthPoints;
    }

    @Override
    public void applyEffect(Player player){
        player.adjustHealth(healthPoints);
        player.adjustPoints(20);
        player.adjustSpeed(0.1f);
    }
}