package io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;

/**
 * Tracks keyboard state each frame: keys just-pressed and keys held.
 * SOLID: SRP – only handles keyboard polling.
 * Design Pattern: Strategy (implements DeviceHandler).
 * FIX: HELD_START_FRAMES=1 for smooth continuous movement.
 */
public class Keyboard implements DeviceHandler {

    private static final int CAPACITY          = 15;
    private static final int MAX_KEYCODE       = 255;
    private static final int HELD_START_FRAMES = 1;

    private final IntArray keysPressedThisFrame = new IntArray(false, CAPACITY);
    private final IntArray keysHeldThisFrame    = new IntArray(false, CAPACITY);
    private final int[]    heldFrames           = new int[MAX_KEYCODE + 1];

    @Override
    public void handleInput() {
        keysPressedThisFrame.clear();
        keysHeldThisFrame.clear();
        for (int key = 0; key <= MAX_KEYCODE; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                keysPressedThisFrame.add(key);
                heldFrames[key] = 0;
            } else if (Gdx.input.isKeyPressed(key)) {
                heldFrames[key]++;
                if (heldFrames[key] >= HELD_START_FRAMES) keysHeldThisFrame.add(key);
            } else {
                heldFrames[key] = 0;
            }
        }
    }

    public int[] getKeysPressedThisFrame() { return keysPressedThisFrame.toArray(); }
    public int[] getKeysHeldThisFrame()    { return keysHeldThisFrame.toArray(); }
}
