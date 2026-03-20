package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;

/**
 * Handles mouse input.
 * Tracks mouse position and button states, and executes bound actions when buttons are pressed.
 */
public class Mouse implements InputDevice {
    private final IntMap<Object[]> mouseBindings;
    private float x;
    private float y;

    /**
     * Creates a new mouse input handler.
     */
    public Mouse() {
        mouseBindings = new IntMap<>();
    }

    /**
     * Processes mouse input and executes bound actions.
     * Should be called once per frame.
     */
    @Override
    public void handleInput() {
        for (IntMap.Entry<Object[]> entry : mouseBindings.entries()) {
            Object[] binding = entry.value;
            Runnable action = (Runnable) binding[0];
            boolean isJustPressed = (boolean) binding[1];
            boolean isButtonPressed;
            
            if (isJustPressed) {
                isButtonPressed = Gdx.input.isButtonJustPressed(entry.key);
            } 
            else {
                isButtonPressed = Gdx.input.isButtonPressed(entry.key);
            }

            if (isButtonPressed) {
                action.run();  
            }
        }
    }
    
    /**
     * Gets the current X coordinate of the mouse cursor.
     * 
     * @return The X coordinate in screen coordinates
     */
    public float getX() {
        this.x = Gdx.input.getX();
        return x;
    }
    
    /**
     * Gets the current Y coordinate of the mouse cursor.
     * 
     * @return The Y coordinate in screen coordinates
     */
    public float getY() {
        this.y = Gdx.input.getY();
        return y;
    }

    /**
     * Binds an action to a mouse button.
     * 
     * @param button Button code to bind (use LibGDX Buttons constants)
     * @param action Action to execute when the button is activated
     * @param isJustPressed If true, the action triggers only on initial press;
     *                      if false, it triggers continuously while held
     */
    @Override
    public void addBind(int button, Runnable action, boolean isJustPressed) {
        mouseBindings.put(button, new Object[]{action, isJustPressed});
    }

    /**
     * Removes a binding for the specified button.
     * 
     * @param button Button code to unbind
     */
    @Override
    public void removeBind(int button) {
        mouseBindings.remove(button);
    }
}