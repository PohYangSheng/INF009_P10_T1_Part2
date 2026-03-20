package io.github.some_example_name.lwjgl3.application_classes.utilities;

/**
 * Interface for listening to dialog popup events.
 * Allows scenes to respond when dialogs are opened and closed.
 */
public interface DialogEventListener {
    /**
     * Called when a dialog is opened.
     */
    void onDialogOpened();
    
    /**
     * Called when a dialog is closed.
     */
    void onDialogClosed();
}