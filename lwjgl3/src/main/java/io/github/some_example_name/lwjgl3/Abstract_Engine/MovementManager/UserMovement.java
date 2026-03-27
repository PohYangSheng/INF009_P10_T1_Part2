package io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager;

import com.badlogic.gdx.Input;
import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iMovable;
import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.IOManager;

// reads keyboard input and moves the player accordingly
public class UserMovement<T extends iMovable> extends Movement<T> {

    private final IOManager ioManager;

    // constructor
    public UserMovement(T entity, IOManager ioManager) {
        super(entity);
        this.ioManager = ioManager;
    }

    // update movement for this frame
    @Override
    public void move(T entity, float deltaTime) {
        if (entity == null) return;

        float dx = 0f, dy = 0f;

        for (int key : ioManager.getKeyboard().getKeysHeldThisFrame()) {
            switch (key) {
                case Input.Keys.A: case Input.Keys.LEFT:  dx -= 1f; break;
                case Input.Keys.D: case Input.Keys.RIGHT: dx += 1f; break;
                case Input.Keys.W: case Input.Keys.UP:    dy += 1f; break;
                case Input.Keys.S: case Input.Keys.DOWN:  dy -= 1f; break;
                default: break;
            }
        }

        for (int key : ioManager.getKeyboard().getKeysPressedThisFrame()) {
            switch (key) {
                case Input.Keys.A: case Input.Keys.LEFT:  dx -= 1f; break;
                case Input.Keys.D: case Input.Keys.RIGHT: dx += 1f; break;
                case Input.Keys.W: case Input.Keys.UP:    dy += 1f; break;
                case Input.Keys.S: case Input.Keys.DOWN:  dy -= 1f; break;
                default: break;
            }
        }

        dx = Math.max(-1f, Math.min(1f, dx));
        dy = Math.max(-1f, Math.min(1f, dy));

        if (dx == 0f && dy == 0f) {

            entity.stop();

            return;

        }
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        dx /= len; dy /= len;

        float speed = entity.getSpeed() > 0f ? entity.getSpeed() : 200f;
        entity.setVelocity(dx * speed, dy * speed);
    }

    // getter for IOManager used by this movement strategy to read player input
    public IOManager getIOManager() {
        return ioManager;
    }
}
