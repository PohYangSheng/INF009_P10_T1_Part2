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
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.KenneyMenuWidgetFactory;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuKeyboardNavigator;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuStyleConfig;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuWidgetFactory;

// end screen - shows win/lose and final score
public class ScoreScene extends AbstractScene {

    private final SceneFactory factory;
    private Stage stage;
    private Skin  skin;
    private MenuKeyboardNavigator navigator;
    private MenuWidgetFactory widgetFactory;

    // constructor - takes in shared stuff from the factory
    public ScoreScene(SceneFactory factory) {
        this.factory = factory;
    }

    // set up the score screen
    @Override
    public void create() {
        stage = new Stage();
        skin  = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        widgetFactory = new KenneyMenuWidgetFactory(skin);
        Gdx.input.setInputProcessor(stage);

        factory.getAudio().resume();

        buildUI();
    }

    // build the UI layout
    private void buildUI() {
        boolean won    = factory.isGameWon();
        String  header = won ? "YOU WIN!" : "GAME OVER";
        Color   colour = won ? Color.YELLOW : Color.RED;

        Table root = new Table(); root.setFillParent(true); root.center();

        MenuStyleConfig styleConfig = MenuStyleConfig.getInstance();
        Label headerLbl = widgetFactory.createSubtitleLabel(header, colour);

        Label scoreLbl = widgetFactory.createSubtitleLabel("Score: " + factory.getFinalPoints(), Color.WHITE);

        TextButton playAgain = widgetFactory.createButton("Play Again");
        TextButton menu      = widgetFactory.createButton("Back to Menu");
        navigator = new MenuKeyboardNavigator(ioManager);

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
                AbstractScene next = factory.create(SceneFactory.SceneType.MENU);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });

        navigator.register(playAgain, 0, 0, () -> {
            factory.getAudio().stop();
            AbstractScene fresh = factory.create(SceneFactory.SceneType.PLAY);
            fresh.setSceneManager(sceneManager);
            sceneManager.setScene(fresh);
        });
        navigator.register(menu, 1, 0, () -> {
            factory.getAudio().stop();
            AbstractScene next = factory.create(SceneFactory.SceneType.MENU);
            next.setSceneManager(sceneManager);
            sceneManager.setScene(next);
        });

        root.add(headerLbl).center().padBottom(20).row();
        root.add(scoreLbl).center().padBottom(40).row();
        root.add(playAgain).size(styleConfig.getStandardButtonWidth() - 20f, styleConfig.getStandardButtonHeight()).padBottom(12).row();
        root.add(menu).size(styleConfig.getStandardButtonWidth() - 20f, styleConfig.getStandardButtonHeight());
        stage.addActor(root);
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
