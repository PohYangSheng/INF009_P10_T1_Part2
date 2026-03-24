package io.github.some_example_name.lwjgl3.Simulation.IO_AudioSimulation;

import com.badlogic.gdx.Input;
import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.Audio;
import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.IOManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps function-key presses to audio commands.
 * Kept from Part 1 with Command pattern preserved.
 *
 * Key mapping: F1=Play, F2=Pause, F3=Resume, F4=Stop, F5=ToggleLoop,
 *              UP=Volume+10, DOWN=Volume-10.
 *
 * Design Pattern: Command – each AudioAction encapsulates one audio operation.
 */
public class AudioSimulation {

    private final IOManager         ioManager;
    private final Audio             audio;
    private final List<AudioAction> actions = new ArrayList<>();
    private final String            songFileName;

    public AudioSimulation() { this("Ruins_Soundtrack.mp3"); }

    public AudioSimulation(String songFileName) {
        this.ioManager    = new IOManager();
        this.audio        = new Audio();
        this.songFileName = songFileName;

        actions.add(new PlayAction());
        actions.add(new PauseAction());
        actions.add(new ResumeAction());
        actions.add(new StopAction());
        actions.add(new ToggleLoopAction());
        actions.add(new VolumeUpAction());
        actions.add(new VolumeDownAction());
    }

    public Audio     getAudio()     { return audio; }
    public IOManager getIOManager() { return ioManager; }

    public void update() {
        ioManager.handleInput();
        for (AudioAction a : actions) a.execute(ioManager);
    }

    public void dispose() { audio.dispose(); }

    // ── Command interface ──────────────────────────────────────────────────

    private static abstract class AudioAction {
        public abstract void execute(IOManager io);
    }

    private class PlayAction extends AudioAction {
        @Override public void execute(IOManager io) {
            for (int k : io.getKeyboard().getKeysPressedThisFrame())
                if (k == Input.Keys.F1) { audio.play(songFileName, audio.isLooping()); System.out.println("Play"); }
        }
    }
    private class PauseAction extends AudioAction {
        @Override public void execute(IOManager io) {
            for (int k : io.getKeyboard().getKeysPressedThisFrame())
                if (k == Input.Keys.F2) { audio.pause(); System.out.println("Pause"); }
        }
    }
    private class ResumeAction extends AudioAction {
        @Override public void execute(IOManager io) {
            for (int k : io.getKeyboard().getKeysPressedThisFrame())
                if (k == Input.Keys.F3) { audio.resume(); System.out.println("Resume"); }
        }
    }
    private class StopAction extends AudioAction {
        @Override public void execute(IOManager io) {
            for (int k : io.getKeyboard().getKeysPressedThisFrame())
                if (k == Input.Keys.F4) { audio.stop(); System.out.println("Stop"); }
        }
    }
    private class ToggleLoopAction extends AudioAction {
        @Override public void execute(IOManager io) {
            for (int k : io.getKeyboard().getKeysPressedThisFrame())
                if (k == Input.Keys.F5) { audio.setLooping(!audio.isLooping()); System.out.println("Loop: " + audio.isLooping()); }
        }
    }
    private class VolumeUpAction extends AudioAction {
        @Override public void execute(IOManager io) {
            for (int k : io.getKeyboard().getKeysPressedThisFrame())
                if (k == Input.Keys.UP) { audio.setVolumePercent(audio.getVolumePercent() + 10f); System.out.println("Volume: " + audio.getVolumePercent()); }
        }
    }
    private class VolumeDownAction extends AudioAction {
        @Override public void execute(IOManager io) {
            for (int k : io.getKeyboard().getKeysPressedThisFrame())
                if (k == Input.Keys.DOWN) { audio.setVolumePercent(audio.getVolumePercent() - 10f); System.out.println("Volume: " + audio.getVolumePercent()); }
        }
    }
}
