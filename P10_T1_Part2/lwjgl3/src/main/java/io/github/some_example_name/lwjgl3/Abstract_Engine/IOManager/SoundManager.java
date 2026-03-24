package io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a cache of short sound effects loaded from internal files.
 *
 * Separates SFX lifecycle from collision logic, fixing the SRP violation
 * where collision handlers held static Sound fields and leaked resources
 * across game sessions.
 *
 * OOP:
 *   Encapsulation – sound cache hidden; accessed only through play/dispose.
 *
 * SOLID:
 *   SRP  – one responsibility: load, cache, play and dispose Sound instances.
 *   OCP  – new sounds added by calling play(path) with a new path; no code
 *          change needed.
 *   DIP  – consumers depend on SoundManager, not on Gdx.audio directly.
 *
 * No game-specific types referenced – pure engine code.
 */
public class SoundManager {

    private final Map<String, Sound> cache = new HashMap<>();
    private       float              volume = 1.0f;

    public void setVolume(float volume) { this.volume = volume; }
    public float getVolume()            { return volume; }

    /**
     * Play a sound effect. Loads and caches on first call.
     *
     * @param path internal asset path (e.g. "SFX/crunch.mp3")
     */
    public void play(String path) {
        Sound sound = cache.computeIfAbsent(path,
                p -> Gdx.audio.newSound(Gdx.files.internal(p)));
        sound.play(volume);
    }

    /** Dispose all loaded sounds. Call when the game session ends. */
    public void dispose() {
        cache.values().forEach(Sound::dispose);
        cache.clear();
    }
}
