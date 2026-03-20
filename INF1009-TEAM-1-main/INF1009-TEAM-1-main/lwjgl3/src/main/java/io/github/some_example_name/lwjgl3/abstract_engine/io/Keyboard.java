package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;

/**
 * Handles keyboard input.
 * Tracks key states and executes bound actions when keys are pressed.
 */
public class Keyboard implements InputDevice {
    private final IntMap<Object[]> keyBindings;

    /**
     * Creates a new keyboard input handler.
     */
    public Keyboard() {
        keyBindings = new IntMap<>();
    }

    /**
     * Processes keyboard input and executes bound actions.
     * Should be called once per frame.
     */
    @Override
    public void handleInput() {
        for (IntMap.Entry<Object[]> entry : keyBindings.entries()) {
            Object[] binding = entry.value;
            Runnable action = (Runnable) binding[0];
            boolean isJustPressed = (boolean) binding[1];
            boolean isKeyPressed;
            
            if (isJustPressed) {
                isKeyPressed = Gdx.input.isKeyJustPressed(entry.key);
            } 
            else {
                isKeyPressed = Gdx.input.isKeyPressed(entry.key);
            }

            if (isKeyPressed) {
                action.run();  
            }
        }
    }

    /**
     * Binds an action to a keyboard key.
     * 
     * @param key Key code to bind (use LibGDX Keys constants)
     * @param action Action to execute when the key is activated
     * @param isJustPressed If true, the action triggers only on initial press;
     *                     if false, it triggers continuously while held
     */
    @Override
    public void addBind(int key, Runnable action, boolean isJustPressed) {
        keyBindings.put(key, new Object[]{action, isJustPressed});
    }

    /**
     * Removes a binding for the specified key.
     * 
     * @param key Key code to unbind
     */
    @Override
    public void removeBind(int key) {
        keyBindings.remove(key);
    }
}