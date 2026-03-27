package io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.*;

// keeps track of all entities in the current scene and handles updating/rendering them
public class EntityManager {

    private final Map<UUID, Entity> entities = new LinkedHashMap<>();

    // add entity so it gets updated and drawn
    public void addEntity(Entity entity) {
        if (entity != null) entities.put(entity.getEntityID(), entity);
    }

    // remove entity by id and dispose it
    public Entity removeEntity(UUID id) {
        if (id == null) return null;
        Entity removed = entities.remove(id);
        if (removed != null) removed.dispose();
        return removed;
    }

    // get rid of all entities
    public void clearEntities() {
        entities.values().forEach(Entity::dispose);
        entities.clear();
    }

    // find entity by id
    public Entity            getEntityByID(UUID id) {
        return id == null ? null : entities.get(id);
    }
    // get all entities
    public Collection<Entity> getAllEntities() {
        return entities.values();
    }
    // getter for number of entities currently stored in the manager
    public int               getActiveEntitiesCount() {
        return entities.size();
    }

    // get all entities of a specific type (e.g. all enemies)
    public <T extends Entity> List<T> getEntitiesOfType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Entity e : entities.values())
            if (type.isInstance(e)) result.add(type.cast(e));
        return result;
    }

    // update all entities and remove dead ones
    public void updateEntities(float deltaTime) {
        List<Entity> snapshot = new ArrayList<>(entities.values());
        for (Entity e : snapshot) e.update(deltaTime);
        entities.values().removeIf(e -> !e.isActive());
    }

    // draw all entities
    public void render(SpriteBatch batch) {
        for (Entity e : entities.values()) e.render(batch);
    }

    // clean up textures/resources so we dont leak memory
    public void dispose() {
        clearEntities();
    }
}
