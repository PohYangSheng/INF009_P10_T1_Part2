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
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.SceneManager;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.CharacterSelectionManager;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.CharacterSelectionManager.CharacterOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class CharacterSelectScene extends AbstractScene {

    private final SceneFactory             factory;
    private Stage  stage;
    private Skin   skin;
    private Image  preview;
    private Label  selectedLabel;
    private final Map<CharacterOption, Texture>    textures = new LinkedHashMap<>();
    private final Map<CharacterOption, TextButton> buttons  = new LinkedHashMap<>();

    public CharacterSelectScene(SceneFactory factory) { this.factory = factory; }

    @Override
    public void create() {
        stage = new Stage();
        skin  = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        Gdx.input.setInputProcessor(stage);

        // Resume home.mp3 from where it was – don't restart it
        factory.getAudio().resume();

        CharacterSelectionManager csm = factory.getCharSelection();
        for (CharacterOption opt : csm.getAvailableCharacters())
            textures.put(opt, new Texture(Gdx.files.internal(opt.getTexturePath())));

        preview       = new Image();
        selectedLabel = new Label("", skin, "subtitle");
        buildUI(csm);
        updateSelection(csm.getSelectedCharacter());
    }

    private void buildUI(CharacterSelectionManager csm) {
        Table root = new Table(); root.setFillParent(true); root.pad(14);
        Label title = new Label("Choose your character", skin, "subtitle");
        title.setColor(Color.YELLOW); title.setFontScale(0.7f);

        Table charTable = new Table(); charTable.top();
        for (CharacterOption opt : csm.getAvailableCharacters()) {
            TextButton b = btn(opt.getDisplayName());
            b.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) { updateSelection(opt); }
            });
            buttons.put(opt, b);
            charTable.add(b).width(260).height(48).padBottom(8).left().row();
        }
        ScrollPane scroll = new ScrollPane(charTable, skin);
        scroll.setScrollingDisabled(true, false);

        Table previewTable = new Table();
        selectedLabel.setFontScale(0.6f);
        preview.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        previewTable.add(selectedLabel).center().padBottom(10).row();
        previewTable.add(preview).size(180, 180).center();

        TextButton continueBtn = btn("Continue");
        TextButton backBtn     = btn("Back");
        Table footer = new Table();
        footer.add(backBtn).width(180).left();
        footer.add().expandX();
        footer.add(continueBtn).width(180).right();

        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                AbstractScene next = factory.create(SceneFactory.SceneType.HOME);
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

        root.add(title).colspan(2).padBottom(10).center().row();
        root.add(scroll).width(280).expandY().fill().top().padRight(20);
        root.add(previewTable).expandX().fillX().top().row();
        root.add(footer).colspan(2).expandX().fillX().padTop(10).bottom();
        stage.addActor(root);
    }

    private void updateSelection(CharacterOption opt) {
        factory.getCharSelection().setSelectedCharacter(opt);
        selectedLabel.setText("Selected: " + opt.getDisplayName());
        preview.setDrawable(new TextureRegionDrawable(textures.get(opt)));
        for (Map.Entry<CharacterOption, TextButton> e : buttons.entrySet())
            e.getValue().setColor(e.getKey() == opt ? Color.GREEN : Color.WHITE);
    }

    @Override public void update(float dt) { ioManager.handleInput(); }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.06f, 0.07f, 0.1f, 1f);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        Array<Texture> unique = new Array<>(textures.values().toArray(new Texture[0]));
        for (Texture t : unique) t.dispose();
        if (stage != null) stage.dispose();
        if (skin  != null) skin.dispose();
    }

    private TextButton btn(String text) {
        TextButton b = new TextButton(text, skin); b.getLabel().setFontScale(0.45f); return b;
    }
}
