package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

// abstract class for what happens when two things collide - subclasses define the actual behaviour
public abstract class CollisionResolution {

    public abstract void resolve(CollisionPair pair);
}
