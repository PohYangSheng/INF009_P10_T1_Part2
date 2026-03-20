package io.github.some_example_name.lwjgl3.abstract_engine.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Abstract base class for scene management.
 * Handles scene transitions and maintains the active scene stack.
 */
public abstract class SceneManager {
    /** The currently active scene */
    protected Scene currentScene;
    
    /** A stored scene for pause/resume functionality */
    protected Scene storedScene;

    /**
     * Initializes a new SceneManager.
     */
    public SceneManager() {
        // Base initialization
    }

    /**
     * Changes the current scene with the specified transition type.
     * 
     * @param sceneType The type of scene to change to
     * @param transitionType How the transition should be handled
     */
    public abstract void changeScene(SceneType sceneType, SceneTransitionType transitionType);

    /**
     * Gets a scene of the specified type.
     * This method should create a new instance of the requested scene.
     * 
     * @param sceneType The type of scene to create
     * @return A new instance of the requested scene
     */
    protected abstract Scene getScene(SceneType sceneType);

    /**
     * Renders the current scene using the provided batch.
     * 
     * @param batch The sprite batch to use for rendering
     */
    public void render(SpriteBatch batch) {
        if (currentScene != null) {
            currentScene.render(batch);
        }
    }

    /**
     * Handles scene transitions based on the specified transition type.
     * 
     * @param newScene The new scene to transition to
     * @param transitionType The type of transition to perform
     */
    protected void handleSceneTransition(Scene newScene, SceneTransitionType transitionType) {
        switch (transitionType) {
            case PAUSE:
                // Store current scene before transitioning to pause screen
                storedScene = currentScene;
                break;

            case RESUME:
                // Restore previously stored scene
                if (storedScene != null) {
                    if (currentScene != null) {
                        currentScene.dispose();
                    }
                    newScene = storedScene;
                    storedScene = null;
                }
                break;

            case RESTART:
                // Dispose both current and stored scenes
                if (storedScene != null) {
                    storedScene.dispose();
                    storedScene = null;
                }
                if (currentScene != null) {
                    currentScene.dispose();
                }
                break;

            case NORMAL:
            default:
                // Standard transition, just dispose current scene
                if (currentScene != null) {
                    currentScene.dispose();
                }
                break;
        }

        currentScene = newScene;
    }
    
    /**
     * Gets the currently active scene.
     * 
     * @return The current scene, or null if no scene is active
     */
    public Scene getCurrentScene() {
        return currentScene;
    }
    
    /**
     * Cleans up resources when the manager is no longer needed.
     * Disposes both the current and stored scenes.
     */
    public void dispose() {
        if (currentScene != null) {
            currentScene.dispose();
            currentScene = null;
        }
        
        if (storedScene != null) {
            storedScene.dispose();
            storedScene = null;
        }
    }
}