package io.github.some_example_name.lwjgl3.application_classes.collision_detectors;

import io.github.some_example_name.lwjgl3.abstract_engine.audio.AudioManager;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.EntityCollisionDetector;
import io.github.some_example_name.lwjgl3.abstract_engine.collision.Removable;
import io.github.some_example_name.lwjgl3.application_classes.entities.Nutritionist;
import io.github.some_example_name.lwjgl3.application_classes.entities.Player;
import io.github.some_example_name.lwjgl3.application_classes.utilities.DialogPopUpManager;

/**
 * Detects and resolves collisions between the player and nutritionist entities.
 * When a collision occurs, the player receives health and a nutritional tip is displayed.
 */
public class PlayerCollideNutritionistDetector extends EntityCollisionDetector<Player, Nutritionist> implements Removable {
    private final Player player;
    private final Nutritionist nutritionist;
    private final DialogPopUpManager dialogPopUpManager;
    private boolean toBeRemoved = false;

    /**
     * Creates a new detector for player-nutritionist collisions.
     *
     * @param player The player entity
     * @param nutritionist The nutritionist entity
     * @param dialogPopUpManager The dialog popup manager for displaying tips
     */
    public PlayerCollideNutritionistDetector(Player player, Nutritionist nutritionist, DialogPopUpManager dialogPopUpManager) {
        super(player, nutritionist);
        this.player = player;
        this.nutritionist = nutritionist;
        this.dialogPopUpManager = dialogPopUpManager;
    }

    /**
     * Checks if a collision should be processed.
     * Prevents collision processing if the nutritionist is already removed.
     *
     * @return true if collision should be processed, false otherwise
     */
    @Override
    public boolean checkCollision() {
        if (nutritionist.shouldBeRemoved()) {
            return false;
        }
        return super.checkCollision();
    }

    /**
     * Resolves player-nutritionist collisions by giving the player health
     * and showing a nutritional tip popup.
     */
    @Override
    public void resolveCollision() {
        if (nutritionist.shouldBeRemoved()) return;
        
        // Play sound effect
        AudioManager.getInstance().playSFX("SFX/nutritionist.mp3");
        
        // Give player health bonus
        player.adjustHealth(5);

        // Give player points
        player.adjustPoints(20);

        // Mark nutritionist for removal
        nutritionist.setToBeRemoved();

        // Mark this detector for removal
        setToBeRemoved();

        // Show dialog popup with nutritionist tip
        dialogPopUpManager.showPopUp("Nutrition Tip!", nutritionist.getFunFact(), nutritionist.getTexture());
    }

    /**
     * Marks this detector to be removed.
     */
    @Override
    public void setToBeRemoved() {
        toBeRemoved = true;
    }

    /**
     * Checks if this detector should be removed.
     * 
     * @return true if it should be removed, false otherwise
     */
    @Override
    public boolean shouldBeRemoved() {
        return toBeRemoved;
    }
}