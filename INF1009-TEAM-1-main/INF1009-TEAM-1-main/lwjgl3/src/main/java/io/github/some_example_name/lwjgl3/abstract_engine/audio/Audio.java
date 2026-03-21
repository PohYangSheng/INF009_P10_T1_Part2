package io.github.some_example_name.lwjgl3.abstract_engine.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

// Handle music controls (play, pause, resume, stop, volume, loop)
public class Audio {

    private static final float MIN_VOLUME = 0f;
    private static final float MAX_VOLUME = 100f;

    private Music music;
    private float volumePercent;
    private boolean looping;

    // Default constructor
    public Audio() {
        music = null;
        volumePercent = 100f;
        looping = false;
    }

    // Constructors
    public Audio(float volumePercent, boolean looping) {
        music = null;
        setVolumePercent(volumePercent);
        setLooping(looping);
    }

    // Getters
    public float getVolumePercent() {
        return volumePercent;
    }

    public boolean isLooping() {
        return looping;
    }

    public boolean isLoaded() {
        return music != null;
    }

    public boolean isPlaying() {
        return music != null && music.isPlaying();
    }

    // Setters
    public void setVolumePercent(float volumePercent) {
        this.volumePercent = clamp(volumePercent, MIN_VOLUME, MAX_VOLUME);

        if (music != null) {
            music.setVolume(this.volumePercent / 100f);
        }
    }

    public void setLooping(boolean looping) {
        this.looping = looping;

        if (music != null) {
            music.setLooping(looping);
        }
    }

    // Overloading (no loop)
    public void play(String fileName) {
        play(fileName, false);
    }

    // Overloading (choose loop)
    public void play(String fileName, boolean loop) {
        unloadCurrent();
        music = Gdx.audio.newMusic(Gdx.files.internal(fileName));
        setLooping(loop);
        applySettings();
        music.play();
    }

    public void pause() {
        if (music != null && music.isPlaying()) {
            music.pause();
        }
    }

    public void resume() {
        if (music != null && !music.isPlaying()) {
            music.play();
        }
    }

    public void stop() {
        if (music != null) {
            music.stop();
        }
    }

    public void dispose() {
        unloadCurrent();
    }

    private void applySettings() {
        if (music != null) {
            music.setVolume(volumePercent / 100f);
            music.setLooping(looping);
        }
    }

    private void unloadCurrent() {
        if (music != null) {
            music.stop();
            music.dispose();
            music = null;
        }
    }

    private float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
