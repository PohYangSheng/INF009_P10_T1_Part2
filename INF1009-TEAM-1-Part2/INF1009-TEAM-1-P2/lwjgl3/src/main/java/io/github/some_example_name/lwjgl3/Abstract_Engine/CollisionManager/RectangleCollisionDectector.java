package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete collision detection strategy using AABB (Rectangle overlap).
 *
 * Design Pattern: Strategy – concrete implementation of CollisionDetector.
 * OOP: Polymorphism – registered in CollisionManager as a CollisionDetector.
 *
 * Note: kept original name from INF1009_P10_T1 (typo preserved for compatibility).
 */
public class RectangleCollisionDectector extends CollisionDetector {

    @Override
    public List<CollisionPair> detectEntitiesList(List<iCollidable> entities) {
        List<CollisionPair> collisions = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                iCollidable a = entities.get(i);
                iCollidable b = entities.get(j);
                if (a.getBounds().overlaps(b.getBounds())) {
                    collisions.add(new CollisionPair(a, b));
                }
            }
        }
        return collisions;
    }
}
