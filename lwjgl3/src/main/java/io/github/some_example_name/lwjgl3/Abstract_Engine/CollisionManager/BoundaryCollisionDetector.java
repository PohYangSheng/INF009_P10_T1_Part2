package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iMovable;

import java.util.Collections;
import java.util.List;

// stops entities from going out of bounds
public class BoundaryCollisionDetector<T extends iMovable & iCollidable>
        extends CollisionDetector {

    private final T entity;

    // constructor
    public BoundaryCollisionDetector(T entity) {
        if (entity == null) throw new IllegalArgumentException("Entity must not be null");
        this.entity = entity;
    }

    // loop through all entities and find which ones are overlapping
    @Override
    public List<CollisionPair> detectEntitiesList(List<iCollidable> entities) {
        if (checkCollision()) {
            return Collections.singletonList(new CollisionPair(entity, entity));
        }
        return Collections.emptyList();
    }

    // check if the two supplied objects are colliding
    public boolean checkCollision() {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float w  = entity.getBounds().width;
        float h  = entity.getBounds().height;
        return entity.getX() < 0 || entity.getX() > sw - w
            || entity.getY() < 0 || entity.getY() > sh - h;
    }

    // handle what happens after the collision
    public void resolveCollision() {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float w  = entity.getBounds().width;
        float h  = entity.getBounds().height;

        float clampedX = MathUtils.clamp(entity.getX(), 0, sw - w);
        float clampedY = MathUtils.clamp(entity.getY(), 0, sh - h);

        if (clampedX != entity.getX()) {
            entity.setPosition(clampedX, entity.getY());
            entity.setVelocity(0, entity.getVy());
        }
        if (clampedY != entity.getY()) {
            entity.setPosition(entity.getX(), clampedY);
            entity.setVelocity(entity.getVx(), 0);
        }
    }

    // runs every frame
    public void update() {
        if (checkCollision()) resolveCollision();
    }

    // getter for entity
    public T getEntity() {
        return entity;
    }
}
