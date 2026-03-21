package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;

import java.util.HashMap;
import java.util.Map;

public class Keyboard implements InputDevice {
    private static final int CAPACITY = 15;
    private static final int MAX_KEYCODE = 255;

    // After 20 frames keyboard button is treated as held
    private static final int HELD_START_FRAMES = 20;

    // Keys that were pressed this frame
    private final IntArray keysPressedThisFrame;

    // Keys that have been held long enough
    private final IntArray keysHeldThisFrame;

    // Count how many frames each key has been held
    private final int[] heldFrames;

    private static class Bind {
        private final Runnable action;
        private final boolean justPressed;

        public Bind(Runnable action, boolean justPressed) {
            this.action = action;
            this.justPressed = justPressed;
        }
    }

    private final Map<Integer, Bind> binds;

    // Default constructor
    public Keyboard() {
        keysPressedThisFrame = new IntArray(false, CAPACITY);
        keysHeldThisFrame = new IntArray(false, CAPACITY);
        heldFrames = new int[MAX_KEYCODE + 1];
        binds = new HashMap<Integer, Bind>();
    }

    // Getters
    public int[] getKeysPressedThisFrame() {
        return keysPressedThisFrame.toArray();
    }

    public int[] getKeysHeldThisFrame() {
        return keysHeldThisFrame.toArray();
    }

    @Override
    public void addBind(int keyOrButton, Runnable action, boolean isJustPressed) {
        if (action == null) {
            return;
        }
        binds.put(keyOrButton, new Bind(action, isJustPressed));
    }

    @Override
    public void removeBind(int keyOrButton) {
        binds.remove(keyOrButton);
    }

    @Override
    public void handleInput() {
        keysPressedThisFrame.clear();
        keysHeldThisFrame.clear();

        // Check every keycode and decide if pressed or held
        for (int key = 0; key <= MAX_KEYCODE; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                keysPressedThisFrame.add(key);
                heldFrames[key] = 0;
            } else if (Gdx.input.isKeyPressed(key)) {
                heldFrames[key]++;
                if (heldFrames[key] >= HELD_START_FRAMES) {
                    keysHeldThisFrame.add(key);
                }
            } else {
                heldFrames[key] = 0;
            }
        }

        // Run binds
        for (Map.Entry<Integer, Bind> e : binds.entrySet()) {
            int key = e.getKey();
            Bind bind = e.getValue();

            if (bind.justPressed) {
                if (Gdx.input.isKeyJustPressed(key)) {
                    bind.action.run();
                }
            } else {
                if (Gdx.input.isKeyPressed(key)) {
                    bind.action.run();
                }
            }
        }
    }
}
