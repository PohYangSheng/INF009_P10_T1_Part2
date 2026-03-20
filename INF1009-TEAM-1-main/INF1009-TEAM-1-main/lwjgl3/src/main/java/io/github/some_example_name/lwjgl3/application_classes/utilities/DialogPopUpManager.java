package io.github.some_example_name.lwjgl3.application_classes.utilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Manages dialog popups in the game.
 * Ensures only one popup is shown at a time and coordinates
 * communication between dialogs and game scenes.
 */
public class DialogPopUpManager {
    /** The actual dialog popup instance */
    private final DialogPopUp dialogPopup;
    
    /** Listener for dialog events (typically the current scene) */
    private DialogEventListener listener;
     
    /**
     * Creates a new dialog popup manager.
     * 
     * @param stage The stage to render dialogs on
     * @param skin The skin to use for UI components
     */
    public DialogPopUpManager(Stage stage, Skin skin) {
        dialogPopup = new DialogPopUp(stage, skin);
    }

    /**
     * Shows a popup with the specified title and fact.
     * 
     * @param title The popup title
     * @param fact The popup content text
     */
    public void showPopUp(String title, String fact) {
        showPopUp(title, fact, null);
    }

    /**
     * Shows a popup with the specified title, fact, and optional image.
     * Only shows if no popup is currently visible.
     * 
     * @param title The popup title
     * @param fact The popup content text
     * @param image Optional texture to display (can be null)
     */
    public void showPopUp(String title, String fact, Texture image) {
        if (!dialogPopup.isVisible() && listener != null) {
            dialogPopup.show(title, fact, image);
            listener.onDialogOpened();
            
            // Set callback function in dialog pop up to notify observer when the popup closes
            dialogPopup.setCloseCallback(()->{
                // uses listener's (Play Scene) function to resume game
                listener.onDialogClosed();
            });
        }
    }
    
    /**
     * Sets the listener that will receive dialog events.
     * The listener is typically the current game scene.
     * 
     * @param listener The object that will receive dialog events
     */
    public void setDialogEventListener(DialogEventListener listener) {
        this.listener = listener;
    }
    
    /**
     * Renders the current dialog popup if visible.
     * 
     * @param batch The sprite batch for rendering
     */
    public void render(SpriteBatch batch) {
        dialogPopup.render(batch);
    }
}