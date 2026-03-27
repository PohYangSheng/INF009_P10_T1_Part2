package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

// stores the difficulty setting for this run
public class DifficultyConfig {

    public enum Difficulty { EASY, NORMAL, HARD }

    private Difficulty difficulty = Difficulty.EASY;

    // getter for selected difficulty
    public Difficulty getSelectedDifficulty() {
        return difficulty;
    }
    // setter for selected difficulty
    public void       setSelectedDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
