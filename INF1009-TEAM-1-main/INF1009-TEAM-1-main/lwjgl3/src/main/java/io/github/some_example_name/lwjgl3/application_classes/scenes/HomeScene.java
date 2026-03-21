// This code shows the changes needed to implement the audio slider in HomeScene.java
// You would integrate these changes into your existing HomeScene class

package io.github.some_example_name.lwjgl3.application_classes.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.some_example_name.lwjgl3.abstract_engine.audio.AudioManager;
import io.github.some_example_name.lwjgl3.abstract_engine.io.IOManager;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneTransitionType;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneType;

/**
 * Home scene of the game, serving as the main menu.
 * Now includes an audio volume slider.
 */
public class HomeScene implements Scene {
    // UI components
    private final TextButton startBtn;
    private final TextButton tutBtn;
    private final TextButton quitBtn;
    private final Slider volumeSlider;
    private final Label volumeLabel;
    
    // Scene management
    private final Stage stage;
    private final Skin skin;
    private final Texture homeBackground;
    private final IOManager ioManager;

    /**
     * Constructs the home scene.
     * 
     * @param sceneManager Manager responsible for scene transitions
     */
    public HomeScene(GameSceneManager sceneManager) {
        // Initialize stage and input
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        
        // Load visual resources
        homeBackground = new Texture(Gdx.files.internal("background_images/homebackground.jpg"));
        skin = new Skin(Gdx.files.internal("pixthulhu/skin/pixthulhu-ui.json"));
        
        // Set background music
        AudioManager.getInstance().setSceneBGM("background_music/home.mp3");

        // Create and style buttons
        startBtn = createStyledButton("Start");
        tutBtn = createStyledButton("Tutorial");
        quitBtn = createStyledButton("Quit");
        
        // Create volume slider
        volumeSlider = createVolumeSlider();
        volumeLabel = new Label("Volume:", skin);
        volumeLabel.setColor(Color.YELLOW);

        // Create and populate scene layout
        createSceneLayout(sceneManager);

        // Initialize input manager
        ioManager = new IOManager();
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
        button.getLabel().setColor(Color.SKY);
        button.setColor(Color.ROYAL);
        return button;
    }
    
    /**
     * Creates and configures the volume slider.
     * 
     * @return Configured volume slider
     */
    private Slider createVolumeSlider() {
        // Create slider with range 0-1
        Slider slider = new Slider(0f, 1f, 0.05f, false, skin);
        
        // Set initial value from AudioPlayer's current volume
        float currentVolume = AudioManager.getInstance().getVolume();
        slider.setValue(currentVolume);
        
        // Add listener to update volume when slider is moved
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = slider.getValue();
                AudioManager.getInstance().setVolume(volume);
            }
        });
        
        return slider;
    }

    /**
     * Sets up the scene layout and button interactions.
     * 
     * @param sceneManager Manager for scene transitions
     */
    private void createSceneLayout(GameSceneManager sceneManager) {
        // Create background
        Image backgroundImage = new Image(new TextureRegion(homeBackground));
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.addActor(backgroundImage);

        // Create title table
        Table titleTable = createTitleTable();
        stage.addActor(titleTable);

        // Create button table
        Table buttonTable = createButtonTable(sceneManager);
        stage.addActor(buttonTable);
        
        // Create volume controls table
        Table volumeTable = createVolumeTable();
        stage.addActor(volumeTable);
    }

    /**
     * Creates the title table with game name.
     * 
     * @return Configured title Table
     */
    private Table createTitleTable() {
        Table titleTable = new Table();
        titleTable.setFillParent(true);
        titleTable.top();

        Label label = new Label("Hungry Dash", skin, "title");
        label.setColor(Color.YELLOW);
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
        buttonTable.center().padTop(170);

        // Add buttons with consistent sizing
        float buttonWidth = startBtn.getWidth() * 0.6f;
        float buttonHeight = startBtn.getHeight() * 0.6f;

        buttonTable.add(startBtn).size(buttonWidth, buttonHeight).row();
        buttonTable.add(tutBtn).padTop(10).size(buttonWidth, buttonHeight).row();
        buttonTable.add(quitBtn).padTop(10).size(buttonWidth, buttonHeight);

        // Set button listeners
        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.changeScene(SceneType.CHARACTER_SELECT, SceneTransitionType.NORMAL);
            }
        });
        
        tutBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.changeScene(SceneType.TUTORIAL, SceneTransitionType.NORMAL);
            }
        });

        quitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        return buttonTable;
    }
    
    /**
     * Creates the volume control table with slider and label.
     * 
     * @return Configured volume Table
     */
    private Table createVolumeTable() {
        Table volumeTable = new Table();
        volumeTable.setFillParent(true);
        volumeTable.bottom().right();
        
        // Add volume label and slider to table
        volumeTable.add(volumeLabel).padBottom(20).padRight(10);
        volumeTable.add(volumeSlider).width(200).padBottom(20).padRight(40);
        
        return volumeTable;
    }

    @Override
    public void render(SpriteBatch batch) {
        // Render the stage
        stage.draw();
        
        // Handle any input
        ioManager.handleInput();
    }

    @Override
    public void dispose() {
        // Clean up resources
        stage.dispose();
        skin.dispose();
        homeBackground.dispose();
    }
}