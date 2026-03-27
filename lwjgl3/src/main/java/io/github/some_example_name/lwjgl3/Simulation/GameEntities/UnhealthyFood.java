package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;

// junk food - damages player and takes points
public class UnhealthyFood extends Food {

    private final int damage;

    // constructor
    public UnhealthyFood(String name, float x, float y, Texture texture,
                         int damage, float lifespan, String funFact, float renderSize) {
        super(name, x, y, texture, FoodType.UNHEALTHY, lifespan, funFact, renderSize);
        this.damage = damage;
    }

    // apply the food effect to player
    @Override
    public void activateEffect(Player player) {
        player.modifyHealth(-damage);
        player.modifyPoints(-10);
        player.modifySpeed(-10f);
    }

    @Override public int    getEffectMagnitude() {
        return -damage;
    }
    @Override public String getCollisionSfx() {
        return "sound_fx/damage_alert.mp3";
    }
}
