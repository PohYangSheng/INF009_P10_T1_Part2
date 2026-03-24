package io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation;

import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.SoundManager;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Nutritionist;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Player;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.DialogPopUpManager;

/**
 * Resolves Player ↔ Nutritionist collisions.
 *
 * Design Pattern: Strategy – concrete collision resolution algorithm.
 *
 * SOLID SRP fix: SFX played via injected SoundManager, not static Sound fields.
 */
public class PlayerNutritionistCollision {

    private static final String SFX_NUTRITIONIST = "SFX/nutritionist.mp3";

    public static void resolve(Player player, Nutritionist nutritionist,
                               DialogPopUpManager dialogManager,
                               SoundManager soundManager) {
        if (nutritionist.shouldBeRemoved()) return;

        soundManager.play(SFX_NUTRITIONIST);
        player.adjustHealth(5);
        player.adjustPoints(20);
        nutritionist.markForRemoval();

        dialogManager.showPopUp("Nutrition Tip!", nutritionist.getFunFact(),
                nutritionist.getTexture());
    }
}
