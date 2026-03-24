package io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Non-moving entity that can participate in collisions.
 * Used for environmental objects such as walls and floor tiles.
 *
 * OOP:
 *   Inheritance  – extends abstract Entity.
 *   Polymorphism – update() overridden as intentional no-op; render() overridden
 *                  to use renderWidth/renderHeight rather than raw texture size.
 *
 * SOLID:
 *   SRP – stores only static render size; no movement logic.
 *   OCP – subclasses add behaviour (e.g. swapTexture on environment tiles) without
 *         modifying this class.
 *   LSP – valid substitute for Entity anywhere in the engine.
 */
public class StaticEntity extends Entity implements iCollidable {

    private float renderWidth;
    private float renderHeight;

    public StaticEntity(String name, float x, float y, String texturePath) {
        super(name, x, y, texturePath);
        this.renderWidth  = getWidth();
        this.renderHeight = getHeight();
    }

    /** No movement – intentional no-op (Template Method hook). */
    @Override
    public void update(float deltaTime) {}

    public void  setSize(float w, float h)  { renderWidth = w; renderHeight = h; }
    public float getRenderWidth()            { return renderWidth; }
    public float getRenderHeight()           { return renderHeight; }

    @Override
    public Rectangle getBounds() { return bounds; }

    /**
     * Default collision response: no-op.
     * Subclasses override if they need to react (e.g. interactive tiles).
     */
    @Override
    public void onCollision(iCollidable other) { /* no-op by default */ }

    @Override
    public void render(SpriteBatch batch) {
        if (getTexture() != null)
            batch.draw(getTexture(), getX(), getY(), renderWidth, renderHeight);
    }
}
