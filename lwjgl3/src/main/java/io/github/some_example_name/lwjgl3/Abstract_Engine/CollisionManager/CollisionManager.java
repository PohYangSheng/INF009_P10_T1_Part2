package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

import java.util.*;

// manages all collisions - detects overlaps then resolves them
public class CollisionManager {

    private final List<iCollidable> collidables       = new ArrayList<>();
    private final Set<iCollidable>  staticCollidables  = new HashSet<>();
    private       CollisionDetector   detector;
    private       CollisionResolution resolution;

    private final Set<Long> previousPairs = new HashSet<>();

    // constructor
    public CollisionManager(CollisionDetector detector, CollisionResolution resolution) {
        this.detector   = detector;
        this.resolution = resolution;
    }

    // add something to the collision checking list
    public void registerCollidable(iCollidable obj) {
        if (obj != null && !collidables.contains(obj)) collidables.add(obj);
    }

    // register a static thing like a wall for collisions
    public void registerStaticCollidable(iCollidable obj) {
        if (obj == null) return;
        if (!collidables.contains(obj)) collidables.add(obj);
        staticCollidables.add(obj);
    }

    // remove from collision checks
    public void unregisterCollidable(iCollidable obj) {
        collidables.remove(obj);
        staticCollidables.remove(obj);
    }

    // getter for collidable objects currently registered with the collision manager
    public List<iCollidable> getCollidables() {
        return collidables;
    }

    // runs every frame
    public void update(float deltaTime) {
        List<CollisionPair> pairs   = detector.detectEntitiesList(collidables);
        Set<Long>           current = new HashSet<>(pairs.size() * 2);

        for (CollisionPair pair : pairs) {
            boolean involvesStatic = staticCollidables.contains(pair.getEntityA())
                                  || staticCollidables.contains(pair.getEntityB());
            if (involvesStatic) {
                resolution.resolve(pair);          
            } else {
                long key = pairKey(pair.getEntityA(), pair.getEntityB());
                current.add(key);
                if (!previousPairs.contains(key)) {
                    resolution.resolve(pair);
                }
            }
        }
        previousPairs.clear();
        previousPairs.addAll(current);
    }

    // swap out the collision detection method
    public void setDetector(CollisionDetector d) {
        this.detector   = d;
    }
    // swap out the collision resolution method
    public void setResolution(CollisionResolution r) {
        this.resolution = r;
    }

    // clear everything out
    public void dispose() {
        collidables.clear();
        staticCollidables.clear();
        previousPairs.clear();
    }

    // make a unique key for a pair of colliding objects so we can track them
    private long pairKey(iCollidable a, iCollidable b) {
        int ha = System.identityHashCode(a), hb = System.identityHashCode(b);
        int lo = Math.min(ha, hb),           hi = Math.max(ha, hb);
        return ((long) lo << 32) ^ (hi & 0xFFFFFFFFL);
    }
}
