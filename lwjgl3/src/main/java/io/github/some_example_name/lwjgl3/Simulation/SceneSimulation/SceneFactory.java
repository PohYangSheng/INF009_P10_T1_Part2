package io.github.some_example_name.lwjgl3.Simulation.SceneSimulation;

import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.Audio;
import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.SoundManager;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.CharacterSelectionManager;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.GameDifficulty;

/**
 * Factory for all game scenes.
 *
 * Design Pattern: Factory Method – centralises scene creation so callers
 *   never instantiate scene classes directly. Adding a new scene only
 *   requires adding one case here (OCP).
 *
 * OOP:
 *   Encapsulation – session state (difficulty, character, audio, score)
 *     is owned here and injected into scenes rather than scattered as
 *     statics or singletons.
 *   Polymorphism  – create() returns AbstractScene so callers are
 *     decoupled from concrete scene types.
 *
 * SOLID:
 *   SRP – owns session-wide shared state only; no game logic.
 *   OCP – new scene types added by extending the SceneType enum and
 *         adding one switch case; no other class changes.
 *   DIP – scenes receive their dependencies via this factory rather
 *         than reaching for globals.
 */
public class SceneFactory {

    public enum SceneType {
        HOME, CHARACTER_SELECT, DIFFICULTY, TUTORIAL, PLAY, PAUSE, RESULTS
    }

    private final GameDifficulty            gameDifficulty;
    private final CharacterSelectionManager charSelection;

    /** Shared music controller – one track at a time across all scenes. */
    private final Audio        audio        = new Audio();

    /**
     * Shared SFX manager – replaces static Sound fields in collision handlers.
     * SOLID SRP fix: sound lifecycle is now owned by one dedicated object.
     */
    private final SoundManager soundManager = new SoundManager();

    private int     finalPoints = 0;
    private boolean gameWon     = false;

    public SceneFactory() {
        this.gameDifficulty = new GameDifficulty();
        this.charSelection  = new CharacterSelectionManager();
    }

    // ── Factory method ─────────────────────────────────────────────────────

    public AbstractScene create(SceneType type) {
        switch (type) {
            case HOME:             return new HomeScene(this);
            case CHARACTER_SELECT: return new CharacterSelectScene(this);
            case DIFFICULTY:       return new DifficultyScene(this);
            case TUTORIAL:         return new TutorialScene(this);
            case PLAY:             return new PlayScene(this);
            case PAUSE:            return new PauseScene(this);
            case RESULTS:          return new ResultsScene(this);
            default: throw new IllegalArgumentException("Unknown SceneType: " + type);
        }
    }

    // ── Accessors ──────────────────────────────────────────────────────────

    public GameDifficulty            getGameDifficulty() { return gameDifficulty; }
    public CharacterSelectionManager getCharSelection()  { return charSelection; }
    public Audio                     getAudio()          { return audio; }
    public SoundManager              getSoundManager()   { return soundManager; }

    public void    setGameResults(int points, boolean won) { finalPoints = points; gameWon = won; }
    public int     getFinalPoints()                        { return finalPoints; }
    public boolean isGameWon()                             { return gameWon; }
}
