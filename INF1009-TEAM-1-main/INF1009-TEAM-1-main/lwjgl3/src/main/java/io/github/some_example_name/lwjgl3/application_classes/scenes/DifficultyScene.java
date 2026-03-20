package io.github.some_example_name.lwjgl3.application_classes.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneTransitionType;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneType;
import io.github.some_example_name.lwjgl3.application_classes.utilities.GameDifficulty;
import io.github.some_example_name.lwjgl3.application_classes.utilities.GameDifficulty.Difficulty;

/**
 * Scene for selecting game difficulty.
 */
public class DifficultyScene implements Scene {
    // UI components
    private final TextButton easyBtn;
    private final TextButton normalBtn;
    private final TextButton hardBtn;
    private final TextButton backBtn;
    
    // Scene management
    private final Stage stage;
    private final Skin skin;

    /**
     * Constructs the difficulty selection scene.
     * 
     * @param sceneManager Manager responsible for scene transitions
     */
    public DifficultyScene(GameSceneManager sceneManager) {
        // Initialize stage and input
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        
        // Load visual resources
        skin = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));

        // Create and style buttons
        easyBtn = createStyledButton("Easy");
        normalBtn = createStyledButton("Normal");
        hardBtn = createStyledButton("Hard");
        backBtn = createStyledButton("Back");

        // Create scene layout
        createSceneLayout(sceneManager);
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
     * Sets up the scene layout with title, difficulty buttons, and back button.
     * 
     * @param sceneManager Manager for scene transitions
     */
    private void createSceneLayout(GameSceneManager sceneManager) {
        // Create title table
        Table titleTable = createTitleTable();
        stage.addActor(titleTable);

        // Create difficulty buttons table
        Table buttonTable = createDifficultyButtonTable(sceneManager);
        stage.addActor(buttonTable);

        // Create back button table
        Table backTable = createBackButtonTable(sceneManager);
        stage.addActor(backTable);
    }

    /**
     * Creates the title table with scene title.
     * 
     * @return Configured title Table
     */
    private Table createTitleTable() {
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();

        Label label = new Label("Select a difficulty", skin, "subtitle");
        titleTable.add(label).center().padTop(20);

        return titleTable;
    }

    /**
     * Creates the difficulty button table.
     * 
     * @param sceneManager Manager for scene transitions
     * @return Configured button Table
     */
    private Table createDifficultyButtonTable(GameSceneManager sceneManager) {
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.center();

        // Add difficulty buttons side by side
        float buttonWidth = 130;
        float buttonHeight = 45;

        buttonTable.add(easyBtn).pad(10).size(buttonWidth, buttonHeight);
        buttonTable.add(normalBtn).pad(10).size(buttonWidth, buttonHeight);
        buttonTable.add(hardBtn).pad(10).size(buttonWidth, buttonHeight);

        // Set button listeners
        easyBtn.addListener(createDifficultyListener(sceneManager, Difficulty.EASY));
        normalBtn.addListener(createDifficultyListener(sceneManager, Difficulty.NORMAL));
        hardBtn.addListener(createDifficultyListener(sceneManager, Difficulty.HARD));

        return buttonTable;
    }

    /**
     * Creates the back button table.
     * 
     * @param sceneManager Manager for scene transitions
     * @return Configured back button Table
     */
    private Table createBackButtonTable(GameSceneManager sceneManager) {
        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.bottom();

        // Add back button
        backTable.add(backBtn).padBottom(30).size(150, 45).expandX().center();

        // Set back button listener
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.changeScene(SceneType.HOME, SceneTransitionType.NORMAL);
            }
        });

        return backTable;
    }

    /**
     * Creates a difficulty selection listener.
     * 
     * @param sceneManager Manager for scene transitions
     * @param difficulty Selected difficulty level
     * @return ClickListener for difficulty selection
     */
    private ClickListener createDifficultyListener(GameSceneManager sceneManager, Difficulty difficulty) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameDifficulty.getInstance().setDifficulty(difficulty);
                sceneManager.changeScene(SceneType.PLAY, SceneTransitionType.NORMAL);
            }
        };
    }

    @Override
    public void render(SpriteBatch batch) {
        // Clear screen and draw stage
        Gdx.gl20.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void dispose() {
        // Clean up resources
        stage.dispose();
        skin.dispose();
    }
}