package io.github.some_example_name.lwjgl3.Simulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.SceneManager;
import io.github.some_example_name.lwjgl3.Simulation.SceneSimulation.SceneFactory;

/**
 * Application entry point – owns the render loop and top-level managers.
 *
 * Responsibilities (SRP):
 *   - Create one SpriteBatch shared across all scenes.
 *   - Create SceneFactory (session state) and SceneManager (stack lifecycle).
 *   - Start the application on the HomeScene.
 *   - Forward render() and dispose() to SceneManager.
 *
 * No singletons anywhere – all dependencies flow downward via injection.
 *
 * Design Patterns present:
 *   1. Template Method – AbstractScene.create/update/render lifecycle
 *   2. Factory         – SceneFactory.create(SceneType)
 *   3. Strategy        – CollisionResolution dispatches per entity-pair type
 *   4. Observer        – IDialogEventListener / DialogPopUpManager
 *   5. Command         – AudioSimulation / IOSimulation action lists
 */
public class GameMaster extends ApplicationAdapter {

    private SpriteBatch  batch;
    private SceneManager sceneManager;
    private SceneFactory sceneFactory;

    @Override
    public void create() {
        batch        = new SpriteBatch();
        sceneFactory = new SceneFactory();
        sceneManager = new SceneManager();

        // No downcast needed – setSceneManager is on AbstractScene
        AbstractScene home = sceneFactory.create(SceneFactory.SceneType.HOME);
        home.setSceneManager(sceneManager);
        sceneManager.setScene(home);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        sceneManager.update(dt);
        sceneManager.render(batch);
    }

    @Override
    public void dispose() {
        if (sceneManager != null) sceneManager.dispose();
        if (batch        != null) batch.dispose();
    }
}
