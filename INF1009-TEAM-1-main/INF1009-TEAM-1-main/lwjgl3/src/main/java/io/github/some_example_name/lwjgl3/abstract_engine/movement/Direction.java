package io.github.some_example_name.lwjgl3.abstract_engine.movement;

/**
 * Represents cardinal movement directions with associated vector components.
 */
public enum Direction {
    UP(0, 1),
    RIGHT(1, 0),
    DOWN(0, -1),
    LEFT(-1, 0);
    
    private final float dx;
    private final float dy;
    
    Direction(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    /**
     * Gets the x component of this direction vector
     * @return x component (-1, 0, or 1)
     */
    public float getX() { 
        return dx; 
    }
    
    /**
     * Gets the y component of this direction vector
     * @return y component (-1, 0, or 1)
     */
    public float getY() { 
        return dy; 
    }
}