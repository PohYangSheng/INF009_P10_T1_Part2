package io.github.some_example_name.lwjgl3.abstract_engine.movement;

/**
 * Interface for entities that have AI-controlled movement.
 * Implementations should handle autonomous movement logic.
 */
public interface AIMovable {
    /**
     * Updates the AI-controlled movement.
     * This should be called once per frame.
     */
    void moveAIControlled();
}