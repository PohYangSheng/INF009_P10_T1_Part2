package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

/**
 * Tracks runtime game state: NORMAL / BAD / SECRET, lives and junk-food count.
 *
 * State transitions:
 *   NORMAL → BAD    : 3 junk foods eaten
 *   NORMAL → SECRET : player score reaches target
 *   BAD    → NORMAL : 8-second timer expires
 *   SECRET → RESULTS: 15-second timer expires (player wins)
 *
 * No singleton – one instance created per play session in PlayScene.
 */
public class GameStateManager {

    public enum GameState { NORMAL, BAD, SECRET }

    private GameState currentState  = GameState.NORMAL;
    private int       junkFoodEaten = 0;
    private int       lives         = 3;

    public void reset() { currentState = GameState.NORMAL; junkFoodEaten = 0; lives = 3; }

    public GameState getCurrentState()            { return currentState; }
    public void      setCurrentState(GameState s) { this.currentState = s; }

    public int  getJunkFoodEaten()   { return junkFoodEaten; }
    public void incrementJunkFood()  { junkFoodEaten++; }
    public void resetJunkFoodCount() { junkFoodEaten = 0; }

    public int  getLives() { return lives; }
    public void loseLife() { lives = Math.max(0, lives - 1); }
}
