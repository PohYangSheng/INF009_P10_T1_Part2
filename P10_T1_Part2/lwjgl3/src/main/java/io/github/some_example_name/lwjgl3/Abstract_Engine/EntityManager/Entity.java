package io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.UUID;

/**
 * Abstract base class for all game entities.
 *
 * OOP: Abstract class with template method pattern (update/render hooks).
 * SOLID:
 *   - SRP: stores identity, position, texture only
 *   - OCP: subclasses extend behaviour without modifying this class
 *   - LSP: all subclasses are valid Entity substitutes
 */
public abstract class Entity {

    private final UUID    entityID;
    private       String  name;
    private       float   x;
    private       float   y;
    private       Texture texture;
    private       boolean active = true;

    protected Rectangle bounds;

    // ── Constructors ───────────────────────────────────────────────────────

    public Entity(String name, float x, float y, String texturePath) {
        this.entityID = UUID.randomUUID();
        this.name     = name;
        this.x        = x;
        this.y        = y;

        if (texturePath != null) {
            this.texture = new Texture(texturePath);
            this.bounds  = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        } else {
            this.bounds  = new Rectangle(x, y, 0, 0);
        }
    }

    // ── Abstract hooks (Template Method pattern) ───────────────────────────

    /** Called every frame – subclasses define their own behaviour. */
    public abstract void update(float deltaTime);

    // ── Rendering ──────────────────────────────────────────────────────────

    public void render(SpriteBatch batch) {
        if (texture != null) batch.draw(texture, x, y);
    }

    // ── Position ───────────────────────────────────────────────────────────

    public float getX() { return x; }
    public float getY() { return y; }

    public void setPosition(float x, float y) {
        this.x = x; this.y = y;
        if (bounds != null) bounds.setPosition(x, y);
    }

    public void moveBy(float dx, float dy) {
        this.x += dx; this.y += dy;
        if (bounds != null) bounds.setPosition(this.x, this.y);
    }

    // ── Identity ───────────────────────────────────────────────────────────

    public UUID   getEntityID() { return entityID; }
    public String getName()     { return name; }
    public void   setName(String name) { this.name = name; }

    // ── Texture / Size ─────────────────────────────────────────────────────

    public Texture getTexture()              { return texture; }
    public void    setTexture(Texture t)     { this.texture = t; }
    public int     getWidth()                { return texture == null ? 0 : texture.getWidth(); }
    public int     getHeight()               { return texture == null ? 0 : texture.getHeight(); }

    // ── Lifecycle ──────────────────────────────────────────────────────────

    public boolean isActive() { return active; }
    public void    setActive(boolean active) { this.active = active; }

    public void dispose() {
        if (texture != null) { texture.dispose(); texture = null; }
    }
}
