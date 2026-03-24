package io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.*;

/**
 * Manages lifecycle of all Entity instances.
 * OOP: Generics used in getEntitiesOfType() for type-safe queries.
 * SOLID: SRP – stores, updates, renders entities only.
 *        OCP – works with any Entity subclass without modification.
 * Design Pattern: UUID key for O(1) lookup.
 *
 * FIX: updateEntities() snapshots entity list before calling update()
 * to prevent ConcurrentModificationException when an entity update calls
 * markForRemoval() -> setActive(false) mid-iteration.
 */
public class EntityManager {

    private final Map<UUID, Entity> entities = new LinkedHashMap<>();

    public void addEntity(Entity entity) {
        if (entity != null) entities.put(entity.getEntityID(), entity);
    }

    public Entity removeEntity(UUID id) {
        if (id == null) return null;
        Entity removed = entities.remove(id);
        if (removed != null) removed.dispose();
        return removed;
    }

    public void clearEntities() {
        entities.values().forEach(Entity::dispose);
        entities.clear();
    }

    public Entity            getEntityByID(UUID id)   { return id == null ? null : entities.get(id); }
    public Collection<Entity> getAllEntities()         { return entities.values(); }
    public int               getActiveEntitiesCount() { return entities.size(); }

    /**
     * Generic type-safe entity query.
     * Demonstrates generics (rubric requirement).
     */
    public <T extends Entity> List<T> getEntitiesOfType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Entity e : entities.values())
            if (type.isInstance(e)) result.add(type.cast(e));
        return result;
    }

    /**
     * FIX: Snapshot to List before iterating so that setActive(false)
     * calls inside update() don't cause ConcurrentModificationException.
     */
    public void updateEntities(float deltaTime) {
        List<Entity> snapshot = new ArrayList<>(entities.values());
        for (Entity e : snapshot) e.update(deltaTime);
        entities.values().removeIf(e -> !e.isActive());
    }

    public void render(SpriteBatch batch) {
        for (Entity e : entities.values()) e.render(batch);
    }

    public void dispose() { clearEntities(); }
}
