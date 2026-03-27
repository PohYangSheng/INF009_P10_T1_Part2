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
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.KenneyMenuWidgetFactory;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuKeyboardNavigator;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuStyleConfig;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.MenuWidgetFactory;

// tutorial screen showing how to play
public class TutorialScene extends AbstractScene {

    private final SceneFactory factory;
    private Stage   stage;
    private Skin    skin;
    private Texture healthyTex, unhealthyTex, enemyTex;
    private MenuKeyboardNavigator navigator;
    private MenuWidgetFactory widgetFactory;

    // constructor - takes in shared stuff from the factory
    public TutorialScene(SceneFactory factory) {
        this.factory = factory;
    }

    // set up tutorial page with icons and descriptions
    @Override
    public void create() {
        stage        = new Stage();
        skin         = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));
        healthyTex   = new Texture(Gdx.files.internal("sprite_assets/watermelon_pickup.png"));
        unhealthyTex = new Texture(Gdx.files.internal("sprite_assets/pizza_pickup.png"));
        enemyTex     = new Texture(Gdx.files.internal("sprite_assets/rogue_chef.png"));
        widgetFactory = new KenneyMenuWidgetFactory(skin);
        Gdx.input.setInputProcessor(stage);
        buildUI();
    }

    // build the UI layout
    private void buildUI() {
        Table root = new Table(); root.setFillParent(true); stage.addActor(root);
        root.add(widgetFactory.createSubtitleLabel("Tutorial")).center().padTop(20).row();

        Table content = new Table(); content.defaults().pad(15).align(Align.left);
        addEntry(content, healthyTex,  "Collect healthy food to gain health, speed and points.");
        addEntry(content, unhealthyTex,"Unhealthy food decreases health, speed and points.");
        addEntry(content, enemyTex,    "Avoid the angry chef – colliding costs you a life!");
        root.add(content).expand().row();

        MenuStyleConfig styleConfig = MenuStyleConfig.getInstance();
        TextButton backBtn = widgetFactory.createBackButton();
        navigator = new MenuKeyboardNavigator(ioManager);
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                AbstractScene next = factory.create(SceneFactory.SceneType.MENU);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
            }
        });
        navigator.register(backBtn, 0, 0, () -> {
            AbstractScene next = factory.create(SceneFactory.SceneType.MENU);
            next.setSceneManager(sceneManager);
            sceneManager.setScene(next);
        });
        root.add(backBtn).size(styleConfig.getCompactButtonWidth(), styleConfig.getCompactButtonHeight()).padBottom(30).align(Align.center);
    }

    // add one row to the tutorial (icon + description)
    private void addEntry(Table t, Texture tex, String desc) {
        Image img = new Image(tex);
        Label lbl = widgetFactory.createStandardLabel(desc); lbl.setWrap(true); lbl.setAlignment(Align.left);
        t.add(img).size(80).padRight(30);
        t.add(lbl).width(300).row();
    }

    // runs every frame
    @Override
    public void update(float dt) {
        ioManager.handleInput();
        for (int k : ioManager.getKeyboard().getKeysPressedThisFrame()) {
            if (k == Input.Keys.ESCAPE) {
                AbstractScene next = factory.create(SceneFactory.SceneType.MENU);
                next.setSceneManager(sceneManager);
                sceneManager.setScene(next);
                return;
            }
        }
        if (navigator != null) {
            navigator.update();
        }
    }

    // draw everything to screen
    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    // clean up textures/resources so we dont leak memory
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
