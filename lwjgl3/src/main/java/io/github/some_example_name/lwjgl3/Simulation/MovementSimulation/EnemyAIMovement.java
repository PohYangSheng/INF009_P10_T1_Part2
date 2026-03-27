package io.github.some_example_name.lwjgl3.Simulation.MovementSimulation;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.Entity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager.AIMovement;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Enemy;

// AI movement for enemies - chase, scatter, random
public class EnemyAIMovement extends AIMovement<Enemy> {

    public enum Behaviour { RANDOM, CHASE, SCATTER }

    private static final float DIR_CHANGE_INTERVAL   = 1.0f;
    private static final float BEHAVIOUR_INTERVAL    = 5.0f;
    private static final float COLLISION_STATE_DUR   = 0.5f;
    private static final float CHASE_CHANCE          = 0.9f;

    private float     dirTimer       = 0f;
    private float     behaviourTimer = 0f;
    private float     collisionTimer = 0f;
    private float[]   prevDir        = { 1f, 0f };
    private Behaviour behaviour      = Behaviour.RANDOM;

    // constructor
    public EnemyAIMovement(Enemy entity) {
        super(entity, 0, 0);
        float[] card = randomCardinal();
        float   spd  = entity.getSpeed();
        setVelocity(card[0] * spd, card[1] * spd);
        entity.setVelocity(card[0] * spd, card[1] * spd);
        prevDir[0] = card[0];
        prevDir[1] = card[1];
    }

    // update movement for this frame
    @Override
    public void move(Enemy entity, float deltaTime) {
        if (entity == null || !entity.isActive()) return;

        dirTimer       += deltaTime;
        behaviourTimer += deltaTime;

        if (entity.isInCollision()) {
            collisionTimer += deltaTime;
            if (collisionTimer >= COLLISION_STATE_DUR) {
                entity.setInCollision(false);
                collisionTimer = 0f;
                pickBehaviour(entity);
            }
            return;                       
        }
        collisionTimer = 0f;             

        if (behaviourTimer >= BEHAVIOUR_INTERVAL) {
            pickBehaviour(entity);
            behaviourTimer = 0f;
        }

        switch (behaviour) {
            case CHASE:
                if (entity.getTarget() != null) chaseTarget(entity);
                else                            moveRandom(entity);
                break;
            default:
                moveRandom(entity);
        }

        prevDir[0] = entity.getVx() == 0f ? prevDir[0] : Math.signum(entity.getVx());
        prevDir[1] = entity.getVy() == 0f ? prevDir[1] : Math.signum(entity.getVy());
    }

    // randomly pick chase/scatter/random behaviour
    private void pickBehaviour(Enemy entity) {
        float r = MathUtils.random();
        if (entity.getTarget() != null && r < CHASE_CHANCE)
            behaviour = Behaviour.CHASE;
        else if (r < 0.85f)
            behaviour = Behaviour.SCATTER;
        else
            behaviour = Behaviour.RANDOM;
    }

    // move towards the player
    private void chaseTarget(Enemy entity) {
        if (dirTimer < DIR_CHANGE_INTERVAL
                && !(entity.getVx() == 0f && entity.getVy() == 0f))
            return;

        Entity target = entity.getTarget();
        float  dx     = target.getX() - entity.getX();
        float  dy     = target.getY() - entity.getY();
        float[] dir   = Math.abs(dx) > Math.abs(dy)
                ? new float[]{ Math.signum(dx), 0f }
                : new float[]{ 0f, Math.signum(dy) };

        if (dir[0] == -prevDir[0] && dir[1] == -prevDir[1])
            dir = Math.abs(dx) > Math.abs(dy)
                    ? new float[]{ 0f, Math.signum(dy) }
                    : new float[]{ Math.signum(dx), 0f };

        entity.setVelocity(dir[0] * entity.getSpeed(), dir[1] * entity.getSpeed());
        dirTimer = 0f;
    }

    // wander around randomly
    private void moveRandom(Enemy entity) {
        if (dirTimer >= DIR_CHANGE_INTERVAL
                || (entity.getVx() == 0f && entity.getVy() == 0f)) {
            pickNewDirection(entity);
            dirTimer = 0f;
        }
    }

    // pick a new random direction (not backwards)
    private void pickNewDirection(Enemy entity) {
        float[][] cards = {{ 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }};
        Array<float[]> valid = new Array<>();
        for (float[] d : cards)
            if (!(d[0] == -prevDir[0] && d[1] == -prevDir[1])) valid.add(d);
        if (valid.size > 0) {
            float[] c = valid.get(MathUtils.random(valid.size - 1));
            entity.setVelocity(c[0] * entity.getSpeed(), c[1] * entity.getSpeed());
        }
    }

    // get a random up/down/left/right direction
    private static float[] randomCardinal() {
        switch (MathUtils.random(3)) {
            case 0:  return new float[]{ 1,  0 };
            case 1:  return new float[]{ -1, 0 };
            case 2:  return new float[]{ 0,  1 };
            default: return new float[]{ 0, -1 };
        }
    }
}
