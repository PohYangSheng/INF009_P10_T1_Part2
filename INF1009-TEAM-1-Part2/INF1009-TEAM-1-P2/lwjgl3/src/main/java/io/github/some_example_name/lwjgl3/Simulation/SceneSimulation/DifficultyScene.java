package io.github.some_example_name.lwjgl3.Simulation.SceneSimulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.SceneManager;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.GameDifficulty.Difficulty;

/** Difficulty selection: Easy / Normal / Hard. */
public class DifficultyScene extends AbstractScene {

    private final SceneFactory factory;
    private Stage stage;
    private Skin  skin;

    public DifficultyScene(SceneFactory factory) { this.factory = factory; }

    @Override
    public void create() {
        stage = new Stage();
        skin  = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        Gdx.input.setInputProcessor(stage);
        buildUI();
    }

    private void buildUI() {
        Table titleTable = new Table(); titleTable.setFillParent(true); titleTable.top();
        titleTable.add(new Label("Select a difficulty", skin, "subtitle")).center().padTop(20);
        stage.addActor(titleTable);

        TextButton easyBtn   = btn("Easy");
        TextButton normalBtn = btn("Normal");
        TextButton hardBtn   = btn("Hard");
        Table btnTable = new Table(); btnTable.setFillParent(true); btnTable.center();
        btnTable.add(easyBtn).pad(10).size(130,45);
        btnTable.add(normalBtn).pad(10).size(130,45);
        btnTable.add(hardBtn).pad(10).size(130,45);
        stage.addActor(btnTable);

        easyBtn.addListener(diffListener(Difficulty.EASY));
        normalBtn.addListener(diffListener(Difficulty.NORMAL));
        hardBtn.addListener(diffListener(Difficulty.HARD));

        TextButton backBtn = btn("Back");
        Table backTable = new Table(); backTable.setFillParent(true); backTable.bottom();
        backTable.add(backBtn).padBottom(30).size(150,45).expandX().center();
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                AbstractScene next = factory.create(SceneFactory.SceneType.HOME);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });
        stage.addActor(backTable);
    }

    private ClickListener diffListener(Difficulty diff) {
        return new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                factory.getGameDifficulty().setDifficulty(diff);
                AbstractScene next = factory.create(SceneFactory.SceneType.PLAY);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        };
    }

    @Override public void update(float dt) { ioManager.handleInput(); }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (stage != null) stage.dispose();
        if (skin  != null) skin.dispose();
    }

    private TextButton btn(String text) {
        TextButton b = new TextButton(text, skin); b.getLabel().setFontScale(0.5f); return b;
    }
}
