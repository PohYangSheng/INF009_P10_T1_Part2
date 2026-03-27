package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Food;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.UnhealthyFood;

import java.util.Map;

// creates unhealthy food objects
public class UnhealthyFoodFactory implements IFoodFactory {

    private static final int DAMAGE_MIN = 4;
    private static final int DAMAGE_MAX = 18;

    private final Map<String, Texture> textureCache;

    // constructor
    public UnhealthyFoodFactory(Map<String, Texture> textureCache) {
        this.textureCache = textureCache;
    }

    // make an unhealthy food item at the given position
    @Override
    public Food create(String id, float px, float py, float spriteW,
                       float lifespan, String[] entry) {
        Texture tex    = textureCache.get(entry[0]);
        int     damage = MathUtils.random(DAMAGE_MIN, DAMAGE_MAX);
        return new UnhealthyFood(id, px, py, tex, damage, lifespan, entry[1], spriteW);
    }
}
