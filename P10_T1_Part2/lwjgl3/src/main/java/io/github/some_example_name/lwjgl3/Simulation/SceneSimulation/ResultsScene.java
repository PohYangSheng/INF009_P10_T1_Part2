package io.github.some_example_name.lwjgl3.Simulation.SceneSimulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.SceneManager;

public class ResultsScene extends AbstractScene {

    private final SceneFactory factory;
    private Stage stage;
    private Skin  skin;

    public ResultsScene(SceneFactory factory) { this.factory = factory; }

    @Override
    public void create() {
        stage = new Stage();
        skin  = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        Gdx.input.setInputProcessor(stage);

        // Resume play.mp3 from where it was – don't restart it
        factory.getAudio().resume();

        buildUI();
    }

    private void buildUI() {
        boolean won    = factory.isGameWon();
        String  header = won ? "YOU WIN!" : "GAME OVER";
        Color   colour = won ? Color.YELLOW : Color.RED;

        Table root = new Table(); root.setFillParent(true); root.center();

        Label headerLbl = new Label(header, skin, "subtitle");
        headerLbl.setColor(colour);

        Label scoreLbl = new Label("Score: " + factory.getFinalPoints(), skin, "subtitle");
        scoreLbl.setColor(Color.WHITE);

        TextButton playAgain = btn("Play Again");
        TextButton menu      = btn("Back to Menu");

        playAgain.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                factory.getAudio().stop();
                AbstractScene fresh = factory.create(SceneFactory.SceneType.PLAY);
                fresh.setSceneManager(sceneManager);
                sceneManager.setScene(fresh);
            }
        });
        menu.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                factory.getAudio().stop();
                AbstractScene next = factory.create(SceneFactory.SceneType.HOME);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });

        root.add(headerLbl).center().padBottom(20).row();
        root.add(scoreLbl).center().padBottom(40).row();
        root.add(playAgain).size(160, 50).padBottom(12).row();
        root.add(menu).size(160, 50);
        stage.addActor(root);
    }

    @Override public void update(float dt) { ioManager.handleInput(); }

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
