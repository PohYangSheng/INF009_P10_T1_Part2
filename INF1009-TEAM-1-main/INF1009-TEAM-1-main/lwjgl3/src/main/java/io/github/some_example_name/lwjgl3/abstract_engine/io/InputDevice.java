package io.github.some_example_name.lwjgl3.abstract_engine.io;

/**
 * Interface for input devices such as Mouse, Keyboard, etc.
 * Provides a common API for handling user input from different sources.
 */
public interface InputDevice {
    /**
     * Processes input for this device.
     * Should be called once per frame.
     */
    void handleInput();
    
    /**
     * Binds an action to a key or button.
     * 
     * @param keyOrButton Key or button code to bind
     * @param action Action to execute when the key/button is activated
     * @param isJustPressed If true, the action triggers only on initial press;
     *                      if false, it triggers continuously while held
     */
    void addBind(int keyOrButton, Runnable action, boolean isJustPressed);
    
    /**
     * Removes a binding for the specified key or button.
     * 
     * @param keyOrButton Key or button code to unbind
     */
    void removeBind(int keyOrButton);
}