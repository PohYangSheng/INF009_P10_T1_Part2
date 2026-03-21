package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;

/**
 * Handles keyboard input.
 * - Tracks keys pressed this frame (one-time)
 * - Tracks keys held (continuous after a short delay)
 * - Supports simple key bindings through IOManager
 */
public class Keyboard implements InputDevice, DeviceHandler {

    private static final int CAPACITY = 15;
    private static final int MAX_KEYCODE = 255;
    // After 20 frames a key is treated as held
    private static final int HELD_START_FRAMES = 20;

    // Keys pressed this frame (prints once)
    private final IntArray keysPressedThisFrame;
    // Keys held long enough (prints many)
    private final IntArray keysHeldThisFrame;
    // Frame counters for each key
    private final int[] heldFrames;

    // Key binds (Runnable + isJustPressed)
    private final IntMap<Object[]> keyBindings;

    // Default constructor
    public Keyboard() {
        keysPressedThisFrame = new IntArray(false, CAPACITY);
        keysHeldThisFrame = new IntArray(false, CAPACITY);
        heldFrames = new int[MAX_KEYCODE + 1];
        keyBindings = new IntMap<>();
    }

    // Getters
    public int[] getKeysPressedThisFrame() {
        return keysPressedThisFrame.toArray();
    }

    public int[] getKeysHeldThisFrame() {
        return keysHeldThisFrame.toArray();
    }

    @Override
    public void handleInput() {
        keysPressedThisFrame.clear();
        keysHeldThisFrame.clear();

        for (int key = 0; key <= MAX_KEYCODE; key++) {

            if (Gdx.input.isKeyJustPressed(key)) {
                keysPressedThisFrame.add(key);
                heldFrames[key] = 0;
            }
            else if (Gdx.input.isKeyPressed(key)) {
                heldFrames[key]++;

                if (heldFrames[key] >= HELD_START_FRAMES) {
                    keysHeldThisFrame.add(key);
                }
            }
            else {
                heldFrames[key] = 0;
            }
        }

        // Execute any binds after state is updated
        for (IntMap.Entry<Object[]> entry : keyBindings.entries()) {
            Object[] binding = entry.value;
            Runnable action = (Runnable) binding[0];
            boolean isJustPressed = (boolean) binding[1];

            boolean active;
            if (isJustPressed) {
                active = Gdx.input.isKeyJustPressed(entry.key);
            }
            else {
                active = Gdx.input.isKeyPressed(entry.key);
            }

            if (active) {
                action.run();
            }
        }
    }

    @Override
    public void addBind(int key, Runnable action, boolean isJustPressed) {
        keyBindings.put(key, new Object[]{action, isJustPressed});
    }

    @Override
    public void removeBind(int key) {
        keyBindings.remove(key);
    }
}
