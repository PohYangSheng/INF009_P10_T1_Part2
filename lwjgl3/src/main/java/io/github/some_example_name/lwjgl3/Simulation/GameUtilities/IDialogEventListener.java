package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

/**
 * Observer interface for dialog popup events.
 *
 * Design Pattern: Observer – PlayScene implements this to pause/resume
 * game logic when a dialog opens or closes, without tight coupling
 * between DialogPopUpManager (Subject) and PlayScene (Observer).
 */
public interface IDialogEventListener {
    void onDialogOpened();
    void onDialogClosed();
}
