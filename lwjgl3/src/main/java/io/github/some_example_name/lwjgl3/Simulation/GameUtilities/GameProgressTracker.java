package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

// tracks game state - lives, junk food eaten, current mode
public class GameProgressTracker {

    public enum GameState { NORMAL, BAD, SECRET }

    private GameState currentState  = GameState.NORMAL;
    private int       junkFoodEaten = 0;
    private int       lives         = 3;

    // reset
    public void reset() {
        currentState = GameState.NORMAL; junkFoodEaten = 0; lives = 3;
    }

    // getter for current state
    public GameState getCurrentState() {
        return currentState;
    }
    // setter for current state
    public void      setCurrentState(GameState s) {
        this.currentState = s;
    }

    // getter for junk food eaten
    public int  getJunkFoodEaten() {
        return junkFoodEaten;
    }
    // ate another junk food
    public void incrementJunkFood() {
        junkFoodEaten++;
    }
    // reset junk food counter
    public void resetJunkFoodCount() {
        junkFoodEaten = 0;
    }

    // getter for lives
    public int  getLives() {
        return lives;
    }
    // lose a life
    public void loseLife() {
        lives = Math.max(0, lives - 1);
    }
}
