package io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation;

import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.SoundManager;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Dietitian;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Player;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.FoodFactPopUpManager;

// handles player touching the dietitian
public class PlayerDietitianCollision {

    private static final String SFX_DIETITIAN = "sound_fx/health_advice.mp3";

    // handle what happens after the collision
    public static void resolve(Player player, Dietitian dietitian,
                               FoodFactPopUpManager dialogManager,
                               SoundManager soundManager) {
        if (dietitian.isPendingRemoval()) return;

        soundManager.play(SFX_DIETITIAN);
        player.modifyHealth(5);
        player.modifyPoints(20);
        dietitian.markForRemoval();

        dialogManager.displayPopUp("Nutrition Tip!", dietitian.getDietitianTip(),
                dietitian.getTexture());
    }
}
