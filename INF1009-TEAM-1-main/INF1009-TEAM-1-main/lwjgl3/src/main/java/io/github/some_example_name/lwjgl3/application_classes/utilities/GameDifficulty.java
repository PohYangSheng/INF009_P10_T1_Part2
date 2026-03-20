package io.github.some_example_name.lwjgl3.application_classes.utilities;

/**
 * Singleton class that manages game difficulty settings.
 * Controls gameplay parameters based on the selected difficulty level.
 */
public class GameDifficulty {
    private static GameDifficulty instance = null;
    private Difficulty difficulty = Difficulty.EASY;

    /**
     * Available difficulty levels.
     */
    public enum Difficulty {
        EASY, NORMAL, HARD
    }
    
    /**
     * Gets the singleton instance, creating it if necessary.
     * 
     * @return The GameDifficulty singleton instance
     */
    public static synchronized GameDifficulty getInstance() {
        if (instance == null) {
            instance = new GameDifficulty();
        }
        return instance;
    }

    /**
     * Private constructor to enforce singleton pattern.
     */
    private GameDifficulty() {}

    /**
     * Gets the current difficulty level.
     * 
     * @return The current difficulty setting
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the game difficulty level.
     * 
     * @param difficulty The new difficulty setting
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}