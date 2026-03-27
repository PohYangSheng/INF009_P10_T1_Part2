package io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation;

import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.SoundManager;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Enemy;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Player;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.GameProgressTracker;

// handles player touching an enemy
public class PlayerEnemyCollision {

    private static final String SFX_BAD = "sound_fx/damage_alert.mp3";

    // handle what happens after the collision
    public static void resolve(Player player, Enemy enemy,
                               GameProgressTracker gsm,
                               SoundManager soundManager) {
        if (enemy.isPendingRemoval()) return;

        if (gsm.getCurrentState() == GameProgressTracker.GameState.SECRET) {
            
            enemy.markForRemoval();
            player.modifyPoints(100);
        } else {
            soundManager.play(SFX_BAD);
            gsm.loseLife();
            player.setCurrentHealth(0);
        }
    }
}
