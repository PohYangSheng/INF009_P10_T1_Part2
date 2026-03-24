package io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;

/**
 * Tracks mouse button state and cursor position each frame.
 */
public class Mouse implements DeviceHandler {

    private static final int CAPACITY          = 15;
    private static final int MAX_BUTTON        = 4;
    private static final int HELD_START_FRAMES = 20;

    private float x, y;
    private final IntArray buttonsPressedThisFrame = new IntArray(false, CAPACITY);
    private final IntArray buttonsHeldThisFrame    = new IntArray(false, CAPACITY);
    private final int[]    heldFrames              = new int[MAX_BUTTON + 1];

    @Override
    public void handleInput() {
        buttonsPressedThisFrame.clear();
        buttonsHeldThisFrame.clear();
        x = Gdx.input.getX();
        y = Gdx.input.getY();

        for (int btn = 0; btn <= MAX_BUTTON; btn++) {
            if (Gdx.input.isButtonJustPressed(btn)) {
                buttonsPressedThisFrame.add(btn);
                heldFrames[btn] = 0;
            } else if (Gdx.input.isButtonPressed(btn)) {
                heldFrames[btn]++;
                if (heldFrames[btn] >= HELD_START_FRAMES) buttonsHeldThisFrame.add(btn);
            } else {
                heldFrames[btn] = 0;
            }
        }
    }

    public float getX()                           { return x; }
    public float getY()                           { return y; }
    public int[] getButtonsPressedThisFrame()     { return buttonsPressedThisFrame.toArray(); }
    public int[] getButtonsHeldThisFrame()        { return buttonsHeldThisFrame.toArray(); }
}
