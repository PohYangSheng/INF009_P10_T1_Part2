package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;

/**
 * Handles mouse input.
 * - Tracks buttons pressed this frame (one-time)
 * - Tracks buttons held (continuous after a short delay)
 * - Tracks mouse position
 * - Supports simple button bindings through IOManager
 */
public class Mouse implements InputDevice, DeviceHandler {

    private static final int CAPACITY = 15;
    private static final int MAX_BUTTON = 4;
    // After 20 frames a button is treated as held
    private static final int HELD_START_FRAMES = 20;

    private float x;
    private float y;

    private final IntArray buttonsPressedThisFrame;
    private final IntArray buttonsHeldThisFrame;
    private final int[] heldFrames;

    // Button binds (Runnable + isJustPressed)
    private final IntMap<Object[]> buttonBindings;

    // Default constructor
    public Mouse() {
        buttonsPressedThisFrame = new IntArray(false, CAPACITY);
        buttonsHeldThisFrame = new IntArray(false, CAPACITY);
        heldFrames = new int[MAX_BUTTON + 1];
        buttonBindings = new IntMap<>();
    }

    // Getters
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int[] getButtonsPressedThisFrame() {
        return buttonsPressedThisFrame.toArray();
    }

    public int[] getButtonsHeldThisFrame() {
        return buttonsHeldThisFrame.toArray();
    }

    // Setters
    private void setX(float x) {
        this.x = x;
    }

    private void setY(float y) {
        this.y = y;
    }

    @Override
    public void handleInput() {
        buttonsPressedThisFrame.clear();
        buttonsHeldThisFrame.clear();

        setX(Gdx.input.getX());
        setY(Gdx.input.getY());

        for (int button = 0; button <= MAX_BUTTON; button++) {

            if (Gdx.input.isButtonJustPressed(button)) {
                buttonsPressedThisFrame.add(button);
                heldFrames[button] = 0;
            }
            else if (Gdx.input.isButtonPressed(button)) {
                heldFrames[button]++;

                if (heldFrames[button] >= HELD_START_FRAMES) {
                    buttonsHeldThisFrame.add(button);
                }
            }
            else {
                heldFrames[button] = 0;
            }
        }

        // Execute any binds after state is updated
        for (IntMap.Entry<Object[]> entry : buttonBindings.entries()) {
            Object[] binding = entry.value;
            Runnable action = (Runnable) binding[0];
            boolean isJustPressed = (boolean) binding[1];

            boolean active;
            if (isJustPressed) {
                active = Gdx.input.isButtonJustPressed(entry.key);
            }
            else {
                active = Gdx.input.isButtonPressed(entry.key);
            }

            if (active) {
                action.run();
            }
        }
    }

    @Override
    public void addBind(int button, Runnable action, boolean isJustPressed) {
        buttonBindings.put(button, new Object[]{action, isJustPressed});
    }

    @Override
    public void removeBind(int button) {
        buttonBindings.remove(button);
    }
}
