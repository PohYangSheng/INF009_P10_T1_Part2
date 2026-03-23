package io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager;

/**
 * Interface for any input device (keyboard, mouse, gamepad…).
 * Follows ISP – minimal contract for per-frame input polling.
 */
public interface DeviceHandler {
    void handleInput();
}
