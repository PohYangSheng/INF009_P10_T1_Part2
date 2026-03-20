package io.github.some_example_name.lwjgl3.abstract_engine.io;

/**
 * Manages input devices and input handling.
 * Acts as a facade for accessing keyboard and mouse input.
 */
public class IOManager {
    private final Keyboard keyboard;
    private final Mouse mouse;

    /**
     * Creates a new IO manager with default keyboard and mouse devices.
     */
    public IOManager() {
        this.keyboard = new Keyboard();
        this.mouse = new Mouse();
    }
    
    /**
     * Gets the keyboard input device.
     * 
     * @return The keyboard device
     */
    public Keyboard getKeyboard() {
        return keyboard;
    }
    
    /**
     * Gets the mouse input device.
     * 
     * @return The mouse device
     */
    public Mouse getMouse() {
        return mouse;
    }

    /**
     * Processes input for all managed devices.
     * Should be called once per frame.
     */
    public void handleInput() {
        keyboard.handleInput();  
        mouse.handleInput(); 
    }

    /**
     * Binds an action to a key or button on the specified device.
     * 
     * @param device The input device to bind to
     * @param keyOrButton Key or button code to bind
     * @param action Action to execute when the key/button is activated
     * @param isJustPressed If true, the action triggers only on initial press;
     *                     if false, it triggers continuously while held
     */
    public void addIOBind(InputDevice device, int keyOrButton, Runnable action, boolean isJustPressed) {
        device.addBind(keyOrButton, action, isJustPressed);
    }

    /**
     * Removes a binding from the specified device.
     * 
     * @param device The input device to unbind from
     * @param keyOrButton Key or button code to unbind
     */
    public void removeIOBind(InputDevice device, int keyOrButton) {
        device.removeBind(keyOrButton);
    }
}