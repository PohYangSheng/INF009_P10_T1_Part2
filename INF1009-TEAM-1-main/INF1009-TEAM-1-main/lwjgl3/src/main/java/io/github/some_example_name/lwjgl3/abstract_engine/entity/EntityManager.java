package io.github.some_example_name.lwjgl3.abstract_engine.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.some_example_name.lwjgl3.abstract_engine.collision.Removable;
import io.github.some_example_name.lwjgl3.abstract_engine.movement.AIMovable;

/**
 * Manages a collection of game entities.
 * Handles entity updates, rendering, and lifecycle management.
 */
public class EntityManager {
    private final List<Entity> entities;

    /**
     * Creates a new entity manager.
     */
    public EntityManager() {
        this.entities = new ArrayList<>();
    }

    /**
     * Adds an entity to the manager.
     *
     * @param entity The entity to add
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Checks for and removes entities that are marked for removal.
     */
    public void checkForEntitiesToBeRemoved() {
        // Creates an Iterator for the list of entities - to remove/access safely
        Iterator<Entity> iterator = this.entities.iterator();
        
        // Checks if there are more elements in the collection
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if ((entity instanceof Removable) && ((Removable) entity).shouldBeRemoved()) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Gets an entity by its index in the collection.
     *
     * @param i Index of the entity to retrieve
     * @return The entity at the specified index
     */
    public Entity getEntity(int i) {
        return entities.get(i);
    }

    /**
     * Gets the number of entities in the manager.
     *
     * @return The number of entities
     */
    public int getEntitiesSize() {
        return entities.size();
    }

    /**
     * Draws all managed entities.
     *
     * @param batch SpriteBatch to use for drawing
     */
    public void drawEntites(SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.draw(batch);
        }
    }

    /**
     * Updates movement for all AI-controlled entities.
     */
    public void moveEntities() {
        for (Entity entity : entities) {
            // Move entities that implement AIMovable
            if (entity instanceof AIMovable) {
                ((AIMovable) entity).moveAIControlled();
            }
        }
    }
}