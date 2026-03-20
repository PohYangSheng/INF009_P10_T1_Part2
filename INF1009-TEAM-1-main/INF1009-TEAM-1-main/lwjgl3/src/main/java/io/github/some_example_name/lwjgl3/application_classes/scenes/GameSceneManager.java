package io.github.some_example_name.lwjgl3.application_classes.scenes;

import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneTransitionType;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneType;

/**
 * Game-specific implementation of the SceneManager.
 * Manages scene transitions and scene creation for the game.
 */
public class GameSceneManager extends SceneManager {
    // Scene factory for creating new scenes
    private final SceneFactory sceneFactory;

    private int finalPoints;
    private boolean gameWon;
    
    /**
     * Constructs the GameSceneManager.
     * Initializes the scene factory for scene creation.
     */
    public GameSceneManager() {
        super();
        this.sceneFactory = new SceneFactory(this);
    }

    public void setGameResults(int points, boolean won) {
        this.finalPoints = points;
        this.gameWon = won;
    }
    
    public int getFinalPoints() {
        return finalPoints;
    }
    
    public boolean isGameWon() {
        return gameWon;
    }
    
    /**
     * Changes the current scene with the specified transition type.
     * 
     * @param sceneType The type of scene to change to
     * @param transitionType How the transition should be handled
     */
    @Override
    public void changeScene(SceneType sceneType, SceneTransitionType transitionType) {
        Scene newScene = getScene(sceneType);
        handleSceneTransition(newScene, transitionType);
    }
    
    /**
     * Creates a new scene of the specified type using the scene factory.
     * 
     * @param sceneType The type of scene to create
     * @return A new instance of the requested scene
     */
    @Override
    protected Scene getScene(SceneType sceneType) {
        return sceneFactory.createScene(sceneType);
    }
}