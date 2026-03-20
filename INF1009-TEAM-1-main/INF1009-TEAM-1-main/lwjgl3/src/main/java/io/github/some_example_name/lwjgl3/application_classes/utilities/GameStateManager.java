package io.github.some_example_name.lwjgl3.application_classes.utilities;

/**
 * Singleton that tracks the current gameplay state (NORMAL, BAD, SECRET),
 * the player's remaining lives, and how many junk foods have been eaten
 * since the last reset.
 *
 * GameState transitions:
 *  NORMAL  → BAD    : triggered when junkFoodEaten reaches JUNK_FOOD_THRESHOLD (3)
 *  NORMAL  → SECRET : triggered when player score reaches SECRET_SCORE_THRESHOLD (200)
 *  BAD     → NORMAL : timer expires after 8 seconds
 *  SECRET  → NORMAL : timer expires after 15 seconds
 */
public class GameStateManager {

    /** The three possible gameplay states. */
    public enum GameState {
        NORMAL,   // Default gameplay
        BAD,      // Food-poisoning mode – reversed controls, reduced speed
        SECRET    // Super-player mode – faster, can eat enemies
    }

    private static GameStateManager instance;

    private GameState currentState = GameState.NORMAL;
    private int junkFoodEaten = 0;
    private int lives = 3;

    private GameStateManager() {}

    /** Returns the single shared instance, creating it on first call. */
    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    /**
     * Resets all state back to defaults.
     * Call this at the start of every new play session.
     */
    public void reset() {
        currentState = GameState.NORMAL;
        junkFoodEaten = 0;
        lives = 3;
    }

    // ── State ──────────────────────────────────────────────────────────────

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState state) {
        this.currentState = state;
    }

    // ── Junk food counter ──────────────────────────────────────────────────

    public int getJunkFoodEaten() {
        return junkFoodEaten;
    }

    public void incrementJunkFood() {
        junkFoodEaten++;
    }

    public void resetJunkFoodCount() {
        junkFoodEaten = 0;
    }

    // ── Lives ──────────────────────────────────────────────────────────────

    public int getLives() {
        return lives;
    }

    /** Decrements lives by one, minimum 0. */
    public void loseLife() {
        lives = Math.max(0, lives - 1);
    }
}
