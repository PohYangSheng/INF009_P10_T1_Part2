package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.Entity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.MovableEntity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

/**
 * Enemy (angry chef) with three AI behaviours: RANDOM, CHASE, SCATTER.
 *
 * BUG FIX: markForRemoval() previously only set toBeRemoved = true but never
 * called setActive(false). EntityManager.updateEntities() only removes entities
 * where isActive() == false, so enemies were never removed after being eaten in
 * SECRET mode – they stayed on screen and kept colliding.
 *
 * FIX: markForRemoval() now also calls setActive(false).
 *
 * OOP: Polymorphic update(); enum for behaviour states.
 */
public class Enemy extends MovableEntity {

    public enum Behaviour { RANDOM, CHASE, SCATTER }

    private static final float DIR_CHANGE_INTERVAL   = 1.0f;
    private static final float BEHAVIOUR_INTERVAL    = 5.0f;
    private static final float COLLISION_STATE_DUR   = 0.5f;
    private static final long  COLLISION_COOLDOWN_MS = 500L;
    private static final float CHASE_CHANCE          = 0.9f;

    private final float renderSize;
    private float   dirTimer        = 0f;
    private float   behaviourTimer  = 0f;
    private float   collisionTimer  = 0f;
    private boolean inCollision     = false;
    private long    lastCollisionMs = 0L;
    private float[] prevDir         = { 1f, 0f };
    private Behaviour behaviour     = Behaviour.RANDOM;
    private Entity    target        = null;
    private boolean   toBeRemoved   = false;

    public Enemy(String name, float x, float y, Texture texture, float speed, float renderSize) {
        super(name, x, y, null, speed);
        setTexture(texture);
        this.renderSize = renderSize;
        this.bounds     = new Rectangle(x, y, renderSize, renderSize);
        float[] card    = randomCardinal();
        setVelocity(card[0] * speed, card[1] * speed);
        prevDir[0] = card[0]; prevDir[1] = card[1];
    }

    // ── Per-frame AI ───────────────────────────────────────────────────────

    @Override
    public void update(float dt) {
        if (!isActive()) return;          // skip update if already removed
        super.update(dt);                 // applies velocity → position
        dirTimer       += dt;
        behaviourTimer += dt;

        if (inCollision) {
            collisionTimer += dt;
            if (collisionTimer >= COLLISION_STATE_DUR) {
                inCollision    = false;
                collisionTimer = 0f;
                pickBehaviour();
            }
            return;
        }

        if (behaviourTimer >= BEHAVIOUR_INTERVAL) { pickBehaviour(); behaviourTimer = 0f; }

        switch (behaviour) {
            case CHASE:
                if (target != null) chaseTarget(dt); else moveRandom(dt);
                break;
            default:
                moveRandom(dt);
        }
        prevDir[0] = getVx() == 0f ? prevDir[0] : Math.signum(getVx());
        prevDir[1] = getVy() == 0f ? prevDir[1] : Math.signum(getVy());
    }

    private void pickBehaviour() {
        float r = MathUtils.random();
        if (target != null && r < CHASE_CHANCE) behaviour = Behaviour.CHASE;
        else if (r < 0.85f)                     behaviour = Behaviour.SCATTER;
        else                                     behaviour = Behaviour.RANDOM;
    }

    private void chaseTarget(float dt) {
        if (dirTimer < DIR_CHANGE_INTERVAL && !isZeroVelocity()) return;
        float dx = target.getX() - getX(), dy = target.getY() - getY();
        float[] dir = Math.abs(dx) > Math.abs(dy)
                ? new float[]{ Math.signum(dx), 0f }
                : new float[]{ 0f, Math.signum(dy) };
        if (dir[0] == -prevDir[0] && dir[1] == -prevDir[1])
            dir = Math.abs(dx) > Math.abs(dy)
                    ? new float[]{ 0f, Math.signum(dy) }
                    : new float[]{ Math.signum(dx), 0f };
        setVelocity(dir[0] * getSpeed(), dir[1] * getSpeed());
        dirTimer = 0f;
    }

    private void moveRandom(float dt) {
        if (dirTimer >= DIR_CHANGE_INTERVAL || isZeroVelocity()) {
            changeDirection();
            dirTimer = 0f;
        }
    }

    private boolean isZeroVelocity() { return getVx() == 0f && getVy() == 0f; }

    public void changeDirection() {
        float[][] cards = {{ 1,0},{-1,0},{0,1},{0,-1}};
        Array<float[]> valid = new Array<>();
        for (float[] d : cards)
            if (!(d[0] == -prevDir[0] && d[1] == -prevDir[1])) valid.add(d);
        if (valid.size > 0) {
            float[] c = valid.get(MathUtils.random(valid.size - 1));
            setVelocity(c[0] * getSpeed(), c[1] * getSpeed());
        }
    }

    public void forceDirectionChange() {
        long now = TimeUtils.millis();
        if (now - lastCollisionMs < COLLISION_COOLDOWN_MS) return;
        lastCollisionMs = now;
        float[][] perp = getVx() != 0f
                ? new float[][]{{0,1},{0,-1}}
                : new float[][]{{1,0},{-1,0}};
        float[] c = perp[MathUtils.random(1)];
        setVelocity(c[0] * getSpeed(), c[1] * getSpeed());
        prevDir[0] = c[0]; prevDir[1] = c[1];
        dirTimer = 0f;
    }

    // ── State ──────────────────────────────────────────────────────────────

    public void    setTarget(Entity target)  { this.target = target; }
    public void    setInCollision(boolean v) { inCollision = v; collisionTimer = 0f; }
    public boolean isInCollision()           { return inCollision; }
    public boolean shouldBeRemoved()         { return toBeRemoved; }

    /**
     * BUG FIX: also call setActive(false) so EntityManager removes this enemy.
     */
    public void markForRemoval() {
        toBeRemoved = true;
        setActive(false);   // ← KEY FIX
        stop();             // stop movement immediately
    }

    // ── iCollidable ────────────────────────────────────────────────────────

    @Override
    public Rectangle getBounds() {
        bounds.setPosition(getX(), getY());
        return bounds;
    }

    @Override
    public void onCollision(iCollidable other) { /* handled externally */ }

    // ── Rendering ──────────────────────────────────────────────────────────

    @Override
    public void render(SpriteBatch batch) {
        if (isActive() && getTexture() != null)
            batch.draw(getTexture(), getX(), getY(), renderSize, renderSize);
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private static float[] randomCardinal() {
        switch (MathUtils.random(3)) {
            case 0: return new float[]{ 1, 0};
            case 1: return new float[]{-1, 0};
            case 2: return new float[]{ 0, 1};
            default:return new float[]{ 0,-1};
        }
    }
}
