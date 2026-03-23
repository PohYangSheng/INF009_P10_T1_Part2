package io.github.some_example_name.lwjgl3.Simulation.SceneSimulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.SceneManager;

/** Explains game mechanics with images. ESC or Back returns to Home. */
public class TutorialScene extends AbstractScene {

    private final SceneFactory factory;
    private Stage   stage;
    private Skin    skin;
    private Texture healthyTex, unhealthyTex, enemyTex;

    public TutorialScene(SceneFactory factory) { this.factory = factory; }

    @Override
    public void create() {
        stage        = new Stage();
        skin         = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        healthyTex   = new Texture(Gdx.files.internal("entities_images/banana.png"));
        unhealthyTex = new Texture(Gdx.files.internal("entities_images/fries.png"));
        enemyTex     = new Texture(Gdx.files.internal("entities_images/angry_chef.png"));
        Gdx.input.setInputProcessor(stage);
        buildUI();
    }

    private void buildUI() {
        Table root = new Table(); root.setFillParent(true); stage.addActor(root);
        root.add(new Label("Tutorial", skin, "subtitle")).center().padTop(20).row();

        Table content = new Table(); content.defaults().pad(15).align(Align.left);
        addEntry(content, healthyTex,  "Collect healthy food to gain health, speed and points.");
        addEntry(content, unhealthyTex,"Unhealthy food decreases health, speed and points.");
        addEntry(content, enemyTex,    "Avoid the angry chef – colliding costs you a life!");
        root.add(content).expand().row();

        TextButton backBtn = new TextButton("Back", skin);
        backBtn.getLabel().setFontScale(0.5f);
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                AbstractScene next = factory.create(SceneFactory.SceneType.HOME);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });
        root.add(backBtn).size(150,45).padBottom(30).align(Align.center);
    }

    private void addEntry(Table t, Texture tex, String desc) {
        Image img = new Image(tex);
        Label lbl = new Label(desc, skin); lbl.setWrap(true); lbl.setAlignment(Align.left);
        t.add(img).size(80).padRight(30);
        t.add(lbl).width(300).row();
    }

    @Override
    public void update(float dt) {
        ioManager.handleInput();
        for (int k : ioManager.getKeyboard().getKeysPressedThisFrame())
            if (k == Input.Keys.ESCAPE) {
                AbstractScene next = factory.create(SceneFactory.SceneType.HOME);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (stage        != null) stage.dispose();
        if (skin         != null) skin.dispose();
        if (healthyTex   != null) healthyTex.dispose();
        if (unhealthyTex != null) unhealthyTex.dispose();
        if (enemyTex     != null) enemyTex.dispose();
    }
}
