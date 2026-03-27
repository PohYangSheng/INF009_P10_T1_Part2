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
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.KenneyMenuWidgetFactory;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuKeyboardNavigator;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuStyleConfig;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuWidgetFactory;

// main menu screen
public class MenuScene extends AbstractScene {

    private final SceneFactory factory;
    private Stage   stage;
    private Skin    skin;
    private Texture background;
    private Slider  volumeSlider;
    private MenuKeyboardNavigator navigator;
    private MenuWidgetFactory widgetFactory;

    // constructor - takes in shared stuff from the factory
    public MenuScene(SceneFactory factory) {
        this.factory = factory;
    }

    // set up the main menu with start, tutorial, quit buttons
    @Override
    public void create() {
        stage      = new Stage();
        skin       = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        background = new Texture(Gdx.files.internal("scene_images/title_lobby_bg.jpg"));
        widgetFactory = new KenneyMenuWidgetFactory(skin);
        Gdx.input.setInputProcessor(stage);

        if (!factory.getAudio().isPlaying()) {

            factory.getAudio().play("music_loops/menu_theme.mp3", true);

        }
        buildUI();
    }

    // build the UI layout
    private void buildUI() {
        Image bg = new Image(new TextureRegionDrawable(new TextureRegion(background)));
        bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(bg);

        Table titleTable = new Table();
        titleTable.setFillParent(true); titleTable.top();
        Label title = widgetFactory.createSubtitleLabel("Munch Maze", Color.GREEN);
        titleTable.add(title).center().padTop(20).row();
        titleTable.add(widgetFactory.createStandardLabel("Find your way. Feed your hunger.")).center().padTop(4);
        stage.addActor(titleTable);

        TextButton startBtn = widgetFactory.createButton("Start");
        TextButton tutBtn   = widgetFactory.createButton("Tutorial");
        TextButton quitBtn  = widgetFactory.createButton("Quit");
        navigator = new MenuKeyboardNavigator(ioManager);
        Table btnTable = new Table();
        btnTable.setFillParent(true); btnTable.center().padTop(170);
        btnTable.add(startBtn).size(150, 45).row();
        btnTable.add(tutBtn).padTop(10).size(150, 45).row();
        btnTable.add(quitBtn).padTop(10).size(150, 45);
        stage.addActor(btnTable);

        startBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                AbstractScene next = factory.create(SceneFactory.SceneType.AVATAR_SELECTION);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });
        tutBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
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

        navigator.register(startBtn, 0, 0, () -> {
            AbstractScene next = factory.create(SceneFactory.SceneType.AVATAR_SELECTION);
            next.setSceneManager(sceneManager);
            sceneManager.setScene(next);
        });
        navigator.register(tutBtn, 1, 0, () -> {
            AbstractScene next = factory.create(SceneFactory.SceneType.TUTORIAL);
            next.setSceneManager(sceneManager);
            sceneManager.setScene(next);
        });
        navigator.register(quitBtn, 2, 0, () -> {
            factory.getAudio().stop();
            Gdx.app.exit();
        });

        MenuStyleConfig styleConfig = MenuStyleConfig.getInstance();
        volumeSlider = widgetFactory.createVolumeSlider(0f, 100f, 5f);
        volumeSlider.setValue(factory.getAudio().getVolumePercent());
        volumeSlider.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                float val = volumeSlider.getValue();
                factory.getAudio().setVolumePercent(val);
                factory.getSoundManager().setVolume(val / 100f);
            }
        });
        Label volLabel = widgetFactory.createStandardLabel("Volume:", Color.WHITE);
        Table volTable = new Table();
        volTable.setFillParent(true); volTable.bottom().right();
        volTable.add(volLabel).padBottom(20).padRight(10);
        volTable.add(volumeSlider).width(styleConfig.getSliderWidth()).padBottom(20).padRight(40);
        stage.addActor(volTable);
    }

    // runs every frame
    @Override
    public void update(float dt) {
        ioManager.handleInput();
        if (navigator != null) navigator.update();
    }

    // draw everything to screen
    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.06f, 0.07f, 0.1f, 1f);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    // clean up textures/resources so we dont leak memory
    @Override
    public void dispose() {
        super.dispose();
        if (stage      != null) stage.dispose();
        if (skin       != null) skin.dispose();
        if (background != null) background.dispose();
    }

}
