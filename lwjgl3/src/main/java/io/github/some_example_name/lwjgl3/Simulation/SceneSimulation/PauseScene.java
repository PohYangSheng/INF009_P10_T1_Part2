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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.KenneyMenuWidgetFactory;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuKeyboardNavigator;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuStyleConfig;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuWidgetFactory;

// pause menu overlay
public class PauseScene extends AbstractScene {

    private final SceneFactory factory;
    private Stage stage;
    private Skin  skin;
    private Slider volumeSlider;
    private MenuKeyboardNavigator navigator;
    private MenuWidgetFactory widgetFactory;

    // constructor - takes in shared stuff from the factory
    public PauseScene(SceneFactory f) {
        this.factory = f;
    }

    // set up pause menu buttons and volume slider
    @Override
    public void create() {
        stage = new Stage();
        skin  = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        widgetFactory = new KenneyMenuWidgetFactory(skin);
        Gdx.input.setInputProcessor(stage);

        factory.getAudio().stop();
        factory.getAudio().play("music_loops/pause_theme.mp3", true);

        buildUI();
    }

    // build the UI layout
    private void buildUI() {
        Table t = new Table(); t.setFillParent(true); t.top();
        Label title = widgetFactory.createSubtitleLabel("PAUSED", Color.WHITE);
        t.add(title).center().padTop(30);
        stage.addActor(t);

        MenuStyleConfig styleConfig = MenuStyleConfig.getInstance();
        TextButton resume  = widgetFactory.createButton("Resume  [P]");
        TextButton restart = widgetFactory.createButton("Restart");
        TextButton quit    = widgetFactory.createButton("Quit to Menu");
        navigator = new MenuKeyboardNavigator(ioManager);
        Table bt = new Table(); bt.setFillParent(true); bt.center();
        bt.add(resume).spaceTop(30).size(styleConfig.getStandardButtonWidth(), styleConfig.getStandardButtonHeight()).row();
        bt.add(restart).spaceTop(12).size(styleConfig.getStandardButtonWidth(), styleConfig.getStandardButtonHeight()).row();
        bt.add(quit).spaceTop(12).size(styleConfig.getStandardButtonWidth(), styleConfig.getStandardButtonHeight());
        stage.addActor(bt);

        resume.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                doResume();
            }
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
                AbstractScene next = factory.create(SceneFactory.SceneType.MENU);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });

        navigator.register(resume, 0, 0, this::doResume);
        navigator.register(restart, 1, 0, () -> {
            factory.getAudio().stop();
            sceneManager.popScene();
            AbstractScene fresh = factory.create(SceneFactory.SceneType.PLAY);
            fresh.setSceneManager(sceneManager);
            sceneManager.setScene(fresh);
        });
        navigator.register(quit, 2, 0, () -> {
            factory.getAudio().stop();
            sceneManager.popScene();
            AbstractScene next = factory.create(SceneFactory.SceneType.MENU);
            next.setSceneManager(sceneManager);
            sceneManager.setScene(next);
        });

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
        volTable.setFillParent(true);
        volTable.bottom().right();
        volTable.add(volLabel).padBottom(20).padRight(10);
        volTable.add(volumeSlider).width(styleConfig.getSliderWidth()).padBottom(20).padRight(40);
        stage.addActor(volTable);
    }

    // do resume
    private void doResume() {
        factory.getAudio().stop();
        factory.getAudio().play("music_loops/gameplay_theme.mp3", true);
        sceneManager.popScene();

        Stage hud = sceneManager.getCurrentScene().getHudStage();
        if (hud != null) {
            InputMultiplexer mux = new InputMultiplexer();
            mux.addProcessor(hud);
            Gdx.input.setInputProcessor(mux);
        }
    }

    // runs every frame
    @Override
    public void update(float dt) {
        ioManager.handleInput();
        for (int key : ioManager.getKeyboard().getKeysPressedThisFrame()) {
            if (key == Input.Keys.P || key == Input.Keys.ESCAPE) {
                doResume();
                return;
            }
        }
        if (navigator != null) {
            navigator.update();
        }
    }

    // draw everything to screen
    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    // clean up textures/resources so we dont leak memory
    @Override
    public void dispose() {
        super.dispose();
        if (stage != null) stage.dispose();
        if (skin  != null) skin.dispose();
    }

}
