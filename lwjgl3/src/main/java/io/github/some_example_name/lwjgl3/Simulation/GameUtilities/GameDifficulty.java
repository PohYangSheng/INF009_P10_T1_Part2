package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

/**
 * Stores the player's chosen difficulty for the current session.
 * Plain object – created once in GameMaster and passed by injection.
 * No singleton.
 */
public class GameDifficulty {

    public enum Difficulty { EASY, NORMAL, HARD }

    private Difficulty difficulty = Difficulty.EASY;

    public Difficulty getDifficulty()                     { return difficulty; }
    public void       setDifficulty(Difficulty difficulty){ this.difficulty = difficulty; }
}
