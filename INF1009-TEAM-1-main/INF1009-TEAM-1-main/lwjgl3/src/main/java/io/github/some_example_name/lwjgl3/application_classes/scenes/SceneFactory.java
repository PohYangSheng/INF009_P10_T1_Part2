package io.github.some_example_name.lwjgl3.application_classes.scenes;

import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneType;

/**
 * Factory class for creating game-specific scenes
 */
public class SceneFactory {
    private final GameSceneManager sceneManager;
    
    /**
     * Constructor
     * 
     * @param sceneManager The scene manager to use for created scenes
     */
    public SceneFactory(GameSceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    /**
     * Creates a scene of the specified type
     * 
     * @param sceneType The type of scene to create
     * @return A new instance of the requested scene
     */
    public Scene createScene(SceneType sceneType) {
        switch (sceneType) {
            case HOME:
                return new HomeScene(sceneManager);
            case CHARACTER_SELECT:
                return new CharacterSelectScene(sceneManager);
            case TUTORIAL:
                return new TutorialScene(sceneManager);
            case DIFFICULTY:
                return new DifficultyScene(sceneManager);
            case PLAY:
                return new PlayScene(sceneManager);
            case PAUSE:
                return new PauseScene(sceneManager);
            case RESULTS:
                return new ResultsScene(sceneManager);
            default:
                throw new IllegalArgumentException("Unknown SceneType: " + sceneType);
        }
    }
}