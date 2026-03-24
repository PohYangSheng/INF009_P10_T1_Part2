package io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iMovable;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all active Movement instances and drives them each frame.
 *
 * SOLID: OCP – new movement types are added by registering them here,
 *              not by modifying this class.
 * OOP: Uses raw Movement (wildcard) so it can hold any generic Movement<T>.
 */
public class MovementManager {

    private final List<Movement<? extends iMovable>> movements = new ArrayList<>();

    public void addMovement(Movement<? extends iMovable> movement) {
        if (movement != null) movements.add(movement);
    }

    public void removeMovement(Movement<? extends iMovable> movement) {
        movements.remove(movement);
    }

    @SuppressWarnings("unchecked")
    public void update(float deltaTime) {
        for (Movement m : movements) {
            iMovable e = m.getEntity();
            if (e != null) m.move(e, deltaTime);
        }
    }

    public List<Movement<? extends iMovable>> getMovements() { return movements; }
}
