package io.github.some_example_name.lwjgl3.abstract_engine.scene;

/**
 * Defines the type of transition between scenes.
 * Controls how the scene manager handles scene changes.
 */
public enum SceneTransitionType {
    NORMAL,  // Normal scene transition (dispose previous scene)
    PAUSE,    // Store PlayScene and switch to PauseScene
    RESUME,   // Restore stored PlayScene when leaving PauseScene
    RESTART  // Dispose PauseScene + Stored PlayScene, create new PlayScene
}
