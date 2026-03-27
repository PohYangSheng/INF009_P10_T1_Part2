package io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import java.util.Map;

// plays short sound effects and caches them
public class SoundManager {

    private final Map<String, Sound> cache = new HashMap<>();
    private       float              volume = 1.0f;

    // set sfx volume
    public void setVolume(float volume) {
        this.volume = volume;
    }
    // getter for volume
    public float getVolume() {
        return volume;
    }

    // play a sound effect
    public void play(String path) {
        Sound sound = cache.computeIfAbsent(path,
                p -> Gdx.audio.newSound(Gdx.files.internal(p)));
        sound.play(volume);
    }

    // clean up textures/resources so we dont leak memory
    public void dispose() {
        cache.values().forEach(Sound::dispose);
        cache.clear();
    }
}
