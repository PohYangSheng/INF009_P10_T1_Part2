package io.github.some_example_name.lwjgl3.Abstract_Engine.MovementManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iMovable;

import java.util.ArrayList;
import java.util.List;

// stores all movement strategies and updates them each frame
public class MovementManager {

    private final List<Movement<? extends iMovable>> movements = new ArrayList<>();

    // add a movement strategy
    public void addMovement(Movement<? extends iMovable> movement) {
        if (movement != null) movements.add(movement);
    }

    // remove a movement strategy
    public void removeMovement(Movement<? extends iMovable> movement) {
        movements.remove(movement);
    }

    // update all movement strategies
    public void update(float deltaTime) {
        for (Movement<? extends iMovable> m : movements) {
            if (m.getEntity() != null) callMove(m, deltaTime);
        }
    }

    // helper to call move() with proper generics
    private <T extends iMovable> void callMove(Movement<T> m, float deltaTime) {
        m.move(m.getEntity(), deltaTime);
    }

    // getter for movement strategies currently registered with this manager
    public List<Movement<? extends iMovable>> getMovements() {
        return movements;
    }
}
