package io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager.CollisionManager;
import io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager.CollisionPair;
import io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager.CollisionResolution;
import io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager.RectangleCollisionDectector;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.Entity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.EntityManager;
import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.IOManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base for all game scenes.
 *
 * Design Pattern: Template Method – defines the invariant lifecycle skeleton
 *   (create → update → render → dispose) and delegates scene-specific steps
 *   to concrete subclasses via abstract methods. This ensures every scene
 *   follows the same structural contract without duplicating boilerplate.
 *
 * OOP:
 *   Abstraction  – abstract methods force subclasses to provide behaviour.
 *   Inheritance  – all scenes extend this class, inheriting engine managers.
 *   Polymorphism – SceneManager calls update/render on AbstractScene references,
 *                  dispatching to the correct concrete scene at runtime.
 *
 * SOLID:
 *   OCP – new scenes extend this without changing engine code.
 *   DIP – depends on engine manager abstractions (EntityManager,
 *         CollisionManager), never on game-specific types.
 *   LSP – any AbstractScene subclass can replace another in SceneManager.
 *
 * Engine managers are created here and accessible to all subclasses.
 * No game-specific code is present.
 */
public abstract class AbstractScene implements Screen {

    protected final IOManager        ioManager;
    protected final EntityManager    entityManager;
    protected final CollisionManager collisionManager;

    /**
     * SceneManager reference – moved here so callers never need to downcast
     * just to call setSceneManager(). Every scene uses this field directly.
     *
     * SOLID DIP fix: GameMaster and all scenes depend on AbstractScene,
     * not on concrete scene types. No downcasting required anywhere.
     */
    protected SceneManager sceneManager;

    public void setSceneManager(SceneManager sm) { this.sceneManager = sm; }

    /**
     * Returns the HUD Stage if this scene has one, null otherwise.
     * Overridden by PlayScene so PauseScene can restore the input processor
     * without casting to PlayScene.
     *
     * OOP: Polymorphism – PauseScene calls getCurrentScene().getHudStage()
     * on an AbstractScene reference; PlayScene returns its Stage,
     * all other scenes return null. No downcast needed.
     */
    public Stage getHudStage() { return null; }

    /**
     * The array of entities this scene owns.
     *
     * Subclasses populate this list by overriding registerEntities() and
     * calling sceneEntities.add(...). AbstractScene then automatically adds
     * every entry to EntityManager at scene start.
     *
     * OOP:
     *   Abstraction  – the engine defines the contract (a list of entities
     *                  to instantiate); subclasses supply the contents.
     *   Polymorphism – every item is stored as Entity so the engine loop
     *                  works for any concrete entity subclass.
     *
     * SOLID:
     *   OCP – new entity types added by subclassing Entity and appending to
     *         this list; no engine code changes needed.
     *   DIP – AbstractScene depends on Entity (abstract), not game types.
     */
    protected final List<Entity> sceneEntities = new ArrayList<>();

    protected AbstractScene() {
        this.ioManager        = new IOManager();
        this.entityManager    = new EntityManager();
        this.collisionManager = new CollisionManager(
            new RectangleCollisionDectector(),
            new CollisionResolution() {
                @Override public void resolve(CollisionPair pair) {
                    pair.getEntityA().onCollision(pair.getEntityB());
                    pair.getEntityB().onCollision(pair.getEntityA());
                }
            });
    }

    // ── Template Method hooks ──────────────────────────────────────────────

    /**
     * Override to populate sceneEntities with the entities this scene owns.
     * Called automatically by create() before the scene-specific setup runs.
     *
     * Example:
     * <pre>
     *   {@literal @}Override
     *   protected void registerEntities() {
     *       sceneEntities.add(player);
     *       sceneEntities.add(wall1);
     *   }
     * </pre>
     *
     * Default implementation is empty – scenes that manage entities through
     * EntityManager directly (e.g. PlayScene) are not forced to use this.
     */
    protected void registerEntities() {}

    /**
     * Called once when the scene becomes active.
     *
     * The default implementation calls registerEntities() first, then
     * adds every entry in sceneEntities to EntityManager automatically.
     * Subclasses should call super.create() if they want this behaviour,
     * or override completely for full control.
     */
    public void create() {
        registerEntities();
        for (Entity e : sceneEntities)
            entityManager.addEntity(e);
    }

    /** Called every frame – subclasses advance game logic. */
    public abstract void update(float dt);

    /** Called every frame – subclasses draw the scene. */
    public abstract void render(SpriteBatch batch);

    // ── LibGDX Screen boilerplate ──────────────────────────────────────────

    @Override public void render(float delta) { /* driven by SceneManager */ }
    @Override public void show()              {}
    @Override public void resize(int w, int h){}
    @Override public void pause()             {}
    @Override public void resume()            {}
    @Override public void hide()              {}

    @Override
    public void dispose() {
        if (entityManager    != null) entityManager.dispose();
        if (collisionManager != null) collisionManager.dispose();
    }
}
