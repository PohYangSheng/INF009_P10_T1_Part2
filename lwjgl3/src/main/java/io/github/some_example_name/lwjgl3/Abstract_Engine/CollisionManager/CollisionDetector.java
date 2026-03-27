package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

import java.util.List;

// abstract class for different ways to detect collisions
public abstract class CollisionDetector {

    public abstract List<CollisionPair> detectEntitiesList(List<iCollidable> entities);
}
