package io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Abstract engine entity that can move.
 * Implements iMovable and iCollidable – no game-specific types referenced.
 *
 * OOP:
 *   Inheritance   – extends abstract Entity.
 *   Polymorphism  – update() is overridden by every concrete entity subclass.
 *   Abstraction   – consumers use iMovable interface, not this concrete class.
 *
 * SOLID:
 *   SRP – responsible only for movement state (velocity, speed, position).
 *   OCP – subclasses extend behaviour without modifying this class.
 *   LSP – any subclass is a valid substitute for MovableEntity.
 *   DIP – BoundaryCollisionDetector depends on iMovable, not this class.
 */
public class MovableEntity extends Entity implements iMovable, iCollidable {

    private float speed;
    private float vx;
    private float vy;

    public MovableEntity(String name, float x, float y, String texturePath, float speed) {
        super(name, x, y, texturePath);
        this.speed = speed;
    }

    // ── iMovable implementation ────────────────────────────────────────────

    @Override public void  setVelocity(float vx, float vy) { this.vx = vx; this.vy = vy; }
    @Override public void  stop()                           { vx = 0f; vy = 0f; }
    @Override public float getSpeed()                       { return speed; }
    @Override public float getVx()                          { return vx; }
    @Override public float getVy()                          { return vy; }

    // getX / getY / setPosition already provided by Entity, satisfying iMovable

    /** Adjust speed clamped to a game-layer-defined range. */
    public void setSpeed(float speed) { this.speed = speed; }

    // ── Per-frame update (Template Method hook) ────────────────────────────

    @Override
    public void update(float deltaTime) {
        if (vx != 0f || vy != 0f) moveBy(vx * deltaTime, vy * deltaTime);
    }

    // ── iCollidable ────────────────────────────────────────────────────────

    @Override
    public Rectangle getBounds() { return bounds; }

    @Override
    public void onCollision(iCollidable other) {
        // Default: no-op. Subclasses override for game-specific behaviour.
    }

    // ── Convenience render ─────────────────────────────────────────────────

    public void render(SpriteBatch batch, float w, float h) {
        if (getTexture() != null) batch.draw(getTexture(), getX(), getY(), w, h);
    }
}
