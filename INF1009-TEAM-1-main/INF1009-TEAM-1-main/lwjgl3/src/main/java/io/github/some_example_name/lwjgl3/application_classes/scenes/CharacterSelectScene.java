package io.github.some_example_name.lwjgl3.application_classes.scenes;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.lwjgl3.abstract_engine.scene.Scene;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneTransitionType;
import io.github.some_example_name.lwjgl3.abstract_engine.scene.SceneType;
import io.github.some_example_name.lwjgl3.application_classes.utilities.CharacterSelectionManager;
import io.github.some_example_name.lwjgl3.application_classes.utilities.CharacterSelectionManager.CharacterOption;

/**
 * Scene for selecting the player's character before choosing difficulty.
 */
public class CharacterSelectScene implements Scene {
    private final Stage stage;
    private final Skin skin;
    private final CharacterSelectionManager selectionManager;
    private final Map<CharacterOption, Texture> characterTextures;
    private final Map<CharacterOption, TextButton> characterButtons;

    private final TextButton continueBtn;
    private final TextButton backBtn;
    private final Image previewImage;
    private final Label selectedCharacterLabel;

    public CharacterSelectScene(GameSceneManager sceneManager) {
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("pixthulhu/skin/pixthulhu-ui.json"));
        selectionManager = CharacterSelectionManager.getInstance();
        characterTextures = new LinkedHashMap<>();
        characterButtons = new LinkedHashMap<>();

        Gdx.input.setInputProcessor(stage);

        previewImage = new Image();
        selectedCharacterLabel = new Label("", skin, "subtitle");
        continueBtn = createStyledButton("Continue");
        backBtn = createStyledButton("Back");

        loadCharacterTextures();
        createLayout(sceneManager);
        updateSelectedCharacter(selectionManager.getSelectedCharacter());
    }

    private void loadCharacterTextures() {
        for (CharacterOption option : selectionManager.getAvailableCharacters()) {
            characterTextures.put(option, new Texture(Gdx.files.internal(option.getTexturePath())));
        }
    }

    private TextButton createStyledButton(String text) {
        TextButton button = new TextButton(text, skin);
        button.getLabel().setFontScale(0.45f);
        button.getLabel().setColor(Color.SKY);
        button.setColor(Color.ROYAL);
        return button;
    }

    private void createLayout(GameSceneManager sceneManager) {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(14);

        Label titleLabel = new Label("Choose your character", skin, "subtitle");
        titleLabel.setColor(Color.YELLOW);
        titleLabel.setFontScale(0.7f);

        Table previewTable = createPreviewTable();
        Table characterTable = createCharacterTable();
        Table footerTable = createFooterTable(sceneManager);

        // Wrap character buttons in a scroll pane so they don't push footer off-screen
        ScrollPane scrollPane = new ScrollPane(characterTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        // Row 1: title
        rootTable.add(titleLabel).colspan(2).padBottom(10).center().row();
        // Row 2: scrollable character list + preview (takes remaining space)
        rootTable.add(scrollPane).width(280).expandY().fill().top().padRight(20);
        rootTable.add(previewTable).expandX().fillX().top().row();
        // Row 3: footer pinned at bottom
        rootTable.add(footerTable).colspan(2).expandX().fillX().padTop(10).bottom();

        stage.addActor(rootTable);
    }

    private Table createCharacterTable() {
        Table characterTable = new Table();
        characterTable.top();

        for (CharacterOption option : selectionManager.getAvailableCharacters()) {
            TextButton button = createStyledButton(option.getDisplayName());
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    updateSelectedCharacter(option);
                }
            });

            characterButtons.put(option, button);
            characterTable.add(button).width(260).height(48).padBottom(8).left().row();
        }

        return characterTable;
    }

    private Table createPreviewTable() {
        Table previewTable = new Table();

        previewImage.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        selectedCharacterLabel.setFontScale(0.6f);

        previewTable.add(selectedCharacterLabel).center().padBottom(10).row();
        previewTable.add(previewImage).size(180, 180).center();

        return previewTable;
    }

    private Table createFooterTable(GameSceneManager sceneManager) {
        Table footerTable = new Table();
        footerTable.add(backBtn).width(180).left();
        footerTable.add().expandX();
        footerTable.add(continueBtn).width(180).right();

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.changeScene(SceneType.HOME, SceneTransitionType.NORMAL);
            }
        });

        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.changeScene(SceneType.DIFFICULTY, SceneTransitionType.NORMAL);
            }
        });

        return footerTable;
    }

    private void updateSelectedCharacter(CharacterOption option) {
        selectionManager.setSelectedCharacter(option);
        selectedCharacterLabel.setText("Selected: " + option.getDisplayName());
        previewImage.setDrawable(new TextureRegionDrawable(characterTextures.get(option)));

        for (Map.Entry<CharacterOption, TextButton> entry : characterButtons.entrySet()) {
            if (entry.getKey() == option) {
                entry.getValue().setColor(Color.FOREST);
            } else {
                entry.getValue().setColor(Color.ROYAL);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0.06f, 0.07f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        Array<Texture> uniqueTextures = new Array<>(characterTextures.values().toArray(new Texture[0]));
        for (Texture texture : uniqueTextures) {
            texture.dispose();
        }
        stage.dispose();
        skin.dispose();
    }
}