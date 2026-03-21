package io.github.some_example_name.lwjgl3.abstract_engine.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

// AudioManager handles background music and simple sound effects.
// This class is a Singleton so any scene can access the same audio controller.
public class AudioManager implements Disposable {

    private static AudioManager instance = null;

    // Volume is stored as 0.0 to 1.0 (matches libGDX Music volume).
    private float masterVolume;

    // Background music uses the simple Audio engine class.
    private final Audio bgm;
    private String currentTrack;

    // Cache SFX so we don't reload files every time.
    private final ObjectMap<String, Music> cachedSFX;
    private final Array<Music> activeSFX;

    // Default constructor (private for singleton)
    private AudioManager() {
        masterVolume = 1.0f;
        bgm = new Audio(100f, true);
        currentTrack = null;

        cachedSFX = new ObjectMap<String, Music>();
        activeSFX = new Array<Music>();
    }

    public static synchronized AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    // Plays looping background music for a scene.
    public void setSceneBGM(String fileName) {
        if (fileName != null && fileName.equals(currentTrack)) {
            return;
        }

        stopBGM();

        bgm.setLooping(true);
        bgm.setVolumePercent(masterVolume * 100f);
        bgm.play(fileName, true);
        currentTrack = fileName;
    }

    // Plays a one-shot sound effect.
    public void playSFX(String fileName) {
        if (fileName == null) {
            return;
        }

        Music sfx;

        if (cachedSFX.containsKey(fileName)) {
            sfx = cachedSFX.get(fileName);
            sfx.setPosition(0);
        } else {
            sfx = Gdx.audio.newMusic(Gdx.files.internal(fileName));
            cachedSFX.put(fileName, sfx);
        }

        sfx.setLooping(false);
        sfx.setVolume(masterVolume);
        sfx.play();

        activeSFX.add(sfx);
        sfx.setOnCompletionListener(music -> activeSFX.removeValue(music, true));
    }

    public void stopBGM() {
        bgm.stop();
        bgm.dispose();
        currentTrack = null;
    }

    public void stopAllAudio() {
        stopBGM();

        for (Music sfx : activeSFX) {
            sfx.stop();
        }
        activeSFX.clear();
    }

    public float getVolume() {
        return masterVolume;
    }

    public void setVolume(float volume) {
        masterVolume = Math.max(0.0f, Math.min(1.0f, volume));

        // Update BGM volume if loaded
        bgm.setVolumePercent(masterVolume * 100f);

        // Update volume for active SFX
        for (Music sfx : activeSFX) {
            sfx.setVolume(masterVolume);
        }
    }

    public void pauseBGM() {
        bgm.pause();
    }

    public void resumeBGM() {
        bgm.resume();
    }

    @Override
    public void dispose() {
        stopAllAudio();

        for (Music sfx : cachedSFX.values()) {
            sfx.dispose();
        }
        cachedSFX.clear();
        activeSFX.clear();

        instance = null;
    }
}
