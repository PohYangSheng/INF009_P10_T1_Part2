package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Coordinates dialog card display and notifies the game scene when the
 * dialog opens or closes.
 *
 * Design Pattern: Observer (Subject side)
 *   This class is the Subject. It owns a {@link DialogPopUp} and notifies
 *   a registered {@link IDialogEventListener} observer (PlayScene) about
 *   open/close events. PlayScene pauses and resumes game logic without the
 *   dialog knowing anything about the scene.
 *
 * OOP:
 *   Encapsulation – the DialogPopUp instance is private; callers only call
 *                   showPopUp() and render().
 *   Polymorphism  – the listener is stored as the interface type so any
 *                   observer can be registered without changing this class.
 *
 * SOLID:
 *   SRP  – one responsibility: decide whether to show a dialog and relay
 *          open/close events; no game logic or rendering decisions here.
 *   OCP  – new dialog trigger conditions are added in the caller, not here.
 *   DIP  – depends on IDialogEventListener (interface), not PlayScene directly.
 */
public class DialogPopUpManager {

    // ── Components ────────────────────────────────────────────────────────

    private final DialogPopUp dialog;

    // ── Observer ──────────────────────────────────────────────────────────

    /** The observer notified on open/close. Typically PlayScene. */
    private IDialogEventListener eventObserver;

    // ── Construction ──────────────────────────────────────────────────────

    public DialogPopUpManager(Stage stage, Skin skin) {
        this.dialog = new DialogPopUp(stage, skin);
    }

    // ── Observer registration ─────────────────────────────────────────────

    /**
     * Registers the observer for dialog events.
     *
     * Observer pattern: the Subject stores the observer as an interface
     * reference, removing any concrete coupling.
     */
    public void setDialogEventListener(IDialogEventListener observer) {
        this.eventObserver = observer;
    }

    // ── Show ──────────────────────────────────────────────────────────────

    /** Show a dialog card without an icon. */
    public void showPopUp(String title, String body) {
        requestPopUp(title, body, null);
    }

    /**
     * Show a dialog card with an optional food icon.
     *
     * Only shown if no card is currently open AND an observer is registered.
     * The observer is notified on open; a dismiss callback notifies on close.
     */
    public void showPopUp(String title, String body, Texture icon) {
        requestPopUp(title, body, icon);
    }

    // ── Render ────────────────────────────────────────────────────────────

    /** Delegates rendering to the underlying DialogPopUp. Call every frame. */
    public void render(SpriteBatch batch) {
        dialog.render(batch);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Internal gate — ensures only one dialog is shown at a time and that
     * the observer chain is always correctly wired before showing.
     */
    private void requestPopUp(String title, String body, Texture icon) {
        if (dialog.isVisible() || eventObserver == null) return;

        dialog.show(title, body, icon);
        eventObserver.onDialogOpened();
        dialog.setCloseCallback(() -> eventObserver.onDialogClosed());
    }
}
