package io.github.some_example_name.lwjgl3.Simulation.SceneSimulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.SceneManager;

public class PauseScene extends AbstractScene {

    private final SceneFactory factory;
    private Stage stage;
    private Skin  skin;

    public PauseScene(SceneFactory f) { this.factory = f; }

    @Override
    public void create() {
        stage = new Stage();
        skin  = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        Gdx.input.setInputProcessor(stage);

        // Play pause music
        factory.getAudio().stop();
        factory.getAudio().play("background_music/pause.mp3", true);

        buildUI();
    }

    private void buildUI() {
        Table t = new Table(); t.setFillParent(true); t.top();
        Label title = new Label("PAUSED", skin, "subtitle");
        title.setColor(Color.WHITE);
        t.add(title).center().padTop(30);
        stage.addActor(t);

        TextButton resume  = btn("Resume  [P]");
        TextButton restart = btn("Restart");
        TextButton quit    = btn("Quit to Menu");
        Table bt = new Table(); bt.setFillParent(true); bt.center();
        bt.add(resume).spaceTop(30).size(180, 50).row();
        bt.add(restart).spaceTop(12).size(180, 50).row();
        bt.add(quit).spaceTop(12).size(180, 50);
        stage.addActor(bt);

        resume.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { doResume(); }
        });
        restart.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                factory.getAudio().stop();
                sceneManager.popScene();
                AbstractScene fresh = factory.create(SceneFactory.SceneType.PLAY);
                fresh.setSceneManager(sceneManager);
                sceneManager.setScene(fresh);
            }
        });
        quit.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                factory.getAudio().stop();
                sceneManager.popScene();
                AbstractScene next = factory.create(SceneFactory.SceneType.HOME);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });
    }

    private void doResume() {
        // Stop pause music and resume play music
        factory.getAudio().stop();
        factory.getAudio().play("background_music/play.mp3", true);
        sceneManager.popScene();
        if (sceneManager.getCurrentScene() instanceof PlayScene) {
            Stage hud = sceneManager.getCurrentScene().getHudStage();
            if (hud != null) {
                InputMultiplexer mux = new InputMultiplexer();
                mux.addProcessor(hud);
                Gdx.input.setInputProcessor(mux);
            }
        }
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) doResume();
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (stage != null) stage.dispose();
        if (skin  != null) skin.dispose();
    }

    private TextButton btn(String t) {
        TextButton b = new TextButton(t, skin);
        b.getLabel().setFontScale(0.5f);
        return b;
    }
}
