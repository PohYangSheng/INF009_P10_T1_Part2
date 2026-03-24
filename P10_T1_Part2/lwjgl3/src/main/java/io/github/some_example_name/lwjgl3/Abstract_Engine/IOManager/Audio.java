package io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * Low-level music controller: play, pause, resume, stop, volume, loop.
 * Used by AudioSimulation in the game layer.
 *
 * SOLID: SRP – handles one music track at a time.
 */
public class Audio {

    private static final float MIN_VOLUME = 0f;
    private static final float MAX_VOLUME = 100f;

    private Music   music;
    private float   volumePercent = 100f;
    private boolean looping       = false;

    // ── Getters ────────────────────────────────────────────────────────────

    public float   getVolumePercent() { return volumePercent; }
    public boolean isLooping()        { return looping; }
    public boolean isLoaded()         { return music != null; }
    public boolean isPlaying()        { return music != null && music.isPlaying(); }

    // ── Setters ────────────────────────────────────────────────────────────

    public void setVolumePercent(float pct) {
        this.volumePercent = clamp(pct, MIN_VOLUME, MAX_VOLUME);
        if (music != null) music.setVolume(this.volumePercent / 100f);
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
        if (music != null) music.setLooping(looping);
    }

    // ── Playback ───────────────────────────────────────────────────────────

    public void play(String fileName)                  { play(fileName, false); }

    public void play(String fileName, boolean loop) {
        unloadCurrent();
        music = Gdx.audio.newMusic(Gdx.files.internal(fileName));
        setLooping(loop);
        music.setVolume(volumePercent / 100f);
        music.play();
    }

    public void pause()  { if (music != null && music.isPlaying())  music.pause(); }
    public void resume() { if (music != null && !music.isPlaying()) music.play();  }
    public void stop()   { if (music != null) music.stop(); }

    public void dispose() { unloadCurrent(); }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void unloadCurrent() {
        if (music != null) { music.stop(); music.dispose(); music = null; }
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
