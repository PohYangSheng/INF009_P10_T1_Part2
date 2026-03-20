package io.github.some_example_name.lwjgl3.abstract_engine.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Manages audio playback for the game including background music and sound effects.
 * Implements the singleton pattern to ensure only one audio manager exists.
 * Also implements Disposable to properly clean up resources when the game exits.
 */
public class AudioPlayer implements Disposable {
    // Singleton instance
    private static AudioPlayer instance = null;
    
    // Audio properties
    private float masterVolume;
    private Music bgm;
    private String currentTrack;
    private final ObjectMap<String, Music> cachedSFX;
    private final Array<Music> activeSFX;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the audio system with default settings.
     */
    private AudioPlayer() {
        masterVolume = 1.0f; // Default volume (0.0 to 1.0 scale)
        bgm = null;
        currentTrack = null;
        cachedSFX = new ObjectMap<>();
        activeSFX = new Array<>();
    }

    /**
     * Gets the singleton instance, creating it if necessary.
     * 
     * @return The AudioPlayer singleton instance
     */
    public static synchronized AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    /**
     * Sets and plays background music, replacing any currently playing track.
     * Only loads a new file if the requested track is different from the current one.
     * 
     * @param fileName Path to the music file relative to the assets folder
     */
    public void setSceneBGM(String fileName) {
        // Skip if trying to play the same track that's already playing
        if (fileName.equals(currentTrack)) {
            return;
        }
        
        // Stop current BGM if there is one
        stopBGM();
        
        // Load and play new BGM
        bgm = Gdx.audio.newMusic(Gdx.files.internal(fileName));
        bgm.setLooping(true);
        bgm.setVolume(masterVolume);
        bgm.play();
        currentTrack = fileName;
    }

    /**
     * Plays a one-shot sound effect.
     * Sound effects are automatically disposed after they finish playing.
     * 
     * @param fileName Path to the sound file relative to the assets folder
     */
    public void playSFX(String fileName) {
        // Load or retrieve the SFX from cache
        Music sfx;
        
        if (cachedSFX.containsKey(fileName)) {
            sfx = cachedSFX.get(fileName);
            // Reset to beginning if it was already played
            sfx.setPosition(0);
        } else {
            sfx = Gdx.audio.newMusic(Gdx.files.internal(fileName));
            cachedSFX.put(fileName, sfx);
        }
        
        // Configure and play the SFX
        sfx.setLooping(false);
        sfx.setVolume(masterVolume);
        sfx.play();

        // Add to active SFX list
        activeSFX.add(sfx);

        // Set up callback to handle completion
        sfx.setOnCompletionListener(music -> {
            activeSFX.removeValue(music, true);
        });
    }

    /**
     * Stops the current background music and releases its resources.
     */
    public void stopBGM() {
        if (bgm != null) {
            bgm.stop();
            bgm.dispose();
            bgm = null;
            currentTrack = null;
        }
    }

    /**
     * Stops all audio playback including BGM and SFX, and releases resources.
     */
    public void stopAllAudio() {
        // Stop BGM
        stopBGM();
        
        // Stop all active SFX
        for (Music sfx : activeSFX) {
            sfx.stop();
        }
        activeSFX.clear();
    }

    /**
     * Gets the current master volume level.
     * 
     * @return Volume level as a value between 0.0 and 1.0
     */
    public float getVolume() {
        return masterVolume;
    }

    /**
     * Sets the master volume level for all audio.
     * 
     * @param volume Volume level between 0.0 (silent) and 1.0 (maximum)
     */
    public void setVolume(float volume) {
        // Clamp volume to valid range
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        
        // Update volume for active BGM
        if (bgm != null) {
            bgm.setVolume(masterVolume);
        }
        
        // Update volume for all active SFX
        for (Music sfx : activeSFX) {
            sfx.setVolume(masterVolume);
        }
    }

    /**
     * Pauses the current background music if it's playing.
     */
    public void pauseBGM() {
        if (bgm != null && bgm.isPlaying()) {
            bgm.pause();
        }
    }

    /**
     * Resumes the current background music if it's paused.
     */
    public void resumeBGM() {
        if (bgm != null && !bgm.isPlaying()) {
            bgm.play();
        }
    }

    /**
     * Disposes all audio resources managed by this AudioPlayer.
     * Should be called when the game is closing to prevent memory leaks.
     */
    @Override
    public void dispose() {
        // Dispose background music
        if (bgm != null) {
            bgm.dispose();
            bgm = null;
        }
        
        // Dispose all cached SFX
        for (Music sfx : cachedSFX.values()) {
            sfx.dispose();
        }
        cachedSFX.clear();
        activeSFX.clear();
        
        // Clear the singleton instance
        instance = null;
    }
}