package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Food;

// factory interface for creating food
public interface IFoodFactory {

    Food create(String id, float px, float py, float spriteW,
                float lifespan, String[] entry);
}
