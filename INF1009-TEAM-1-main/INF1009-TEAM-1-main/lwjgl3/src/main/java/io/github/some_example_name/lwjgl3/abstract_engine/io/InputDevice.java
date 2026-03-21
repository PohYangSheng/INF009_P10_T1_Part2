package io.github.some_example_name.lwjgl3.abstract_engine.io;

// Common interface for input devices (Keyboard, Mouse)
public interface InputDevice extends DeviceHandler {

    // Adds an action to run when a key/button is activated
    void addBind(int keyOrButton, Runnable action, boolean isJustPressed);

    // Removes an action bind
    void removeBind(int keyOrButton);
}
