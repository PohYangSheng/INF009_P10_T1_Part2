package io.github.some_example_name.lwjgl3.abstract_engine.collision;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages collision detection and resolution between game objects.
 * Coordinates multiple collision detectors and processes them each frame.
 */
public class CollisionManager {
    private final ArrayList<CollisionDetector> detectors;
    
    /**
     * Creates a new collision manager.
     */
    public CollisionManager() {
        detectors = new ArrayList<>();
    }
    
    /**
     * Adds a collision detector to the manager.
     * 
     * @param detector The collision detector to add
     */
    public void addDetector(CollisionDetector detector) {
        detectors.add(detector);
    }
    
    /**
     * Removes all collision detectors.
     * Useful when rebuilding collision detection for a new scene.
     */
    public void clearDetectors() {
        detectors.clear();
    }
    
    /**
     * Checks and resolves all collisions.
     * Should be called once per frame.
     */
    public void checkAllCollisions() {
        for (CollisionDetector detector : detectors) {
            if (detector.checkCollision()) {
                detector.resolveCollision();
            }
        }
    }
    
    /**
     * Removes detectors that are marked for removal.
     * Should be called after processing collisions.
     */
    public void checkForDetectorsToBeRemoved() {
        Iterator<CollisionDetector> iterator = detectors.iterator();
        while (iterator.hasNext()) {
            CollisionDetector detector = iterator.next();
            if ((detector instanceof Removable) && ((Removable) detector).shouldBeRemoved()) {
                iterator.remove();
            }
        }
    }
}