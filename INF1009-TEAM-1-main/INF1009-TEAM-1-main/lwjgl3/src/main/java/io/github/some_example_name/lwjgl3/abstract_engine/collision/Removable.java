package io.github.some_example_name.lwjgl3.abstract_engine.collision;

/**
 * Interface for objects that can be marked for removal.
 * Used by collision detectors and entities that should be removed after certain events.
 */
public interface Removable {
    /**
     * Marks this object to be removed.
     */
    void setToBeRemoved();
    
    /**
     * Checks if this object should be removed.
     * 
     * @return true if the object should be removed, false otherwise
     */
    boolean shouldBeRemoved();
}