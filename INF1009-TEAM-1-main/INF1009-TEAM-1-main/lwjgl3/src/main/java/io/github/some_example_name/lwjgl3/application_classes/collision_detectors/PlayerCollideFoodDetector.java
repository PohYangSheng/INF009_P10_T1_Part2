package io.github.some_example_name.lwjgl3.application_classes.collision_detectors;

import com.badlogic.gdx.math.MathUtils;

import io.github.some_example_name.lwjgl3.abstract_engine.audio.AudioManager;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.EntityCollisionDetector;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Removable;
import io.github.some_example_name.lwjgl3.application_classes.entities.Food;
import io.github.some_example_name.lwjgl3.application_classes.entities.HealthyFood;
import io.github.some_example_name.lwjgl3.application_classes.entities.Player;
import io.github.some_example_name.lwjgl3.application_classes.entities.UnhealthyFood;
import io.github.some_example_name.lwjgl3.application_classes.utilities.DialogPopUpManager;
import io.github.some_example_name.lwjgl3.application_classes.utilities.GameStateManager;

/**
 * Detects and resolves collisions between the player and food items.
 *
 * When an UnhealthyFood is eaten while in NORMAL state, the junk-food counter
 * in GameStateManager is incremented.  Reaching 3 junk foods triggers BAD state.
 */
public class PlayerCollideFoodDetector extends EntityCollisionDetector<Player, Food> implements Removable {
    private final Player player;
    private final Food food;
    private final DialogPopUpManager dialogPopUpManager;
    private boolean toBeRemoved;

    public PlayerCollideFoodDetector(Player player, Food food, DialogPopUpManager dialogManager) {
        super(player, food);
        this.player = player;
        this.food = food;
        this.dialogPopUpManager = dialogManager;
        this.toBeRemoved = false;
    }

    @Override
    public void resolveCollision() {
        if (food.shouldBeRemoved()) return;

        // Play appropriate SFX
        if (food instanceof HealthyFood) {
            AudioManager.getInstance().playSFX("SFX/crunch.mp3");
        } else if (food instanceof UnhealthyFood) {
            AudioManager.getInstance().playSFX("SFX/bad.mp3");
        }

        food.applyEffect(player);

        // Track junk food for BAD-mode trigger (only when currently NORMAL)
        if (food instanceof UnhealthyFood) {
            GameStateManager manager = GameStateManager.getInstance();
            if (manager.getCurrentState() == GameStateManager.GameState.NORMAL) {
                manager.incrementJunkFood();
                if (manager.getJunkFoodEaten() >= 3) {
                    manager.setCurrentState(GameStateManager.GameState.BAD);
                    manager.resetJunkFoodCount();
                }
            }
        }

        food.setToBeRemoved();
        setToBeRemoved();

        // Show popup with 25% probability
        if (MathUtils.random() < 0.25f) {
            dialogPopUpManager.showPopUp("Did you know?", food.getFunFact(), food.getTexture());
        }
    }

    @Override
    public void setToBeRemoved() {
        toBeRemoved = true;
    }

    @Override
    public boolean shouldBeRemoved() {
        return toBeRemoved;
    }
}
