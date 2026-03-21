package io.github.some_example_name.lwjgl3.abstract_engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;

import java.util.HashMap;
import java.util.Map;

public class Mouse implements InputDevice {
    private static final int CAPACITY = 15;
    private static final int MAX_BUTTON = 4;

    // After 20 frames mouse button is treated as held
    private static final int HELD_START_FRAMES = 20;

    // Mouse position
    private float x;
    private float y;

    // Buttons clicked this frame
    private final IntArray buttonsPressedThisFrame;

    // Buttons held long enough
    private final IntArray buttonsHeldThisFrame;

    // Count how many frames each button has been held
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
    public Mouse() {
        buttonsPressedThisFrame = new IntArray(false, CAPACITY);
        buttonsHeldThisFrame = new IntArray(false, CAPACITY);
        heldFrames = new int[MAX_BUTTON + 1];
        binds = new HashMap<Integer, Bind>();
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
        buttonsPressedThisFrame.clear();
        buttonsHeldThisFrame.clear();

        setX(Gdx.input.getX());
        setY(Gdx.input.getY());

        for (int button = 0; button <= MAX_BUTTON; button++) {
            if (Gdx.input.isButtonJustPressed(button)) {
                buttonsPressedThisFrame.add(button);
                heldFrames[button] = 0;
            } else if (Gdx.input.isButtonPressed(button)) {
                heldFrames[button]++;
                if (heldFrames[button] >= HELD_START_FRAMES) {
                    buttonsHeldThisFrame.add(button);
                }
            } else {
                heldFrames[button] = 0;
            }
        }

        for (Map.Entry<Integer, Bind> e : binds.entrySet()) {
            int button = e.getKey();
            Bind bind = e.getValue();

            if (bind.justPressed) {
                if (Gdx.input.isButtonJustPressed(button)) {
                    bind.action.run();
                }
            } else {
                if (Gdx.input.isButtonPressed(button)) {
                    bind.action.run();
                }
            }
        }
    }
}
