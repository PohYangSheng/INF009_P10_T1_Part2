package io.github.some_example_name.lwjgl3.application_classes.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneTransitionType;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneType;

/**
 * Tutorial scene explaining game mechanics and controls.
 */
public class TutorialScene implements Scene {
    // UI components
    private final TextButton backBtn;
    private final IOManager ioManager;
    private final Stage stage;
    private final Skin skin;

    /**
     * Constructs the tutorial scene.
     * 
     * @param sceneManager Manager responsible for scene transitions
     */
    public TutorialScene(GameSceneManager sceneManager) {
        // Initialize stage and input
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        
        // Load visual resources
        skin = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));

        // Create and style back button
        backBtn = createBackButton();

        // Create scene layout
        createSceneLayout(sceneManager);

        // Setup input manager
        ioManager = new IOManager();
        setupInputBindings(sceneManager);
    }

    /**
     * Creates and styles the back button.
     * 
     * @return Styled back button
     */
    private TextButton createBackButton() {
        TextButton button = new TextButton("Back", skin);
        button.getLabel().setFontScale(0.5f);
        return button;
    }

    /**
     * Sets up the scene layout with tutorial content.
     * 
     * @param sceneManager Manager for scene transitions
     */
    private void createSceneLayout(GameSceneManager sceneManager) {
        // Create root table
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
        
        // Add title
        Label titleLabel = new Label("Tutorial", skin, "subtitle");
        titleLabel.setAlignment(Align.center);
        rootTable.add(titleLabel).center().padTop(20).row();

        // Create and add tutorial content
        Table tutorialContentTable = createTutorialContentTable();
        rootTable.add(tutorialContentTable).expand().row();

        // Add back button
        rootTable.add(backBtn).size(150, 45).padBottom(30).align(Align.center);

        // Set back button listener
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.changeScene(SceneType.HOME, SceneTransitionType.NORMAL);
            }
        });
    }

    /**
     * Creates the tutorial content table with images and descriptions.
     * 
     * @return Table containing tutorial information
     */
    private Table createTutorialContentTable() {
        // Load tutorial images
        Texture healthyFoodTexture = new Texture(Gdx.files.internal("entities_images/banana.png"));
        Texture unhealthyFoodTexture = new Texture(Gdx.files.internal("entities_images/fries.png"));
        Texture demonTexture = new Texture(Gdx.files.internal("entities_images/angry_chef.png"));
        
        // Create tutorial content table
        Table tutorialContentTable = new Table();
        tutorialContentTable.defaults().pad(15).align(Align.left);

        // Healthy Food tutorial entry
        addTutorialEntry(
            tutorialContentTable, 
            new Image(healthyFoodTexture), 
            "Collect healthy food items to gain\nhealth, speed and points"
        );

        // Unhealthy Food tutorial entry
        addTutorialEntry(
            tutorialContentTable, 
            new Image(unhealthyFoodTexture), 
            "Collecting unhealthy food items\ndecreases health, speed and points"
        );

        // Enemy tutorial entry
        addTutorialEntry(
            tutorialContentTable, 
            new Image(demonTexture), 
            "Do not run into angry chef!\nInstant GAME OVER!"
        );

        return tutorialContentTable;
    }

    /**
     * Adds a tutorial entry with an image and description.
     * 
     * @param table Table to add the entry to
     * @param image Tutorial image
     * @param description Tutorial description
     */
    private void addTutorialEntry(Table table, Image image, String description) {
        // Scale and size the image
        image.setScale(1f);
        
        // Create description label
        Label descriptionLabel = new Label(description, skin);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.left);
        
        // Add to table
        table.add(image).size(80).padRight(30);
        table.add(descriptionLabel).width(300).row();
    }

    /**
     * Sets up input bindings for the tutorial scene.
     * 
     * @param sceneManager Manager for scene transitions
     */
    private void setupInputBindings(GameSceneManager sceneManager) {
        ioManager.addIOBind(
            ioManager.getKeyboard(), 
            Keys.ESCAPE, 
            () -> sceneManager.changeScene(SceneType.HOME, SceneTransitionType.NORMAL), 
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