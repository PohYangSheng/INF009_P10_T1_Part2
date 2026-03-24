package io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iMovable;
import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.IOManager;

/**
 * Movement strategy driven by WASD / Arrow key input.
 *
 * Design Pattern: Strategy – plugged into MovementManager at runtime.
 * OOP: Generics – T must implement iMovable.
 *
 * FIX: Uses Gdx.input.isKeyPressed() directly instead of IOManager's
 * keyboard arrays. The IOManager approach only worked if handleInput()
 * was called each frame AND the Stage input processor didn't interfere.
 * Gdx.input.isKeyPressed() polls raw hardware state every frame with
 * zero delay, regardless of input processor or frame timing.
 */
public class UserMovement<T extends iMovable> extends Movement<T> {

    private final IOManager ioManager; // kept for API compatibility

    public UserMovement(T entity, IOManager ioManager) {
        super(entity);
        this.ioManager = ioManager;
    }

    @Override
    public void move(T entity, float deltaTime) {
        if (entity == null) return;

        float dx = 0f, dy = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))  dx -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))    dy += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))  dy -= 1f;

        if (dx == 0f && dy == 0f) { entity.stop(); return; }

        float len = (float) Math.sqrt(dx * dx + dy * dy);
        dx /= len; dy /= len;

        float speed = entity.getSpeed() > 0f ? entity.getSpeed() : 200f;
        entity.setVelocity(dx * speed, dy * speed);
    }

    public IOManager getIOManager() { return ioManager; }
}
