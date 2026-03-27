package io.github.some_example_name.lwjgl3.Simulation.SceneSimulation;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.Audio;
import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.SoundManager;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.CharacterSelectionManager;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.DifficultyConfig;

// factory that creates all the different scenes and shares state between them
public class SceneFactory {

    public enum SceneType {
        MENU, AVATAR_SELECTION, DIFFICULTY, TUTORIAL, PLAY, PAUSE, SCORE
    }

    private final DifficultyConfig          gameDifficulty;
    private final CharacterSelectionManager charSelection;

    private final Audio        audio        = new Audio();

    private final SoundManager soundManager = new SoundManager();

    private int     finalPoints = 0;
    private boolean gameWon     = false;

    private final Map<SceneType, Supplier<AbstractScene>> registry =
        new EnumMap<>(SceneType.class);

    // constructor - takes in shared stuff from the factory
    public SceneFactory() {
        this.gameDifficulty = new DifficultyConfig();
        this.charSelection  = new CharacterSelectionManager();

        registry.put(SceneType.MENU,             () -> new MenuScene(this));
        registry.put(SceneType.AVATAR_SELECTION, () -> new AvatarSelectionScene(this));
        registry.put(SceneType.DIFFICULTY,       () -> new DifficultyScene(this));
        registry.put(SceneType.TUTORIAL,         () -> new TutorialScene(this));
        registry.put(SceneType.PLAY,             () -> new PlayScene(this));
        registry.put(SceneType.PAUSE,            () -> new PauseScene(this));
        registry.put(SceneType.SCORE,            () -> new ScoreScene(this));
    }

    // create a new scene of the given type
    public AbstractScene create(SceneType type) {
        Supplier<AbstractScene> supplier = registry.get(type);
        if (supplier == null)
            throw new IllegalArgumentException("No scene registered for: " + type);
        return supplier.get();
    }

    // getter for game difficulty
    public DifficultyConfig          getGameDifficulty() {
        return gameDifficulty;
    }
    // getter for char selection
    public CharacterSelectionManager getCharSelection() {
        return charSelection;
    }
    // getter for audio
    public Audio                     getAudio() {
        return audio;
    }
    // getter for sound manager
    public SoundManager              getSoundManager() {
        return soundManager;
    }

    // save the final score for the score screen
    public void    setGameResults(int points, boolean won) {
        finalPoints = points; gameWon = won;
    }
    // getter for final points
    public int     getFinalPoints() {
        return finalPoints;
    }
    // check if game won
    public boolean isGameWon() {
        return gameWon;
    }

    // clean up audio
    public void dispose() {
        if (audio        != null) audio.dispose();
        if (soundManager != null) soundManager.dispose();
    }
}
