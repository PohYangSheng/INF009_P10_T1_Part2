package io.github.some_example_name.lwjgl3.Simulation.MovementSimulation;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager.AIMovement;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Dietitian;

// AI movement for the dietitian - just wanders around
public class DietitianAIMovement extends AIMovement<Dietitian> {

    private static final float DIR_CHANGE_INTERVAL = 2.0f;

    private float dirTimer = 0f;
    private float prevDx   = 1f;
    private float prevDy   = 0f;

    // constructor
    public DietitianAIMovement(Dietitian entity) {
        super(entity, 0, 0);
        float[] c   = randomCardinal();
        float   spd = entity.getSpeed();
        setVelocity(c[0] * spd, c[1] * spd);
        entity.setVelocity(c[0] * spd, c[1] * spd);
        prevDx = c[0];
        prevDy = c[1];
    }

    // update movement for this frame
    @Override
    public void move(Dietitian entity, float deltaTime) {
        if (entity == null || !entity.isActive()) return;

        dirTimer += deltaTime;
        if (dirTimer >= DIR_CHANGE_INTERVAL) {
            pickNewDirection(entity);
            dirTimer = 0f;
        }
    }

    // pick a new random direction (not backwards)
    private void pickNewDirection(Dietitian entity) {
        float[][] cards = {{ 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }};
        Array<float[]> valid = new Array<>();
        for (float[] d : cards)
            if (!(d[0] == -prevDx && d[1] == -prevDy)) valid.add(d);
        if (!valid.isEmpty()) {
            float[] c = valid.get(MathUtils.random(valid.size - 1));
            entity.setVelocity(c[0] * entity.getSpeed(), c[1] * entity.getSpeed());
            prevDx = c[0];
            prevDy = c[1];
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
