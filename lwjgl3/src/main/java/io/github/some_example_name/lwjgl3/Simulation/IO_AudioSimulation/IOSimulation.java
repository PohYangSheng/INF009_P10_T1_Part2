package io.github.some_example_name.lwjgl3.Simulation.IO_AudioSimulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.IOManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles global input actions (print keys, exit on ESC).
 * Kept from Part 1 – uses Command pattern for input actions.
 *
 * Design Pattern: Command – each InputAction encapsulates one input response.
 */
public class IOSimulation {

    private final IOManager          ioManager;
    private final List<InputAction>  actions = new ArrayList<>();

    public IOSimulation() {
        this.ioManager = new IOManager();
        actions.add(new PrintKeyboardAction());
        actions.add(new PrintMouseAction());
        actions.add(new ExitOnEscAction());
    }

    public void update() {
        ioManager.handleInput();
        for (InputAction action : actions) action.execute(ioManager);
    }

    public IOManager getIOManager() { return ioManager; }

    // ── Command interface ──────────────────────────────────────────────────

    private static abstract class InputAction {
        public abstract void execute(IOManager ioManager);
    }

    private static class PrintKeyboardAction extends InputAction {
        @Override public void execute(IOManager io) {
            for (int k : io.getKeyboard().getKeysPressedThisFrame())
                System.out.println("[KEY PRESS] " + Input.Keys.toString(k));
            for (int k : io.getKeyboard().getKeysHeldThisFrame())
                System.out.println("[KEY HELD] "  + Input.Keys.toString(k));
        }
    }

    private static class PrintMouseAction extends InputAction {
        @Override public void execute(IOManager io) {
            float x = io.getMouse().getX(), y = io.getMouse().getY();
            for (int b : io.getMouse().getButtonsPressedThisFrame())
                System.out.println("[MOUSE PRESS] btn=" + b + " at (" + x + "," + y + ")");
            for (int b : io.getMouse().getButtonsHeldThisFrame())
                System.out.println("[MOUSE HELD] btn="  + b + " at (" + x + "," + y + ")");
        }
    }

    private static class ExitOnEscAction extends InputAction {
        @Override public void execute(IOManager io) {
            for (int k : io.getKeyboard().getKeysPressedThisFrame())
                if (k == Input.Keys.ESCAPE) { System.out.println("Exiting…"); Gdx.app.exit(); }
        }
    }
}
