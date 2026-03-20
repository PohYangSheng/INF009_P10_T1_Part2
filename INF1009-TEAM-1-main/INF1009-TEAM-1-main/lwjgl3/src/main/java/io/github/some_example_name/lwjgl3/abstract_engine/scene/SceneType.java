package io.github.some_example_name.lwjgl3.abstract_engine.scene;

/**
 * Enumeration of scene types used by the SceneManager.
 * Each type represents a distinct scene that can be displayed.
 * Game implementations should extend this enum with their own scene types.
 */
public enum SceneType {
    HOME, // Main menu or home screen
    CHARACTER_SELECT, // Character selection screen
    TUTORIAL, // Tutorial or instructions screen
    DIFFICULTY, // Difficulty selection screen
    PLAY, // Main gameplay screen
    PAUSE, // Pause screen during gameplay
    RESULTS // Results or game over screen
}