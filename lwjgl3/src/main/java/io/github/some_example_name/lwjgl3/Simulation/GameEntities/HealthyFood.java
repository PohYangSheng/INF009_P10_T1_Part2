package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;

// healthy food - heals player and gives points
public class HealthyFood extends Food {

    private final int healthPoints;

    // constructor
    public HealthyFood(String name, float x, float y, Texture texture,
                       int healthPoints, float lifespan, String funFact, float renderSize) {
        super(name, x, y, texture, FoodType.HEALTHY, lifespan, funFact, renderSize);
        this.healthPoints = healthPoints;
    }

    // apply the food effect to player
    @Override
    public void activateEffect(Player player) {
        player.modifyHealth(healthPoints);
        player.modifyPoints(20);
        player.modifySpeed(10f);
    }

    @Override public int    getEffectMagnitude() {
        return healthPoints;
    }
    @Override public String getCollisionSfx() {
        return "sound_fx/bite_crunch.mp3";
    }
}
