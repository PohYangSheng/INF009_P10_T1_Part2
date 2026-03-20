package io.github.some_example_name.lwjgl3.abstract_engine.movement;

/**
 * Interface for entities that can be moved by user input.
 * Implementations should handle user-controlled movement logic.
 */
public interface UserMovable {
    /**
     * Moves the entity in the specified direction.
     * 
     * @param direction The direction to move
     */
    void move(Direction direction);
}