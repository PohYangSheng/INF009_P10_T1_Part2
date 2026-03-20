package io.github.some_example_name.lwjgl3.application_classes.collision_detectors;

import io.github.some_example_name.lwjgl3.abstract_engine.audio.AudioPlayer;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.EntityCollisionDetector;
import io.github.some_example_name.lwjgl3.application_classes.entities.Enemy;
import io.github.some_example_name.lwjgl3.application_classes.entities.Player;
import io.github.some_example_name.lwjgl3.application_classes.utilities.GameStateManager;

/**
 * Detects and resolves collisions between the player and an enemy.
 *
 * Behaviour depends on the current game state:
 *   NORMAL / BAD  – player loses one life; if no lives remain the game ends.
 *   SECRET        – player eats the enemy (+100 points), enemy is removed.
 */
public class PlayerCollideEnemyDetector extends EntityCollisionDetector<Player, Enemy> {
    private final Player player;
    private final Enemy enemy;
    private boolean collisionDetected;

    public PlayerCollideEnemyDetector(Player player, Enemy enemy) {
        super(player, enemy);
        this.player = player;
        this.enemy  = enemy;
    }

    @Override
    public void resolveCollision() {
        if (collisionDetected) return;
        collisionDetected = true;

        GameStateManager manager = GameStateManager.getInstance();

        if (manager.getCurrentState() == GameStateManager.GameState.SECRET) {
            // SECRET mode: eat the enemy
            AudioPlayer.getInstance().playSFX("SFX/crunch.mp3");
            enemy.setToBeRemoved();
            player.adjustPoints(100);
        } else {
            // NORMAL / BAD mode: lose a life
            AudioPlayer.getInstance().playSFX("SFX/bad.mp3");
            manager.loseLife();
            // Setting health to 0 signals PlayScene to check lives and respawn/end
            player.setHealth(0);
        }
    }
}
