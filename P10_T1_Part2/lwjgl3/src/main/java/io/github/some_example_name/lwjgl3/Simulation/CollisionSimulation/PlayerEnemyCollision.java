package io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation;

import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.SoundManager;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Enemy;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Player;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.GameStateManager;

/**
 * Resolves Player ↔ Enemy collisions.
 *
 * Design Pattern: Strategy – concrete collision resolution algorithm.
 *
 * SOLID SRP fix: SFX played via injected SoundManager, not static Sound fields.
 */
public class PlayerEnemyCollision {

    private static final String SFX_BAD = "SFX/bad.mp3";

    public static void resolve(Player player, Enemy enemy,
                               GameStateManager gsm,
                               SoundManager soundManager) {
        if (enemy.shouldBeRemoved()) return;

        if (gsm.getCurrentState() == GameStateManager.GameState.SECRET) {
            // SECRET mode: player eats the enemy for bonus points
            enemy.markForRemoval();
            player.adjustPoints(100);
        } else {
            soundManager.play(SFX_BAD);
            gsm.loseLife();
            player.setHealth(0);
        }
    }
}
