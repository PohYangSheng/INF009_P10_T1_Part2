package io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation;

import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.SoundManager;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Food;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.HealthyFood;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Player;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.UnhealthyFood;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.DialogPopUpManager;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.GameStateManager;

/**
 * Resolves Player ↔ Food collisions.
 *
 * Design Pattern: Strategy – one concrete collision resolution algorithm
 *   plugged into the engine's CollisionResolution dispatch table.
 *
 * OOP Polymorphism: instanceof checks dispatch to the correct food subtype
 *   (HealthyFood / UnhealthyFood) without coupling this class to Food internals.
 *
 * SOLID SRP fix: Sound instances are no longer static fields here.
 *   They are owned by SoundManager (injected), which has one responsibility:
 *   load, cache and play sounds. This removes the resource-leak risk that
 *   occurred when static sounds persisted across game sessions.
 */
public class PlayerFoodCollision {

    private static final String SFX_CRUNCH = "SFX/crunch.mp3";
    private static final String SFX_BAD    = "SFX/bad.mp3";

    public static void resolve(Player player, Food food,
                               GameStateManager gsm,
                               DialogPopUpManager dialogManager,
                               SoundManager soundManager) {
        if (food.shouldBeRemoved()) return;

        food.applyEffect(player);

        // Play the appropriate SFX via the injected SoundManager (SRP)
        if      (food instanceof UnhealthyFood) soundManager.play(SFX_BAD);
        else if (food instanceof HealthyFood)   soundManager.play(SFX_CRUNCH);

        // Track junk food count → trigger BAD state after 3 unhealthy foods
        if (food instanceof UnhealthyFood
                && gsm.getCurrentState() == GameStateManager.GameState.NORMAL) {
            gsm.incrementJunkFood();
            if (gsm.getJunkFoodEaten() >= 3) {
                gsm.setCurrentState(GameStateManager.GameState.BAD);
                gsm.resetJunkFoodCount();
            }
        }

        food.markForRemoval();

        // 25% chance to show a nutrition fun-fact dialog (Observer pattern)
        if (MathUtils.random() < 0.25f) {
            dialogManager.showPopUp("Did you know?", food.getFunFact(), food.getTexture());
        }
    }
}
