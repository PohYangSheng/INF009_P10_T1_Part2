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

/**
 * Results scene displaying game outcome and final score.
 */
public class ResultsScene implements Scene {
    // UI components
    private final TextButton quitBtn;
    
    // Scene management
    private final Stage stage;
    private final Skin skin;

    /**
     * Constructs the results scene.
     * 
     * @param sceneManager Manager responsible for scene transitions
     */
    public ResultsScene(GameSceneManager sceneManager) {
        // Initialize stage and input
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        
        // Load visual resources
        skin = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));

        // Create and style quit button
        quitBtn = createQuitButton();

        // Create scene layout
        createSceneLayout(sceneManager);
    }

    /**
     * Creates and styles the quit button.
     * 
     * @return Styled quit button
     */
    private TextButton createQuitButton() {
        TextButton button = new TextButton("Quit", skin);
        button.getLabel().setFontScale(0.5f);
        return button;
    }

    /**
     * Sets up the scene layout with game results.
     * 
     * @param sceneManager Manager for scene transitions
     */
    private void createSceneLayout(GameSceneManager sceneManager) {
        // Create title table
        Table titleTable = createTitleTable();
        stage.addActor(titleTable);

        // Create results table
        Table resultTable = createResultTable(sceneManager);
        stage.addActor(resultTable);

        // Create button table
        Table buttonTable = createButtonTable(sceneManager);
        stage.addActor(buttonTable);
    }

    /**
     * Creates the title table.
     * 
     * @return Configured title Table
     */
    private Table createTitleTable() {
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();

        Label label = new Label("Results", skin, "subtitle");
        titleTable.add(label).center().padTop(20);

        return titleTable;
    }

    /**
     * Creates the results table with game outcome and points.
     * 
     * @return Configured results Table
     */
    // In ResultsScene.java, modify the createResultTable() method:
    private Table createResultTable(GameSceneManager sceneManager) {
        Table resultTable = new Table();
        resultTable.setFillParent(true);
        resultTable.center();

        // Use the scene manager to get results instead of static PlayScene methods
        String outcome = sceneManager.isGameWon() ? "You Win!" : "You Lose! Health is 0.";
        Label outcomeLabel = new Label(outcome, skin, "subtitle");
        resultTable.add(outcomeLabel).center().padTop(20).row();

        int points = sceneManager.getFinalPoints();
        String pointsText = "Points = " + points;
        Label pointsLabel = new Label(pointsText, skin, "subtitle"); 
        resultTable.add(pointsLabel).center().padTop(20);

        return resultTable;
    }

    /**
     * Creates the button table with quit option.
     * 
     * @param sceneManager Manager for scene transitions
     * @return Configured button Table
     */
    private Table createButtonTable(GameSceneManager sceneManager) {
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.bottom();

        // Add quit button
        buttonTable.add(quitBtn)
            .padBottom(30)
            .size(150, 45)
            .expandX()
            .center();

        // Set quit button listener
        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.changeScene(SceneType.HOME, SceneTransitionType.NORMAL);
            }
        });

        return buttonTable;
    }

    @Override
    public void render(SpriteBatch batch) {
        // Clear screen
        Gdx.gl20.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
        
        // Draw stage
        stage.draw();
    }

    @Override
    public void dispose() {
        // Clean up resources
        stage.dispose();
        skin.dispose();
    }
}