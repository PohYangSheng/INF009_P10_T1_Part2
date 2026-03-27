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

// base class for all scenes - has the standard lifecycle methods
public abstract class AbstractScene implements Screen {

    protected final IOManager        ioManager;
    protected final EntityManager    entityManager;
    protected final CollisionManager collisionManager;

    protected SceneManager sceneManager;

    // setter for scene manager
    public void setSceneManager(SceneManager sm) {
        this.sceneManager = sm;
    }

    // getter for hud stage
    public Stage getHudStage() {
        return null;
    }

    protected final List<Entity> sceneEntities = new ArrayList<>();

    // constructor
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

    // override this to register entities during scene setup
    protected void registerEntities() {}

    // setup method - registers entities
    public void create() {
        registerEntities();
        for (Entity e : sceneEntities)
            entityManager.addEntity(e);
    }

    public abstract void update(float dt);

    public abstract void render(SpriteBatch batch);

    @Override public void render(float delta) {
         
    }
    @Override public void show()              {}
    @Override public void resize(int w, int h){}
    @Override public void pause()             {}
    @Override public void resume()            {}
    @Override public void hide()              {}

    // clean up textures/resources so we dont leak memory
    @Override
    public void dispose() {
        if (entityManager    != null) entityManager.dispose();
        if (collisionManager != null) collisionManager.dispose();
    }
}
