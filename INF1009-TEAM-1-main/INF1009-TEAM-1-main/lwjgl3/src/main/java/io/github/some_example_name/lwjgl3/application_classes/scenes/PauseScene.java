package io.github.some_example_name.lwjgl3.application_classes.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.some_example_name.lwjgl3.abstract_engine.audio.AudioPlayer;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneTransitionType;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneType;

/**
 * Pause scene for the game, providing options to resume, restart, or quit.
 */
public class PauseScene implements Scene {
    // UI components
    private final TextButton resumeBtn;
    private final TextButton restartBtn;
    private final TextButton quitBtn;
    
    // Scene management
    private final Stage stage;
    private final Skin skin;
    private final IOManager ioManager;

    /**
     * Constructs the pause scene.
     * 
     * @param sceneManager Manager responsible for scene transitions
     */
    public PauseScene(GameSceneManager sceneManager) {
        // Initialize stage and input
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        
        // Load visual resources
        skin = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        
        // Set pause scene music
        AudioPlayer.getInstance().setSceneBGM("background_music/pause.mp3");

        // Create and style buttons
        resumeBtn = createStyledButton("Resume");
        restartBtn = createStyledButton("Restart");
        quitBtn = createStyledButton("Quit");

        // Create scene layout
        createSceneLayout(sceneManager);

        // Initialize input manager and keybindings
        ioManager = new IOManager();
        setupInputBindings(sceneManager);
    }

    /**
     * Creates a styled button with consistent appearance.
     * 
     * @param text Button text
     * @return Styled TextButton
     */
    private TextButton createStyledButton(String text) {
        TextButton button = new TextButton(text, skin);
        button.getLabel().setFontScale(0.5f);
        return button;
    }

    /**
     * Sets up the scene layout with title and buttons.
     * 
     * @param sceneManager Manager for scene transitions
     */
    private void createSceneLayout(GameSceneManager sceneManager) {
        // Create title table
        Table titleTable = createTitleTable();
        stage.addActor(titleTable);

        // Create button table
        Table buttonTable = createButtonTable(sceneManager);
        stage.addActor(buttonTable);
    }

    /**
     * Creates the title table with "Pause" label.
     * 
     * @return Configured title Table
     */
    private Table createTitleTable() {
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();

        Label label = new Label("Pause", skin, "subtitle");
        titleTable.add(label).center().padTop(20);

        return titleTable;
    }

    /**
     * Creates the button table with navigation options.
     * 
     * @param sceneManager Manager for scene transitions
     * @return Configured button Table
     */
    private Table createButtonTable(GameSceneManager sceneManager) {
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.center();

        // Add buttons with spacing
        float buttonWidth = 150;
        float buttonHeight = 45;

        buttonTable.add(resumeBtn).spaceTop(30).size(buttonWidth, buttonHeight).expandX().center().row();
        buttonTable.add(restartBtn).spaceTop(10).size(buttonWidth, buttonHeight).expandX().center().row();
        buttonTable.add(quitBtn).spaceTop(10).size(buttonWidth, buttonHeight).expandX().center();

        // Set button listeners
        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.changeScene(SceneType.PLAY, SceneTransitionType.RESUME);
            }
        });

        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.changeScene(SceneType.PLAY, SceneTransitionType.RESTART);
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.changeScene(SceneType.HOME, SceneTransitionType.NORMAL);
            }
        });

        return buttonTable;
    }

    /**
     * Sets up input bindings for the pause scene.
     * 
     * @param sceneManager Manager for scene transitions
     */
    private void setupInputBindings(GameSceneManager sceneManager) {
        ioManager.addIOBind(
            ioManager.getKeyboard(), 
            Keys.ESCAPE, 
            () -> sceneManager.changeScene(SceneType.PLAY, SceneTransitionType.RESUME), 
            true
        );
    }

    @Override
    public void render(SpriteBatch batch) {
        // Clear screen and draw stage
        Gdx.gl20.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

        // Handle input
        ioManager.handleInput();
    }
    
    @Override
    public void dispose() {
        // Clean up resources
        stage.dispose();
        skin.dispose();
    }
}