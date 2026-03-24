package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iMovable;

import java.util.Collections;
import java.util.List;

/**
 * Engine-level boundary collision detector and resolver.
 *
 * Keeps any movable entity inside the visible screen area by detecting
 * when it crosses a screen edge and clamping its position back in-bounds.
 *
 * OOP:
 *   Inheritance  – extends CollisionDetector (engine hierarchy).
 *   Generics     – T must implement both iMovable and iCollidable, ensuring
 *                  type safety without referencing any concrete game class.
 *   Polymorphism – CollisionManager can store this alongside
 *                  RectangleCollisionDectector as a CollisionDetector.
 *
 * SOLID:
 *   SRP – responsible only for screen-boundary enforcement.
 *   OCP – works for any future movable entity without modification.
 *   DIP – depends on iMovable and iCollidable interfaces, not concrete types.
 *
 * Design Pattern: Strategy – a swappable detection algorithm plugged into
 *   the engine alongside AABB detection.
 *
 * No game-specific types referenced – pure engine code.
 *
 * @param <T> type that is both movable and collidable
 */
public class BoundaryCollisionDetector<T extends iMovable & iCollidable>
        extends CollisionDetector {

    private final T entity;

    public BoundaryCollisionDetector(T entity) {
        if (entity == null) throw new IllegalArgumentException("Entity must not be null");
        this.entity = entity;
    }

    // ── CollisionDetector contract ─────────────────────────────────────────

    /**
     * Returns a single-element list containing the entity if it is outside
     * screen bounds, or an empty list if it is fully inside.
     * The returned "pair" uses the entity for both slots as a convention
     * (boundary = entity colliding with itself/screen edge).
     */
    @Override
    public List<CollisionPair> detectEntitiesList(List<iCollidable> entities) {
        if (checkCollision()) {
            return Collections.singletonList(new CollisionPair(entity, entity));
        }
        return Collections.emptyList();
    }

    // ── Boundary-specific API ──────────────────────────────────────────────

    /**
     * Returns true if the entity has crossed any screen boundary.
     */
    public boolean checkCollision() {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float w  = entity.getBounds().width;
        float h  = entity.getBounds().height;
        return entity.getX() < 0 || entity.getX() > sw - w
            || entity.getY() < 0 || entity.getY() > sh - h;
    }

    /**
     * Clamps the entity position to the screen and zeroes velocity on the
     * axis that was violated.
     */
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

    /**
     * Convenience: check then resolve in one call. Use this every frame.
     */
    public void update() {
        if (checkCollision()) resolveCollision();
    }

    public T getEntity() { return entity; }
}
