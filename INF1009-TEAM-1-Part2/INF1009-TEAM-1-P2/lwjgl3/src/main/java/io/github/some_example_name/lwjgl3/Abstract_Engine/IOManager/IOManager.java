package io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager;

/**
 * Facade over Keyboard and Mouse.
 * Called once per frame by GameMaster / scenes.
 *
 * SOLID: SRP – only responsible for delegating input polling.
 * Design Pattern: Facade.
 */
public class IOManager {

    private final Keyboard keyboard;
    private final Mouse    mouse;

    public IOManager() {
        this.keyboard = new Keyboard();
        this.mouse    = new Mouse();
    }

    public Keyboard getKeyboard() { return keyboard; }
    public Mouse    getMouse()    { return mouse; }

    public void handleInput() {
        keyboard.handleInput();
        mouse.handleInput();
    }
}
