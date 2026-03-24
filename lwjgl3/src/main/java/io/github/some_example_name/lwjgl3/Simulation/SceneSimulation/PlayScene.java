package io.github.some_example_name.lwjgl3.Simulation.SceneSimulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager.BoundaryCollisionDetector;
import io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager.CollisionPair;
import io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager.CollisionResolution;
import io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager.RectangleCollisionDectector;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;
import io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager.MovementManager;
import io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager.UserMovement;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.SceneManager;

import io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation.*;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.*;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Main gameplay scene – the heart of Munch Maze.
 *
 * Design Patterns demonstrated:
 *   1. Template Method – create/update/render lifecycle from AbstractScene.
 *   2. Factory         – SceneFactory.create(SceneType) makes all scenes.
 *   3. Strategy        – CollisionResolution subclass dispatches per pair type.
 *                        UserMovement<Player> is another Strategy instance.
 *   4. Observer        – IDialogEventListener / DialogPopUpManager decouple
 *                        the popup from the scene that reacts to it.
 *   5. Command         – AudioSimulation / IOSimulation action lists (Part 1).
 *
 * OOP:
 *   Inheritance  – extends AbstractScene, inheriting engine managers.
 *   Polymorphism – implements IDialogEventListener; both are used via their
 *                  abstract types throughout.
 *   Abstraction  – engine managers are used through their interfaces.
 *   Generics     – UserMovement<Player>, BoundaryCollisionDetector<Enemy>, etc.
 *
 * SOLID:
 *   SRP  – each private method has one reason to change.
 *   OCP  – new collision types added in buildGameResolution() without
 *          touching CollisionManager.
 *   DIP  – depends on AbstractScene, CollisionResolution (abstract),
 *          iCollidable – not concrete engine classes.
 */
public class PlayScene extends AbstractScene implements IDialogEventListener {

    // ── Constants ──────────────────────────────────────────────────────────
    private static final float BAD_DURATION    = 8f;
    private static final float SECRET_DURATION = 15f;
    private static final float BAD_SPEED_DELTA = -30f;

    // ── Session state ──────────────────────────────────────────────────────
    private final SceneFactory     factory;
    private final MovementManager  movementManager = new MovementManager();
    private final GameStateManager gsm             = new GameStateManager();

    // ── World generators ───────────────────────────────────────────────────
    private MazeGenerator         mazeGenerator;
    private FoodGenerator         foodGenerator;
    private NutritionistGenerator nutritionistGenerator;
    private final FoodDictionary  foodDictionary = new FoodDictionary();

    // ── Key entities ───────────────────────────────────────────────────────
    private Player  player;
    private float   playerStartX, playerStartY;
    private float   speed;
    private int     targetPoint;

    // ── Boundary detectors (engine generics) ──────────────────────────────
    private BoundaryCollisionDetector<Player>             playerBoundary;
    private final List<BoundaryCollisionDetector<Enemy>>  enemyBoundaries = new ArrayList<>();
    private BoundaryCollisionDetector<Nutritionist>       nutritionistBoundary;

    // ── Textures ───────────────────────────────────────────────────────────
    private Texture normalBg, badBg, secretBg, currentBg;
    private Texture normalWallTex, badWallTex, secretWallTex;
    private Texture floorTex, nutritionistTex, overlayTex;

    // ── UI ─────────────────────────────────────────────────────────────────
    private Stage hudStage, overlayStage;
    private Skin  skin;
    private Label healthLabel, pointsLabel, foodLabel, livesLabel, stateLabel;

    // ── Observer state ─────────────────────────────────────────────────────
    private DialogPopUpManager dialogManager;
    private boolean            showPopup = false;

    // ── State machine ──────────────────────────────────────────────────────
    private float                      stateTimer = 0f;
    private GameStateManager.GameState prevState  = GameStateManager.GameState.NORMAL;

    // ── Movement handles ───────────────────────────────────────────────────
    private UserMovement<Player> userMovement;

    // ── Construction ───────────────────────────────────────────────────────
    public PlayScene(SceneFactory factory) { this.factory = factory; }

    @Override
    public Stage getHudStage() { return hudStage; }

    // ── Template Method: registerEntities ─────────────────────────────────

    /**
     * Registers the Player entity into the engine's sceneEntities list.
     *
     * AbstractScene.create() calls this automatically, then adds every entry
     * in sceneEntities to EntityManager.
     *
     * OOP Template Method: the engine defines WHEN registerEntities() is called;
     * PlayScene defines WHAT entities are registered.
     *
     * Wall, Floor, Food and Enemy entities are added separately in buildWorld()
     * because they are generated procedurally after the maze is built.
     */
    @Override
    protected void registerEntities() {
        if (player != null) sceneEntities.add(player);
    }

    // ── Template Method: create ────────────────────────────────────────────

    @Override
    public void create() {
        gsm.reset();
        resolveDifficulty();
        loadTextures();
        buildWorld();          // creates player, walls, floors, enemies, food
        registerEntities();    // adds player to sceneEntities (engine contract)
        buildUI();
        factory.getAudio().play("background_music/play.mp3", true);
    }

    private void resolveDifficulty() {
        switch (factory.getGameDifficulty().getDifficulty()) {
            case HARD:   speed = 280f; targetPoint = 500; break;
            case NORMAL: speed = 200f; targetPoint = 400; break;
            default:     speed = 150f; targetPoint = 300; break;
        }
    }

    private void loadTextures() {
        switch (factory.getGameDifficulty().getDifficulty()) {
            case HARD: normalBg = new Texture(Gdx.files.internal("background_images/hardPlay.jpg")); break;
            default:   normalBg = new Texture(Gdx.files.internal("background_images/easyplay.jpg")); break;
        }
        currentBg      = normalBg;
        badBg          = new Texture(Gdx.files.internal("background_images/badscenebackground.jpg"));
        secretBg       = new Texture(Gdx.files.internal("background_images/secretbackground.jpg"));
        normalWallTex  = new Texture(Gdx.files.internal("entities_images/wall.png"));
        badWallTex     = new Texture(Gdx.files.internal("entities_images/red.png"));
        secretWallTex  = new Texture(Gdx.files.internal("entities_images/green.png"));
        floorTex       = new Texture(Gdx.files.internal("entities_images/floor.png"));
        nutritionistTex= new Texture(Gdx.files.internal("entities_images/nutritionist.png"));
        Pixmap px = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        px.setColor(Color.WHITE); px.fill();
        overlayTex = new Texture(px); px.dispose();
    }

    private void buildWorld() {
        float cellSize = Math.min(Gdx.graphics.getWidth()  / 15f,
            Gdx.graphics.getHeight() / 15f) * 0.85f;
        int sw = Gdx.graphics.getWidth(), sh = Gdx.graphics.getHeight();

        mazeGenerator         = new MazeGenerator(cellSize);
        foodGenerator         = new FoodGenerator(cellSize, mazeGenerator, foodDictionary);
        nutritionistGenerator = new NutritionistGenerator(cellSize, mazeGenerator, foodDictionary);

        mazeGenerator.generateFloor(floorTex, sw, sh);
        mazeGenerator.generateMaze(normalWallTex, sw, sh);

        for (Floor f : mazeGenerator.getFloors()) entityManager.addEntity(f);
        for (Wall  w : mazeGenerator.getWalls()) {
            entityManager.addEntity(w);
            collisionManager.registerStaticCollidable(w);
        }

        // Player
        Vector2 sp = mazeGenerator.gridToScreenTopLeft(1, 1);
        playerStartX = sp.x + cellSize * 0.1f;
        playerStartY = sp.y + cellSize * 0.1f;
        Texture playerTex = new Texture(Gdx.files.internal(
            factory.getCharSelection().getSelectedTexturePath()));
        player = new Player("Player", playerStartX, playerStartY, playerTex, speed, cellSize * 0.8f);
        entityManager.addEntity(player);
        collisionManager.registerCollidable(player);

        // UserMovement – Strategy pattern (engine generic)
        userMovement = new UserMovement<>(player, ioManager);
        movementManager.addMovement(userMovement);

        // BoundaryCollisionDetector – engine generic, typed to Player
        playerBoundary = new BoundaryCollisionDetector<>(player);

        // Data-driven enemy spawning
        int[][] spawnPoints = MazeGenerator.getEnemySpawnPoints();
        for (int i = 0; i < spawnPoints.length; i++) {
            spawnEnemy("Chef" + (i + 1),
                mazeGenerator.gridToScreenTopLeft(spawnPoints[i][0], spawnPoints[i][1]),
                cellSize);
        }

        for (Food f : foodGenerator.generateFood(5)) {
            entityManager.addEntity(f);
            collisionManager.registerCollidable(f);
        }

        collisionManager.setDetector(new RectangleCollisionDectector());
        collisionManager.setResolution(buildGameResolution());
    }

    private void spawnEnemy(String name, Vector2 pos, float cellSize) {
        Texture tex = new Texture(Gdx.files.internal("entities_images/angry_chef.png"));
        float off = cellSize * 0.1f;
        Enemy enemy = new Enemy(name, pos.x+off, pos.y+off, tex, speed, cellSize*0.8f);
        enemy.setTarget(player);
        entityManager.addEntity(enemy);
        collisionManager.registerCollidable(enemy);
        enemyBoundaries.add(new BoundaryCollisionDetector<>(enemy));
    }

    /**
     * Builds the game-specific CollisionResolution subclass.
     *
     * OOP Polymorphism: overrides the abstract resolve() method.
     * OOP Abstraction: CollisionManager only knows CollisionResolution (abstract).
     * SOLID OCP: new collision pair types added here without touching the engine.
     */
    private CollisionResolution buildGameResolution() {
        return new CollisionResolution() {
            @Override
            public void resolve(CollisionPair pair) {
                iCollidable a = pair.getEntityA(), b = pair.getEntityB();
                dispatch(a, b);
                dispatch(b, a);
            }

            private void dispatch(iCollidable a, iCollidable b) {
                if      (a instanceof Player       && b instanceof Wall)
                    PlayerWallCollision.resolve((Player)a, (Wall)b);
                else if (a instanceof Player       && b instanceof Enemy)
                    PlayerEnemyCollision.resolve((Player)a, (Enemy)b, gsm,
                        factory.getSoundManager());
                else if (a instanceof Player       && b instanceof Food)
                    PlayerFoodCollision.resolve((Player)a, (Food)b, gsm,
                        dialogManager, factory.getSoundManager());
                else if (a instanceof Player       && b instanceof Nutritionist)
                    PlayerNutritionistCollision.resolve((Player)a, (Nutritionist)b,
                        dialogManager, factory.getSoundManager());
                else if (a instanceof Enemy        && b instanceof Wall)
                    EnemyWallCollision.resolve((Enemy)a, (Wall)b);
                else if (a instanceof Enemy        && b instanceof Enemy && a != b)
                    EnemyEnemyCollision.resolve((Enemy)a, (Enemy)b);
                else if (a instanceof Nutritionist && b instanceof Wall)
                    NutritionistWallCollision.resolve((Nutritionist)a, (Wall)b);
            }
        };
    }

    private void buildUI() {
        hudStage     = new Stage();
        overlayStage = new Stage();
        skin         = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));

        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(hudStage);
        Gdx.input.setInputProcessor(mux);

        dialogManager = new DialogPopUpManager(overlayStage, skin);
        dialogManager.setDialogEventListener(this);

        Table hud = new Table(); hud.setFillParent(true); hud.bottom().padBottom(10);
        healthLabel = lbl("Health: 100/100", Color.WHITE);
        pointsLabel = lbl("Points: 100/" + targetPoint, Color.GOLD);
        foodLabel   = lbl("Food: 0/10", Color.ORANGE);
        livesLabel  = lbl("Lives: 3",  Color.YELLOW);
        hud.add(healthLabel).left().pad(8);
        hud.add(pointsLabel).expandX().center().pad(8);
        hud.add(foodLabel).right().pad(8);
        hud.add(livesLabel).right().pad(8);
        hudStage.addActor(hud);

        stateLabel = lbl("", Color.WHITE);
        Table st = new Table(); st.setFillParent(true); st.top().center().padTop(15);
        st.add(stateLabel);
        hudStage.addActor(st);
    }

    private Label lbl(String t, Color c) {
        Label l = new Label(t, skin); l.setColor(c); return l;
    }

    // ── Template Method: update ────────────────────────────────────────────

    @Override
    public void update(float dt) {
        if (showPopup) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.P) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            factory.getAudio().pause();
            AbstractScene p = factory.create(SceneFactory.SceneType.PAUSE);
            p.setSceneManager(sceneManager);
            sceneManager.pushScene(p);
            return;
        }

        movementManager.update(dt);

        playerBoundary.update();
        for (BoundaryCollisionDetector<Enemy> bd : enemyBoundaries) bd.update();
        if (nutritionistBoundary != null) nutritionistBoundary.update();

        preRemoveExpiredCollidables();
        entityManager.updateEntities(dt);

        for (Food f : foodGenerator.update(dt)) {
            entityManager.addEntity(f);
            collisionManager.registerCollidable(f);
        }

        boolean nutExists = !entityManager.getEntitiesOfType(Nutritionist.class).isEmpty();
        if (nutritionistGenerator.update(dt, nutExists)) {
            Nutritionist n = nutritionistGenerator.generateNutritionist(
                nutritionistTex, speed * 0.6f);
            if (n != null) {
                entityManager.addEntity(n);
                collisionManager.registerCollidable(n);
                nutritionistBoundary = new BoundaryCollisionDetector<>(n);
            }
        }

        collisionManager.update(dt);
        updateStateTimers(dt);
        checkSecretModeTrigger();
        checkGameEndCondition();
    }

    private void preRemoveExpiredCollidables() {
        for (Food f         : new ArrayList<>(entityManager.getEntitiesOfType(Food.class)))
            if (f.shouldBeRemoved())  collisionManager.unregisterCollidable(f);
        for (Enemy e        : new ArrayList<>(entityManager.getEntitiesOfType(Enemy.class)))
            if (e.shouldBeRemoved())  collisionManager.unregisterCollidable(e);
        for (Nutritionist n : new ArrayList<>(entityManager.getEntitiesOfType(Nutritionist.class)))
            if (n.shouldBeRemoved())  collisionManager.unregisterCollidable(n);
    }

    // ── Template Method: render ────────────────────────────────────────────

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(currentBg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        drawStateOverlay(batch);
        entityManager.render(batch);
        batch.end();

        updateHUD();
        hudStage.act(Gdx.graphics.getDeltaTime());
        hudStage.draw();

        dialogManager.render(batch);
    }

    private void drawStateOverlay(SpriteBatch batch) {
        GameStateManager.GameState s = gsm.getCurrentState();
        if      (s == GameStateManager.GameState.BAD) {
            batch.setColor(0.8f, 0f, 0f, 0.3f);
            batch.draw(overlayTex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(Color.WHITE);
        } else if (s == GameStateManager.GameState.SECRET) {
            batch.setColor(1f, 0.85f, 0f, 0.2f);
            batch.draw(overlayTex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(Color.WHITE);
        }
    }

    private void updateHUD() {
        healthLabel.setText("Health: " + player.getHealth() + "/" + player.getMaxHealth());
        pointsLabel.setText("Points: " + player.getPoints() + "/" + targetPoint);
        foodLabel.setText("Food: "   + foodGenerator.getFoodListSize() + "/10");
        livesLabel.setText("Lives: " + gsm.getLives());
        GameStateManager.GameState s = gsm.getCurrentState();
        if (s == GameStateManager.GameState.SECRET) {
            stateLabel.setText("SUPER MODE! " + (int) stateTimer + "s");
            stateLabel.setColor(Color.YELLOW);
        } else {
            stateLabel.setText("");
        }
    }

    // ── State machine ──────────────────────────────────────────────────────

    private void checkSecretModeTrigger() {
        if (gsm.getCurrentState() == GameStateManager.GameState.NORMAL
            && player.getPoints() >= targetPoint)
            gsm.setCurrentState(GameStateManager.GameState.SECRET);
    }

    private void updateStateTimers(float dt) {
        GameStateManager.GameState s = gsm.getCurrentState();
        if (s != prevState) { onStateEnter(s); prevState = s; }
        if (s == GameStateManager.GameState.BAD
            || s == GameStateManager.GameState.SECRET) {
            stateTimer -= dt;
            if (stateTimer <= 0) {
                onStateExit(s);
                if (s == GameStateManager.GameState.SECRET) {
                    factory.setGameResults(player.getPoints(), true);
                    AbstractScene r = factory.create(SceneFactory.SceneType.RESULTS);
                    r.setSceneManager(sceneManager);
                    sceneManager.setScene(r);
                } else {
                    gsm.setCurrentState(GameStateManager.GameState.NORMAL);
                }
            }
        }
    }

    private void onStateEnter(GameStateManager.GameState s) {
        if (s == GameStateManager.GameState.BAD) {
            stateTimer = BAD_DURATION;
            player.adjustSpeed(BAD_SPEED_DELTA);
            currentBg = badBg; swapWalls(badWallTex);
        } else if (s == GameStateManager.GameState.SECRET) {
            stateTimer = SECRET_DURATION;
            player.adjustSpeed(60f);
            currentBg = secretBg; swapWalls(secretWallTex);
        }
    }

    private void onStateExit(GameStateManager.GameState s) {
        if (s == GameStateManager.GameState.BAD) {
            player.adjustSpeed(-BAD_SPEED_DELTA);
            currentBg = normalBg; swapWalls(normalWallTex);
        } else if (s == GameStateManager.GameState.SECRET) {
            player.adjustSpeed(-60f);
        }
    }

    private void swapWalls(Texture t) {
        for (Wall w : entityManager.getEntitiesOfType(Wall.class)) w.swapTexture(t);
    }

    // ── Game end / respawn ─────────────────────────────────────────────────

    private void checkGameEndCondition() {
        if (showPopup || player.getHealth() > 0) return;
        if (gsm.getLives() <= 0) {
            factory.setGameResults(player.getPoints(), false);
            AbstractScene r = factory.create(SceneFactory.SceneType.RESULTS);
            r.setSceneManager(sceneManager);
            sceneManager.setScene(r);
        } else {
            player.setHealth(player.getMaxHealth());
            player.setPosition(playerStartX, playerStartY);
        }
    }

    // ── Observer: IDialogEventListener ────────────────────────────────────

    @Override
    public void onDialogOpened() {
        showPopup = true;
        Gdx.input.setInputProcessor(overlayStage);
    }

    @Override
    public void onDialogClosed() {
        showPopup = false;
        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(hudStage);
        Gdx.input.setInputProcessor(mux);
    }

    // ── Dispose ────────────────────────────────────────────────────────────

    @Override
    public void dispose() {
        movementManager.removeMovement(userMovement);
        super.dispose();
        if (hudStage        != null) hudStage.dispose();
        if (overlayStage    != null) overlayStage.dispose();
        if (skin            != null) skin.dispose();
        if (normalBg        != null) normalBg.dispose();
        if (badBg           != null) badBg.dispose();
        if (secretBg        != null) secretBg.dispose();
        if (normalWallTex   != null) normalWallTex.dispose();
        if (badWallTex      != null) badWallTex.dispose();
        if (secretWallTex   != null) secretWallTex.dispose();
        if (floorTex        != null) floorTex.dispose();
        if (nutritionistTex != null) nutritionistTex.dispose();
        if (overlayTex      != null) overlayTex.dispose();
        if (foodGenerator   != null) foodGenerator.dispose();
    }
}
