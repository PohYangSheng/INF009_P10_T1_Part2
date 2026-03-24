package io.github.some_example_name.lwjgl3.Abstract_Engine.CollisionManager;

import io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager.iCollidable;

import java.util.*;

/**
 * Manages collision detection and resolution for all registered collidables.
 *
 * Design Pattern: Strategy (double) –
 *   1. CollisionDetector   : swappable algorithm for FINDING overlaps
 *      (e.g. AABB, circle, spatial-hash).
 *   2. CollisionResolution : swappable algorithm for HANDLING overlaps
 *      (abstract – game layer supplies concrete subclass).
 *
 * OOP:
 *   Polymorphism – calls detector.detectEntitiesList() and resolution.resolve()
 *     without knowing their concrete types.
 *   Generics     – pairKey uses identity hash to key any iCollidable pair.
 *
 * SOLID:
 *   SRP  – only manages the collision pipeline (detect → resolve).
 *   OCP  – detection and resolution algorithms swapped without touching
 *          this class (setDetector / setResolution).
 *   DIP  – depends on CollisionDetector and CollisionResolution abstractions,
 *          never on concrete game types.
 *
 * Static vs Dynamic collidables:
 *   Static collidables (walls, environment) resolve EVERY frame they overlap –
 *   entities must never tunnel through them.
 *   Dynamic pairs (entity-entity) use onset-only resolution so effects
 *   (damage, pickup) fire exactly once per contact.
 */
public class CollisionManager {

    private final List<iCollidable> collidables       = new ArrayList<>();
    private final Set<iCollidable>  staticCollidables  = new HashSet<>();
    private       CollisionDetector   detector;
    private       CollisionResolution resolution;

    private final Set<Long> previousPairs = new HashSet<>();

    public CollisionManager(CollisionDetector detector, CollisionResolution resolution) {
        this.detector   = detector;
        this.resolution = resolution;
    }

    // ── Registration ───────────────────────────────────────────────────────

    /** Register a dynamic collidable (onset-only resolution). */
    public void registerCollidable(iCollidable obj) {
        if (obj != null && !collidables.contains(obj)) collidables.add(obj);
    }

    /**
     * Register a STATIC collidable (walls, etc.).
     * Collisions involving a static collidable resolve every frame.
     */
    public void registerStaticCollidable(iCollidable obj) {
        if (obj == null) return;
        if (!collidables.contains(obj)) collidables.add(obj);
        staticCollidables.add(obj);
    }

    public void unregisterCollidable(iCollidable obj) {
        collidables.remove(obj);
        staticCollidables.remove(obj);
    }

    public List<iCollidable> getCollidables() { return collidables; }

    // ── Per-frame update ───────────────────────────────────────────────────

    public void update(float deltaTime) {
        List<CollisionPair> pairs   = detector.detectEntitiesList(collidables);
        Set<Long>           current = new HashSet<>(pairs.size() * 2);

        for (CollisionPair pair : pairs) {
            boolean involvesStatic = staticCollidables.contains(pair.getEntityA())
                                  || staticCollidables.contains(pair.getEntityB());
            if (involvesStatic) {
                resolution.resolve(pair);          // every frame for static
            } else {
                long key = pairKey(pair.getEntityA(), pair.getEntityB());
                current.add(key);
                if (!previousPairs.contains(key)) {
                    resolution.resolve(pair);      // onset-only for dynamic
                }
            }
        }
        previousPairs.clear();
        previousPairs.addAll(current);
    }

    // ── Strategy swap (OCP) ────────────────────────────────────────────────

    public void setDetector(CollisionDetector d)    { this.detector   = d; }
    public void setResolution(CollisionResolution r) { this.resolution = r; }

    // ── Lifecycle ──────────────────────────────────────────────────────────

    public void dispose() {
        collidables.clear();
        staticCollidables.clear();
        previousPairs.clear();
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private long pairKey(iCollidable a, iCollidable b) {
        int ha = System.identityHashCode(a), hb = System.identityHashCode(b);
        int lo = Math.min(ha, hb),           hi = Math.max(ha, hb);
        return ((long) lo << 32) ^ (hi & 0xFFFFFFFFL);
    }
}
