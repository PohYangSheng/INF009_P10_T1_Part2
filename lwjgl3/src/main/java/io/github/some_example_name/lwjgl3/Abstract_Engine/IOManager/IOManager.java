package io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager;

// handles keyboard and mouse input
public class IOManager {

    private final Keyboard keyboard;
    private final Mouse    mouse;

    // constructor
    public IOManager() {
        this.keyboard = new Keyboard();
        this.mouse    = new Mouse();
    }

    // getter for keyboard
    public Keyboard getKeyboard() {
        return keyboard;
    }
    // getter for mouse
    public Mouse    getMouse() {
        return mouse;
    }

    // poll input devices
    public void handleInput() {
        keyboard.handleInput();
        mouse.handleInput();
    }
}
