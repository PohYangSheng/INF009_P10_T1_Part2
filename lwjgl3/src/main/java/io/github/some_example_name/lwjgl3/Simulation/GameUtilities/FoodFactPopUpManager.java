package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

// manages the food fact popup display
public class FoodFactPopUpManager {

    private final FoodFactPopUp dialog;

    private IFoodFactEventListener eventObserver;

    // constructor
    public FoodFactPopUpManager(Stage stage, Skin skin) {
        this.dialog = new FoodFactPopUp(stage, skin);
    }

    // setter for popup listener
    public void setPopupListener(IFoodFactEventListener observer) {
        this.eventObserver = observer;
    }

    // display pop up
    public void displayPopUp(String title, String body) {
        requestPopUp(title, body, null);
    }

    // display pop up
    public void displayPopUp(String title, String body, Texture icon) {
        requestPopUp(title, body, icon);
    }

    // render popup if active, also check for enter key dismiss
    public void render(SpriteBatch batch) {
        if (dialog.isShowing() &&
                (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                 Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER))) {
            dialog.hide();
            if (eventObserver != null) eventObserver.onPopupDismissed();
        }
        dialog.render(batch);
    }

    // show a popup if one isnt already showing
    private void requestPopUp(String title, String body, Texture icon) {
        if (dialog.isShowing() || eventObserver == null) return;

        dialog.show(title, body, icon);
        eventObserver.onPopupShown();
        dialog.setDismissCallback(() -> eventObserver.onPopupDismissed());
    }

    // clean up textures/resources so we dont leak memory
    public void dispose() {
        if (dialog != null) dialog.dispose();
    }
}
