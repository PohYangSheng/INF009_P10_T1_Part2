package io.github.some_example_name.lwjgl3.Simulation.SceneSimulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;

import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.DifficultyConfig.Difficulty;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.KenneyMenuWidgetFactory;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuKeyboardNavigator;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuStyleConfig;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuWidgetFactory;

// difficulty selection screen
public class DifficultyScene extends AbstractScene {

    private final SceneFactory factory;
    private Stage stage;
    private Skin  skin;
    private MenuKeyboardNavigator navigator;
    private MenuWidgetFactory widgetFactory;

    // constructor - takes in shared stuff from the factory
    public DifficultyScene(SceneFactory factory) {
        this.factory = factory;
    }

    // set up the difficulty screen UI
    @Override
    public void create() {
        stage = new Stage();
        skin  = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        widgetFactory = new KenneyMenuWidgetFactory(skin);
        Gdx.input.setInputProcessor(stage);
        buildUI();
    }

    // build the UI layout
    private void buildUI() {
        Table titleTable = new Table(); titleTable.setFillParent(true); titleTable.top();
        titleTable.add(widgetFactory.createSubtitleLabel("Select a difficulty")).center().padTop(20);
        stage.addActor(titleTable);

        MenuStyleConfig styleConfig = MenuStyleConfig.getInstance();
        TextButton easyBtn   = widgetFactory.createCompactButton("Easy");
        TextButton normalBtn = widgetFactory.createCompactButton("Normal");
        TextButton hardBtn   = widgetFactory.createCompactButton("Hard");
        navigator = new MenuKeyboardNavigator(ioManager);
        Table btnTable = new Table(); btnTable.setFillParent(true); btnTable.center();
        btnTable.add(easyBtn).pad(10).size(styleConfig.getCompactButtonWidth() - 20f, styleConfig.getCompactButtonHeight());
        btnTable.add(normalBtn).pad(10).size(styleConfig.getCompactButtonWidth() - 20f, styleConfig.getCompactButtonHeight());
        btnTable.add(hardBtn).pad(10).size(styleConfig.getCompactButtonWidth() - 20f, styleConfig.getCompactButtonHeight());
        stage.addActor(btnTable);

        easyBtn.addListener(diffListener(Difficulty.EASY));
        normalBtn.addListener(diffListener(Difficulty.NORMAL));
        hardBtn.addListener(diffListener(Difficulty.HARD));

        TextButton backBtn = widgetFactory.createBackButton();
        Table backTable = new Table(); backTable.setFillParent(true); backTable.bottom();
        backTable.add(backBtn).padBottom(30).size(styleConfig.getCompactButtonWidth(), styleConfig.getCompactButtonHeight()).expandX().center();
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                AbstractScene next = factory.create(SceneFactory.SceneType.AVATAR_SELECTION);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });
        navigator.register(easyBtn, 0, 0, () -> chooseDifficulty(Difficulty.EASY));
        navigator.register(normalBtn, 0, 1, () -> chooseDifficulty(Difficulty.NORMAL));
        navigator.register(hardBtn, 0, 2, () -> chooseDifficulty(Difficulty.HARD));
        navigator.register(backBtn, 1, 1, () -> {
            AbstractScene next = factory.create(SceneFactory.SceneType.AVATAR_SELECTION);
            next.setSceneManager(sceneManager);
            sceneManager.setScene(next);
        });
        stage.addActor(backTable);
    }

    // click listener for difficulty buttons
    private ClickListener diffListener(Difficulty diff) {
        return new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                chooseDifficulty(diff);
            }
        };
    }

    // set the difficulty and go to play scene
    private void chooseDifficulty(Difficulty diff) {
        factory.getGameDifficulty().setSelectedDifficulty(diff);
        AbstractScene next = factory.create(SceneFactory.SceneType.PLAY);
        next.setSceneManager(sceneManager);
        sceneManager.setScene(next);
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
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
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
