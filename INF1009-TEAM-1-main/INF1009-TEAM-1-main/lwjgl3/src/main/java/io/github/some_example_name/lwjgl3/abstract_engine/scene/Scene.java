package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Base interface for all game scenes.
 * A Scene represents a distinct state or screen in the game,
 * such as a menu, in game, or pause menu.
 */
public interface Scene {
    /**
     * Renders the scene using the provided SpriteBatch.
     * This method is called once per frame.
     * 
     * @param batch The SpriteBatch to use for rendering
     */
    void render(SpriteBatch batch);
    
    /**
     * Cleans up resources used by the scene.
     * This should be called when the scene is no longer needed.
     */
    void dispose();
}