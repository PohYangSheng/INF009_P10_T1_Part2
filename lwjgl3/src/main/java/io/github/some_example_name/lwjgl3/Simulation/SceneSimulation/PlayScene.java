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

import io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager.CollisionPair;
import io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager.CollisionResolution;
import io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager.RectangleCollisionDectector;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.MovableEntity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;
import io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager.MovementManager;
import io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager.UserMovement;
import io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager.AbstractScene;

import io.github.some_example_name.lwjgl3.Simulation.CollisionSimulation.*;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.*;
import io.github.some_example_name.lwjgl3.Simulation.GameUtilities.*;
import io.github.some_example_name.lwjgl3.Simulation.MovementSimulation.DietitianAIMovement;
import io.github.some_example_name.lwjgl3.Simulation.MovementSimulation.EnemyAIMovement;

import java.util.ArrayList;

// main gameplay scene - this is where everything happens
public class PlayScene extends AbstractScene implements IFoodFactEventListener {

    private static final float BAD_DURATION    = 8f;
    private static final float SECRET_DURATION = 15f;
    private static final float BAD_SPEED_DELTA = -30f;

    private final SceneFactory     factory;
    private final MovementManager  movementManager = new MovementManager();
    private final GameProgressTracker progressTracker             = new GameProgressTracker();

    private MazeBuilder         mazeBuilder;
    private FoodSpawner         foodSpawner;
    private DietitianSpawner dietitianSpawner;
    private final FoodRepository  foodRepository = new FoodRepository();

    private Player  player;
    private float   playerStartX, playerStartY;
    private float   speed;
    private int     targetPoint;

    private Texture normalBg, badBg, secretBg, currentBg;
    private Texture normalWallTex, badWallTex, secretWallTex;
    private Texture floorTex, dietitianTex, overlayTex, enemyTex, playerTex;

    private Stage hudStage, overlayStage;
    private Skin  skin;
    private Label healthLabel, pointsLabel, foodLabel, livesLabel, stateLabel;

    private FoodFactPopUpManager foodFactPopUpManager;
    private boolean            showPopup = false;

    private float                      stateTimer = 0f;
    private GameProgressTracker.GameState prevState  = GameProgressTracker.GameState.NORMAL;

    private UserMovement<Player> userMovement;

    // constructor - takes in shared stuff from the factory
    public PlayScene(SceneFactory factory) {
        this.factory = factory;
    }

    // get the HUD stage (needed when resuming from pause)
    @Override
    public Stage getHudStage() {
        return hudStage;
    }

    // register the player entity before the scene starts
    @Override
    protected void registerEntities() {
        if (player != null) sceneEntities.add(player);
    }

    // set up everything - difficulty, textures, maze, HUD, collisions
    @Override
    public void create() {
        progressTracker.reset();
        resolveDifficulty();
        loadTextures();
        buildWorld();          
        registerEntities();    
        buildUI();
        factory.getAudio().play("music_loops/gameplay_theme.mp3", true);
    }

    // set speed and target based on difficulty
    private void resolveDifficulty() {
        switch (factory.getGameDifficulty().getSelectedDifficulty()) {
            case HARD:   speed = 280f; targetPoint = 500; break;
            case NORMAL: speed = 200f; targetPoint = 400; break;
            default:     speed = 150f; targetPoint = 300; break;
        }
    }

    // load all the textures we need
    private void loadTextures() {
        switch (factory.getGameDifficulty().getSelectedDifficulty()) {
            case HARD: normalBg = new Texture(Gdx.files.internal("scene_images/challenge_maze_bg.jpg")); break;
            default:   normalBg = new Texture(Gdx.files.internal("scene_images/standard_maze_bg.jpg")); break;
        }
        currentBg      = normalBg;
        badBg          = new Texture(Gdx.files.internal("scene_images/penalty_mode_bg.jpg"));
        secretBg       = new Texture(Gdx.files.internal("scene_images/bonus_mode_bg.jpg"));
        normalWallTex  = new Texture(Gdx.files.internal("sprite_assets/maze_wall_tile.png"));
        badWallTex     = new Texture(Gdx.files.internal("sprite_assets/danger_wall_tile.png"));
        secretWallTex  = new Texture(Gdx.files.internal("sprite_assets/bonus_wall_tile.png"));
        floorTex       = new Texture(Gdx.files.internal("sprite_assets/maze_floor_tile.png"));
        dietitianTex = new Texture(Gdx.files.internal("sprite_assets/health_mentor.png"));
        enemyTex     = new Texture(Gdx.files.internal("sprite_assets/rogue_chef.png"));
        playerTex    = new Texture(Gdx.files.internal(factory.getCharSelection().getSelectedTexturePath()));
        Pixmap px = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        px.setColor(Color.WHITE); px.fill();
        overlayTex = new Texture(px); px.dispose();
    }

    // build the maze and spawn player, enemies, food
    private void buildWorld() {
        float cellSize = Math.min(Gdx.graphics.getWidth()  / 15f,
            Gdx.graphics.getHeight() / 15f) * 0.85f;
        int sw = Gdx.graphics.getWidth(), sh = Gdx.graphics.getHeight();

        mazeBuilder         = new MazeBuilder(cellSize);
        foodSpawner         = new FoodSpawner(cellSize, mazeBuilder, foodRepository);
        dietitianSpawner = new DietitianSpawner(cellSize, mazeBuilder, foodRepository);

        mazeBuilder.buildFloor(floorTex, sw, sh);
        mazeBuilder.buildMaze(normalWallTex, sw, sh);

        for (Floor f : mazeBuilder.getFloors()) entityManager.addEntity(f);
        for (Wall  w : mazeBuilder.getWalls()) {
            entityManager.addEntity(w);
            collisionManager.registerStaticCollidable(w);
        }

        Vector2 sp = mazeBuilder.getScreenPosition(1, 1);
        float playerRenderSize = cellSize * 0.8f;
        float playerOffset = cellSize * 0.05f;
        playerStartX = mazeBuilder.clampX(sp.x + playerOffset, playerRenderSize);
        playerStartY = mazeBuilder.clampY(sp.y + playerOffset, playerRenderSize);
        player = new Player("Player", playerStartX, playerStartY, playerTex, speed, playerRenderSize);
        entityManager.addEntity(player);
        collisionManager.registerCollidable(player);

        userMovement = new UserMovement<>(player, ioManager);
        movementManager.addMovement(userMovement);

        int[][] spawnPoints = MazeBuilder.getEnemySpawnPoints();
        for (int i = 0; i < spawnPoints.length; i++) {
            spawnEnemy("Chef" + (i + 1),
                mazeBuilder.getScreenPosition(spawnPoints[i][0], spawnPoints[i][1]),
                cellSize);
        }

        for (Food f : foodSpawner.spawnFood(5)) {
            entityManager.addEntity(f);
            collisionManager.registerCollidable(f);
        }

        collisionManager.setDetector(new RectangleCollisionDectector());
        collisionManager.setResolution(buildGameResolution());
    }

    // spawn one enemy at a position
    private void spawnEnemy(String name, Vector2 pos, float cellSize) {
        float offset = cellSize * 0.05f;
        float renderSize = cellSize * 0.8f;
        float x = mazeBuilder.clampX(pos.x + offset, renderSize);
        float y = mazeBuilder.clampY(pos.y + offset, renderSize);

        Enemy enemy = new Enemy(name, x, y, enemyTex, speed, renderSize);
        enemy.setTarget(player);
        entityManager.addEntity(enemy);
        collisionManager.registerCollidable(enemy);

        EnemyAIMovement aiMovement = new EnemyAIMovement(enemy);
        movementManager.addMovement(aiMovement);
    }

    // set up collision resolution - routes pairs to the right handler
    private CollisionResolution buildGameResolution() {
        return new CollisionResolution() {
            // handle what happens after the collision
            @Override
            public void resolve(CollisionPair pair) {
                iCollidable a = pair.getEntityA(), b = pair.getEntityB();
                dispatch(a, b);
                dispatch(b, a);
            }

            // figure out what type of collision and call the right handler
            private void dispatch(iCollidable a, iCollidable b) {
                if      (a instanceof Player       && b instanceof Wall)
                    PlayerWallCollision.resolve((Player)a, (Wall)b);
                else if (a instanceof Player       && b instanceof Enemy)
                    PlayerEnemyCollision.resolve((Player)a, (Enemy)b, progressTracker,
                        factory.getSoundManager());
                else if (a instanceof Player       && b instanceof Food)
                    PlayerFoodCollision.resolve((Player)a, (Food)b, progressTracker,
                        foodFactPopUpManager, factory.getSoundManager());
                else if (a instanceof Player       && b instanceof Dietitian)
                    PlayerDietitianCollision.resolve((Player)a, (Dietitian)b,
                        foodFactPopUpManager, factory.getSoundManager());
                else if (a instanceof Enemy        && b instanceof Wall)
                    EnemyWallCollision.resolve((Enemy)a, (Wall)b);
                else if (a instanceof Enemy        && b instanceof Enemy && a != b)
                    EnemyEnemyCollision.resolve((Enemy)a, (Enemy)b);
                else if (a instanceof Dietitian && b instanceof Wall)
                    DietitianWallCollision.resolve((Dietitian)a, (Wall)b);
            }
        };
    }

    // create the HUD labels and popup manager
    private void buildUI() {
        hudStage     = new Stage();
        overlayStage = new Stage();
        skin         = new Skin(Gdx.files.internal("kenney_skin/kenney-ui.json"));

        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(hudStage);
        Gdx.input.setInputProcessor(mux);

        foodFactPopUpManager = new FoodFactPopUpManager(overlayStage, skin);
        foodFactPopUpManager.setPopupListener(this);

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

    // make a HUD label
    private Label lbl(String text, Color color) {
        Label label = new Label(text, skin);
        label.setColor(color);
        return label;
    }

    // main game loop - movement, collisions, spawning, state checks
    @Override
    public void update(float dt) {
        if (showPopup) return;

        ioManager.handleInput();

        for (int key : ioManager.getKeyboard().getKeysPressedThisFrame()) {
            if (key == Input.Keys.P || key == Input.Keys.ESCAPE) {
                factory.getAudio().pause();
                AbstractScene p = factory.create(SceneFactory.SceneType.PAUSE);
                p.setSceneManager(sceneManager);
                sceneManager.pushScene(p);
                return;
            }
        }

        movementManager.update(dt);

        preRemoveExpiredCollidables();
        entityManager.updateEntities(dt);

        for (Food f : foodSpawner.update(dt)) {
            entityManager.addEntity(f);
            collisionManager.registerCollidable(f);
        }

        boolean nutExists = !entityManager.getEntitiesOfType(Dietitian.class).isEmpty();
        if (dietitianSpawner.update(dt, nutExists)) {
            Dietitian n = dietitianSpawner.spawnDietitian(
                dietitianTex, speed * 0.6f);
            if (n != null) {
                entityManager.addEntity(n);
                collisionManager.registerCollidable(n);

                DietitianAIMovement aiMovement = new DietitianAIMovement(n);
                movementManager.addMovement(aiMovement);
            }
        }

        collisionManager.update(dt);
        keepMazeEntitiesInsideBounds();
        updateStateTimers(dt);
        checkSecretModeTrigger();
        checkIfGameOver();
    }

    // unregister dead entities before collision check
    private void preRemoveExpiredCollidables() {
        for (Food f         : new ArrayList<>(entityManager.getEntitiesOfType(Food.class)))
            if (f.isPendingRemoval())  collisionManager.unregisterCollidable(f);
        for (Enemy e        : new ArrayList<>(entityManager.getEntitiesOfType(Enemy.class)))
            if (e.isPendingRemoval())  collisionManager.unregisterCollidable(e);
        for (Dietitian n : new ArrayList<>(entityManager.getEntitiesOfType(Dietitian.class)))
            if (n.isPendingRemoval())  collisionManager.unregisterCollidable(n);
    }

    // keep everything inside the maze bounds
    private void keepMazeEntitiesInsideBounds() {
        keepInsideMaze(player);

        for (Enemy enemy : entityManager.getEntitiesOfType(Enemy.class)) {
            keepInsideMaze(enemy);
        }

        for (Dietitian dietitian : entityManager.getEntitiesOfType(Dietitian.class)) {
            keepInsideMaze(dietitian);
        }
    }

    // clamp one entity inside maze area
    private void keepInsideMaze(MovableEntity entity) {
        float width = entity.getBounds().width;
        float height = entity.getBounds().height;
        float clampedX = mazeBuilder.clampX(entity.getX(), width);
        float clampedY = mazeBuilder.clampY(entity.getY(), height);
        if (clampedX != entity.getX() || clampedY != entity.getY()) {
            entity.setPosition(clampedX, clampedY);
        }
    }

    // render bg, entities, HUD, and popups
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

        foodFactPopUpManager.render(batch);
    }

    // draw the red/gold overlay for bad/secret mode
    private void drawStateOverlay(SpriteBatch batch) {
        GameProgressTracker.GameState s = progressTracker.getCurrentState();
        if      (s == GameProgressTracker.GameState.BAD) {
            batch.setColor(0.8f, 0f, 0f, 0.3f);
            batch.draw(overlayTex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(Color.WHITE);
        } else if (s == GameProgressTracker.GameState.SECRET) {
            batch.setColor(1f, 0.85f, 0f, 0.2f);
            batch.draw(overlayTex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(Color.WHITE);
        }
    }

    // update HUD text with current values
    private void updateHUD() {
        healthLabel.setText("Health: " + player.getCurrentHealth() + "/" + player.getHealthCap());
        pointsLabel.setText("Points: " + player.getPoints() + "/" + targetPoint);
        foodLabel.setText("Food: "   + foodSpawner.getFoodCount() + "/10");
        livesLabel.setText("Lives: " + progressTracker.getLives());
        GameProgressTracker.GameState s = progressTracker.getCurrentState();
        if (s == GameProgressTracker.GameState.SECRET) {
            stateLabel.setText("SUPER MODE! " + (int) stateTimer + "s");
            stateLabel.setColor(Color.YELLOW);
        } else {
            stateLabel.setText("");
        }
    }

    // check if player hit target points for secret mode
    private void checkSecretModeTrigger() {
        if (progressTracker.getCurrentState() == GameProgressTracker.GameState.NORMAL
            && player.getPoints() >= targetPoint)
            progressTracker.setCurrentState(GameProgressTracker.GameState.SECRET);
    }

    // count down state timers and transition when done
    private void updateStateTimers(float dt) {
        GameProgressTracker.GameState s = progressTracker.getCurrentState();
        if (s != prevState) {
            onStateEnter(s);
            prevState = s;
        }
        if (s == GameProgressTracker.GameState.BAD
            || s == GameProgressTracker.GameState.SECRET) {
            stateTimer -= dt;
            if (stateTimer <= 0) {
                onStateExit(s);
                if (s == GameProgressTracker.GameState.SECRET) {
                    factory.setGameResults(player.getPoints(), true);
                    AbstractScene r = factory.create(SceneFactory.SceneType.SCORE);
                    r.setSceneManager(sceneManager);
                    sceneManager.setScene(r);
                } else {
                    progressTracker.setCurrentState(GameProgressTracker.GameState.NORMAL);
                }
            }
        }
    }

    // handle entering a new game state
    private void onStateEnter(GameProgressTracker.GameState s) {
        if (s == GameProgressTracker.GameState.BAD) {
            stateTimer = BAD_DURATION;
            player.modifySpeed(BAD_SPEED_DELTA);
            currentBg = badBg; swapWalls(badWallTex);
        } else if (s == GameProgressTracker.GameState.SECRET) {
            stateTimer = SECRET_DURATION;
            player.modifySpeed(60f);
            currentBg = secretBg; swapWalls(secretWallTex);
        }
    }

    // reverts or finalises any temporary effects when the current gameplay state ends
    private void onStateExit(GameProgressTracker.GameState s) {
        if (s == GameProgressTracker.GameState.BAD) {
            player.modifySpeed(-BAD_SPEED_DELTA);
            currentBg = normalBg; swapWalls(normalWallTex);
        } else if (s == GameProgressTracker.GameState.SECRET) {
            player.modifySpeed(-60f);
        }
    }

    // swaps wall textures across the maze to visually reflect the current gameplay state
    private void swapWalls(Texture t) {
        for (Wall w : entityManager.getEntitiesOfType(Wall.class)) w.swapTexture(t);
    }

    // checks whether the player has won or lost and transitions to the score scene when the run ends
    private void checkIfGameOver() {
        if (showPopup || player.getCurrentHealth() > 0) return;
        if (progressTracker.getLives() <= 0) {
            factory.setGameResults(player.getPoints(), false);
            AbstractScene r = factory.create(SceneFactory.SceneType.SCORE);
            r.setSceneManager(sceneManager);
            sceneManager.setScene(r);
        } else {
            player.setCurrentHealth(player.getHealthCap());
            player.setPosition(playerStartX, playerStartY);
        }
    }

    // callback invoked when the food fact popup becomes visible
    @Override
    public void onPopupShown() {
        showPopup = true;
        Gdx.input.setInputProcessor(overlayStage);
    }

    // callback invoked after the food fact popup is dismissed so normal gameplay flow can continue
    @Override
    public void onPopupDismissed() {
        showPopup = false;
        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(hudStage);
        Gdx.input.setInputProcessor(mux);
    }

    // clean up textures/resources so we dont leak memory
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
        if (dietitianTex != null) dietitianTex.dispose();
        if (overlayTex      != null) overlayTex.dispose();
        if (enemyTex        != null) enemyTex.dispose();
        if (playerTex       != null) playerTex.dispose();
        if (foodSpawner          != null) foodSpawner.dispose();
        if (foodFactPopUpManager != null) foodFactPopUpManager.dispose();
    }
}
