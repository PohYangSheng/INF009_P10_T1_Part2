package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

import java.util.ArrayList;
import java.util.List;

// checks collisions using rectangle overlaps
public class RectangleCollisionDectector extends CollisionDetector {

    // loop through all entities and find which ones are overlapping
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
