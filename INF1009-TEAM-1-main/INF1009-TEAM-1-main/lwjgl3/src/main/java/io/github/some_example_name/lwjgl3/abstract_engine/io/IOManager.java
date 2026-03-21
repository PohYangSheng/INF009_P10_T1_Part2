package io.github.some_example_name.lwjgl3.abstract_engine.io;

public class IOManager {
    private final Keyboard keyboard;
    private final Mouse mouse;

    // Default Constructor
    public IOManager() {
        this.keyboard = new Keyboard();
        this.mouse = new Mouse();
    }

    // Getter for Keyboard
    public Keyboard getKeyboard() {
        return keyboard;
    }

    // Getter for Mouse
    public Mouse getMouse() {
        return mouse;
    }

    // For gamemaster to call a method to update each frame
    public void handleInput() {
        keyboard.handleInput();
        mouse.handleInput();
    }

    // Optional bind helpers (used by game scenes)
    public void addIOBind(InputDevice device, int keyOrButton, Runnable action, boolean isJustPressed) {
        if (device == null) {
            return;
        }
        device.addBind(keyOrButton, action, isJustPressed);
    }

    public void removeIOBind(InputDevice device, int keyOrButton) {
        if (device == null) {
            return;
        }
        device.removeBind(keyOrButton);
    }
}
