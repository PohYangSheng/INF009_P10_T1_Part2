package io.github.some_example_name.lwjgl3.Simulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.SceneManager;
import io.github.some_example_name.lwjgl3.Simulation.SceneSimulation.SceneFactory;

// main LibGDX application class for the game
public class GameMaster extends ApplicationAdapter {

    private SpriteBatch  batch;
    private SceneManager sceneManager;
    private SceneFactory sceneFactory;

    // creates the top-level objects for the game session and starts the opening scene
    @Override
    public void create() {
        batch        = new SpriteBatch();
        sceneFactory = new SceneFactory();
        sceneManager = new SceneManager();

        AbstractScene home = sceneFactory.create(SceneFactory.SceneType.MENU);
        home.setSceneManager(sceneManager);
        sceneManager.setScene(home);
    }

    // forwards the per-frame update and render loop to the SceneManager
    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        sceneManager.update(dt);
        sceneManager.render(batch);
    }

    // clean up textures/resources so we dont leak memory
    @Override
    public void dispose() {
        if (sceneManager != null) sceneManager.dispose();
        if (sceneFactory != null) sceneFactory.dispose();
        if (batch        != null) batch.dispose();
    }
}
