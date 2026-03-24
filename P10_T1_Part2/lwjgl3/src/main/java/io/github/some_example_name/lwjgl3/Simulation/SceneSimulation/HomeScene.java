package io.github.some_example_name.lwjgl3.Simulation.SceneSimulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.SceneManager;

public class HomeScene extends AbstractScene {

    private final SceneFactory factory;
    private Stage   stage;
    private Skin    skin;
    private Texture background;
    private Slider  volumeSlider;

    public HomeScene(SceneFactory factory) { this.factory = factory; }

    @Override
    public void create() {
        stage      = new Stage();
        skin       = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        background = new Texture(Gdx.files.internal("background_images/homebackground.jpg"));
        Gdx.input.setInputProcessor(stage);

        factory.getAudio().play("background_music/home.mp3", true);

        buildUI();
    }

    private void buildUI() {
        Image bg = new Image(new TextureRegionDrawable(new TextureRegion(background)));
        bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(bg);

        Table titleTable = new Table();
        titleTable.setFillParent(true); titleTable.top();
        Label title = new Label("Munch Maze", skin, "subtitle");
        title.setColor(Color.GREEN);
        titleTable.add(title).center().padTop(20).row();
        titleTable.add(new Label("Find your way. Feed your hunger.", skin)).center().padTop(4);
        stage.addActor(titleTable);

        TextButton startBtn = btn("Start");
        TextButton tutBtn   = btn("Tutorial");
        TextButton quitBtn  = btn("Quit");
        Table btnTable = new Table();
        btnTable.setFillParent(true); btnTable.center().padTop(170);
        btnTable.add(startBtn).size(150, 45).row();
        btnTable.add(tutBtn).padTop(10).size(150, 45).row();
        btnTable.add(quitBtn).padTop(10).size(150, 45);
        stage.addActor(btnTable);

        // No stop() here – let home.mp3 keep playing into CharacterSelectScene
        startBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                AbstractScene next = factory.create(SceneFactory.SceneType.CHARACTER_SELECT);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });
        tutBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                factory.getAudio().stop();
                AbstractScene next = factory.create(SceneFactory.SceneType.TUTORIAL);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });
        quitBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                factory.getAudio().stop();
                Gdx.app.exit();
            }
        });

        volumeSlider = new Slider(0f, 100f, 5f, false, skin);
        volumeSlider.setValue(factory.getAudio().getVolumePercent());
        volumeSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                factory.getAudio().setVolumePercent(volumeSlider.getValue());
            }
        });
        Label volLabel = new Label("Volume:", skin); volLabel.setColor(Color.WHITE);
        Table volTable = new Table();
        volTable.setFillParent(true); volTable.bottom().right();
        volTable.add(volLabel).padBottom(20).padRight(10);
        volTable.add(volumeSlider).width(200).padBottom(20).padRight(40);
        stage.addActor(volTable);
    }

    @Override public void update(float dt) { ioManager.handleInput(); }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.06f, 0.07f, 0.1f, 1f);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (stage      != null) stage.dispose();
        if (skin       != null) skin.dispose();
        if (background != null) background.dispose();
    }

    private TextButton btn(String text) {
        TextButton b = new TextButton(text, skin);
        b.getLabel().setFontScale(0.5f);
        return b;
    }
}
