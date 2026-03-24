package io.github.some_example_name.lwjgl3.Simulation.GameEntities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.MovableEntity;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

/**
 * Roaming NPC that grants health and shows a nutrition tip on contact.
 * OOP: Extends MovableEntity; movement self-contained in update().
 * FIX: changeDirection() uses getSpeed() not hardcoded 100f.
 * FIX: markForRemoval() calls setActive(false) so EntityManager removes it.
 */
public class Nutritionist extends MovableEntity {

    private static final float DIR_CHANGE_INTERVAL = 2.0f;

    private final float  renderSize;
    private final String funFact;
    private       float  dirTimer     = 0f;
    private       float  prevDx       = 1f;
    private       float  prevDy       = 0f;
    private       boolean toBeRemoved = false;

    public Nutritionist(String name, float x, float y, Texture texture,
                        float speed, float renderSize, String funFact) {
        super(name, x, y, null, speed);
        setTexture(texture);
        this.renderSize = renderSize;
        this.funFact    = funFact;
        this.bounds     = new Rectangle(x, y, renderSize, renderSize);
        float[] c = randomCardinal();
        setVelocity(c[0] * speed, c[1] * speed);
        prevDx = c[0]; prevDy = c[1];
    }

    @Override
    public void update(float dt) {
        if (!isActive()) return;
        super.update(dt);
        dirTimer += dt;
        if (dirTimer >= DIR_CHANGE_INTERVAL) { changeDirection(); dirTimer = 0f; }
    }

    private void changeDirection() {
        float[][] cards = {{1,0},{-1,0},{0,1},{0,-1}};
        Array<float[]> valid = new Array<>();
        for (float[] d : cards)
            if (!(d[0] == -prevDx && d[1] == -prevDy)) valid.add(d);
        if (!valid.isEmpty()) {
            float[] c = valid.get(MathUtils.random(valid.size - 1));
            setVelocity(c[0] * getSpeed(), c[1] * getSpeed()); // FIX: use getSpeed()
            prevDx = c[0]; prevDy = c[1];
        }
    }

    public String  getFunFact()      { return funFact; }
    public boolean shouldBeRemoved() { return toBeRemoved; }

    public void markForRemoval() {
        toBeRemoved = true;
        setActive(false); // FIX: EntityManager removes on !isActive()
        stop();
    }

    @Override
    public Rectangle getBounds() { bounds.setPosition(getX(), getY()); return bounds; }

    @Override
    public void onCollision(iCollidable other) { /* handled externally */ }

    @Override
    public void render(SpriteBatch batch) {
        if (isActive() && getTexture() != null)
            batch.draw(getTexture(), getX(), getY(), renderSize, renderSize);
    }

    private static float[] randomCardinal() {
        switch (MathUtils.random(3)) {
            case 0:  return new float[]{ 1,  0};
            case 1:  return new float[]{-1,  0};
            case 2:  return new float[]{ 0,  1};
            default: return new float[]{ 0, -1};
        }
    }
}
