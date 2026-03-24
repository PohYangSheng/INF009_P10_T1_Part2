package io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Manages the stack of active scenes.
 *
 * Improvements over Part 1:
 *  - Singleton REMOVED – instance is created in GameMaster and injected.
 *  - pushScene / popScene / setScene preserved (same API as Part 1).
 *  - Renders ALL scenes in stack so Game is visible behind Pause overlay.
 *
 * Design Pattern: used alongside Factory (SceneFactory) to create scenes.
 * SOLID: SRP – only manages the scene stack lifecycle.
 */
public class SceneManager {

    private final Stack<AbstractScene> scenes = new Stack<>();

    // ── Scene transitions ──────────────────────────────────────────────────

    /** Replace the current scene (e.g. Menu → Game). */
    public void setScene(AbstractScene scene) {
        if (!scenes.isEmpty()) scenes.pop().dispose();
        scenes.push(scene);
        scene.create();
    }

    /** Push a scene on top (e.g. Game → Pause). */
    public void pushScene(AbstractScene scene) {
        scenes.push(scene);
        scene.create();
    }

    /** Remove the top scene (e.g. Pause → Game). */
    public void popScene() {
        if (!scenes.isEmpty()) scenes.pop().dispose();
    }

    // ── Queries ────────────────────────────────────────────────────────────

    public AbstractScene getCurrentScene() {
        return scenes.isEmpty() ? null : scenes.peek();
    }

    public boolean isEmpty() { return scenes.isEmpty(); }

    // ── Game loop ──────────────────────────────────────────────────────────

    public void update(float dt) {
        if (!scenes.isEmpty()) scenes.peek().update(dt);
    }

    /**
     * Render all scenes bottom-up so the background scene (Game) is drawn
     * first and the Pause overlay appears on top.
     */
    public void render(SpriteBatch batch) {
        for (AbstractScene scene : scenes) scene.render(batch);
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────

    public void dispose() {
        while (!scenes.isEmpty()) scenes.pop().dispose();
    }
}
