package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Food;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.HealthyFood;

import java.util.Map;

// creates healthy food objects
public class HealthyFoodFactory implements IFoodFactory {

    private static final int HEALTH_BONUS_MIN = 4;
    private static final int HEALTH_BONUS_MAX = 18;

    private final Map<String, Texture> textureCache;

    // constructor
    public HealthyFoodFactory(Map<String, Texture> textureCache) {
        this.textureCache = textureCache;
    }

    // make a healthy food item at the given position
    @Override
    public Food create(String id, float px, float py, float spriteW,
                       float lifespan, String[] entry) {
        Texture tex   = textureCache.get(entry[0]);
        int     bonus = MathUtils.random(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);
        return new HealthyFood(id, px, py, tex, bonus, lifespan, entry[1], spriteW);
    }
}
