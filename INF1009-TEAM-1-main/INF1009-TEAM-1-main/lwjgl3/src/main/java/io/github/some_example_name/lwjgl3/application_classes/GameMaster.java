package io.github.some_example_name.lwjgl3.application_classes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneTransitionType;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneType;
import io.github.some_example_name.lwjgl3.application_classes.scenes.GameSceneManager;

/**
 * The core game class that serves as the main entry point and controller for the game.
 * 
 * This class extends ApplicationAdapter from libGDX and manages the high-level
 * game loop including scene management and rendering. It coordinates between the
 * rendering system and the game scene management.
 */
public class GameMaster extends ApplicationAdapter {
    
    /** The scene manager responsible for handling all game scenes and transitions */
    private GameSceneManager sceneManager;
    
    /** The sprite batch used for rendering all game graphics */
    private SpriteBatch batch;

    /**
     * Initializes the game when it first starts.
     * Creates essential game components and sets up the initial scene.
     */
    @Override
    public void create() {
        // Initialize the scene manager which will control all game screens
        sceneManager = new GameSceneManager();
        
        // Set the initial scene to the home screen with a normal transition
        sceneManager.changeScene(SceneType.HOME, SceneTransitionType.NORMAL);
        
        // Create the sprite batch that will be used for all rendering operations
        batch = new SpriteBatch();        
    }

    /**
     * Called every frame to render the game.
     * This is the main game loop that executes continuously while the game is running.
     */
    @Override
    public void render() {
        // Begin the sprite batch to start the rendering process
        batch.begin();
        
        // Delegate rendering to the current active scene
        sceneManager.render(batch);
        
        // End the sprite batch to complete the rendering process
        batch.end();
    }

    /**
     * Cleans up resources when the game is closing.
     * This method ensures proper disposal of all resources to prevent memory leaks.
     */
    @Override
    public void dispose() {
        // Dispose the scene manager and all active scenes
        if (sceneManager != null) {
            sceneManager.dispose();
        }
        
        // Dispose the sprite batch to release GPU resources
        if (batch != null) {
            batch.dispose();
        }
    }
}