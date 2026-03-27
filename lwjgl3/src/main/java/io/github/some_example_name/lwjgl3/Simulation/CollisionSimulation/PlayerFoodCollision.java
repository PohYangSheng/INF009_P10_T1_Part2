package io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation;

import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.SoundManager;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Food;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Player;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.FoodFactPopUpManager;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.GameProgressTracker;

// handles player picking up food
public class PlayerFoodCollision {

    // handle what happens after the collision
    public static void resolve(Player player, Food food,
                               GameProgressTracker gsm,
                               FoodFactPopUpManager dialogManager,
                               SoundManager soundManager) {
        if (food.isPendingRemoval()) return;

        food.activateEffect(player);

        soundManager.play(food.getCollisionSfx());

        if (food.getFoodType() == Food.FoodType.UNHEALTHY
                && gsm.getCurrentState() == GameProgressTracker.GameState.NORMAL) {
            gsm.incrementJunkFood();
            if (gsm.getJunkFoodEaten() >= 3) {
                gsm.setCurrentState(GameProgressTracker.GameState.BAD);
                gsm.resetJunkFoodCount();
            }
        }

        food.markForRemoval();

        if (MathUtils.random() < 0.25f) {

            dialogManager.displayPopUp("Did you know?", food.getDietitianTip(), food.getTexture());

        }
    }
}
