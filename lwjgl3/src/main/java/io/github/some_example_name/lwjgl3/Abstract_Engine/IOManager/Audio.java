package io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

// handles background music - play, pause, stop etc
public class Audio {

    private static final float MIN_VOLUME = 0f;
    private static final float MAX_VOLUME = 100f;

    private Music   music;
    private float   volumePercent = 100f;
    private boolean looping       = false;

    // getter for volume percent
    public float   getVolumePercent() {
        return volumePercent;
    }
    // check if looping
    public boolean isLooping() {
        return looping;
    }
    // check if loaded
    public boolean isLoaded() {
        return music != null;
    }
    // check if playing
    public boolean isPlaying() {
        return music != null && music.isPlaying();
    }

    // set volume (0-100)
    public void setVolumePercent(float pct) {
        this.volumePercent = clamp(pct, MIN_VOLUME, MAX_VOLUME);
        if (music != null) music.setVolume(this.volumePercent / 100f);
    }

    // toggle looping
    public void setLooping(boolean looping) {
        this.looping = looping;
        if (music != null) music.setLooping(looping);
    }

    // play a music file
    public void play(String fileName) {
        play(fileName, false);
    }

    // play a music file
    public void play(String fileName, boolean loop) {
        unloadCurrent();
        music = Gdx.audio.newMusic(Gdx.files.internal(fileName));
        setLooping(loop);
        music.setVolume(volumePercent / 100f);
        music.play();
    }

    // pause music
    public void pause() {
        if (music != null && music.isPlaying())  music.pause();
    }
    // resume music
    public void resume() {
        if (music != null && !music.isPlaying()) music.play();
    }
    // stop music
    public void stop() {
        if (music != null) music.stop();
    }

    // clean up textures/resources so we dont leak memory
    public void dispose() {
        unloadCurrent();
    }

    // dispose current track before loading new one
    private void unloadCurrent() {
        if (music != null) {
            music.stop();
            music.dispose();
            music = null;
        }
    }

    // clamp
    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
