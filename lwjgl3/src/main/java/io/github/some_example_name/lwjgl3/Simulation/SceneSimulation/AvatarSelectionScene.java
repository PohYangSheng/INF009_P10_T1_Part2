package io.github.some_example_name.lwjgl3.Simulation.SceneSimulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;

import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.CharacterSelectionManager;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.KenneyMenuWidgetFactory;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuKeyboardNavigator;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuStyleConfig;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuWidgetFactory;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.CharacterSelectionManager.CharacterOption;

import java.util.LinkedHashMap;
import java.util.Map;

// character select screen
public class AvatarSelectionScene extends AbstractScene {

    private final SceneFactory             factory;
    private Stage  stage;
    private Skin   skin;
    private Image  preview;
    private Label  selectedLabel;
    private final Map<CharacterOption, Texture>    textures = new LinkedHashMap<>();
    private final Map<CharacterOption, TextButton> buttons  = new LinkedHashMap<>();
    private MenuKeyboardNavigator navigator;
    private MenuWidgetFactory widgetFactory;

    // constructor - takes in shared stuff from the factory
    public AvatarSelectionScene(SceneFactory factory) {
        this.factory = factory;
    }

    // load character textures and build the selection UI
    @Override
    public void create() {
        stage = new Stage();
        skin  = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        widgetFactory = new KenneyMenuWidgetFactory(skin);
        Gdx.input.setInputProcessor(stage);

        factory.getAudio().resume();

        CharacterSelectionManager csm = factory.getCharSelection();
        for (CharacterOption opt : csm.getAvailableCharacters())
            textures.put(opt, new Texture(Gdx.files.internal(opt.getTexturePath())));

        preview       = new Image();
        selectedLabel = widgetFactory.createSubtitleLabel("");
        buildUI(csm);
        updateSelection(csm.getSelectedCharacter());
    }

    // build the UI layout
    private void buildUI(CharacterSelectionManager csm) {
        Table root = new Table(); root.setFillParent(true); root.pad(14);
        Label title = widgetFactory.createSubtitleLabel("Choose your character", Color.YELLOW);

        navigator = new MenuKeyboardNavigator(ioManager);

        Table charTable = new Table(); charTable.top();
        int rowIndex = 0;
        for (CharacterOption opt : csm.getAvailableCharacters()) {
            TextButton b = widgetFactory.createCompactButton(opt.getDisplayName());
            b.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    updateSelection(opt);
                }
            });
            buttons.put(opt, b);
            navigator.register(b, rowIndex, 0, () -> updateSelection(opt), () -> updateSelection(opt));
            charTable.add(b).width(260).height(48).padBottom(8).left().row();
            rowIndex++;
        }
        ScrollPane scroll = new ScrollPane(charTable, skin);
        scroll.setScrollingDisabled(true, false);

        Table previewTable = new Table();
        selectedLabel.setFontScale(MenuStyleConfig.getInstance().getSubtitleFontScale() - 0.1f);
        preview.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        previewTable.add(selectedLabel).center().padBottom(10).row();
        previewTable.add(preview).size(180, 180).center();

        TextButton continueBtn = widgetFactory.createButton("Continue");
        TextButton backBtn     = widgetFactory.createBackButton();
        Table footer = new Table();
        footer.add(backBtn).width(180).left();
        footer.add().expandX();
        footer.add(continueBtn).width(180).right();

        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                AbstractScene next = factory.create(SceneFactory.SceneType.MENU);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });
        continueBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                AbstractScene next = factory.create(SceneFactory.SceneType.DIFFICULTY);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });
        navigator.register(backBtn, rowIndex, 0, () -> {
            AbstractScene next = factory.create(SceneFactory.SceneType.MENU);
            next.setSceneManager(sceneManager);
            sceneManager.setScene(next);
        });
        navigator.register(continueBtn, rowIndex, 1, () -> {
            AbstractScene next = factory.create(SceneFactory.SceneType.DIFFICULTY);
            next.setSceneManager(sceneManager);
            sceneManager.setScene(next);
        });

        root.add(title).colspan(2).padBottom(10).center().row();
        root.add(scroll).width(280).expandY().fill().top().padRight(20);
        root.add(previewTable).expandX().fillX().top().row();
        root.add(footer).colspan(2).expandX().fillX().padTop(10).bottom();
        stage.addActor(root);
    }

    // highlight the selected character and update preview
    private void updateSelection(CharacterOption opt) {
        factory.getCharSelection().setSelectedCharacter(opt);
        selectedLabel.setText("Selected: " + opt.getDisplayName());
        preview.setDrawable(new TextureRegionDrawable(textures.get(opt)));
        for (Map.Entry<CharacterOption, TextButton> e : buttons.entrySet())
            e.getValue().setColor(e.getKey() == opt ? Color.GREEN : Color.WHITE);
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
        Array<Texture> unique = new Array<>(textures.values().toArray(new Texture[0]));
        for (Texture t : unique) t.dispose();
        if (stage != null) stage.dispose();
        if (skin  != null) skin.dispose();
    }

}
