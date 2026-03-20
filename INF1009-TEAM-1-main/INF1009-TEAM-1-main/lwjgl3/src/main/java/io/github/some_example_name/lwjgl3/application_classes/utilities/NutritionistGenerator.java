package io.github.some_example_name.lwjgl3.application_classes.utilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.application_classes.entities.Nutritionist;

/**
 * Manages the generation and spawning of nutritionist entities in the maze.
 * Periodically spawns nutritionists at random valid locations.
 */
public class NutritionistGenerator {
    /** Reference to the maze generator for positioning */
    private final MazeGenerator mazeGenerator;
    
    /** Size of each cell in the maze */
    private final float cellSize;
    
    /** Timer for nutritionist spawning */
    private float spawnTimer;
    
    /** Time between nutritionist spawns (seconds) */
    private final float spawnInterval = 5.0f;
    
    /** Reference to the food dictionary for facts */
    private final FoodDictionary foodDictionary;
    
    /**
     * Creates a new nutritionist generator.
     *
     * @param cellSize Size of each cell in the maze
     * @param mazeGenerator Reference to the maze generator
     * @param foodDictionary Reference to the food dictionary
     */
    public NutritionistGenerator(float cellSize, MazeGenerator mazeGenerator, FoodDictionary foodDictionary) {
        this.cellSize = cellSize;
        this.mazeGenerator = mazeGenerator;
        this.spawnTimer = 0;
        this.foodDictionary = foodDictionary;
    }
    
    /**
     * Updates the spawn timer and determines if a new nutritionist should spawn.
     * 
     * @param deltaTime Time elapsed since last frame (seconds)
     * @param nutritionistExists Whether a nutritionist currently exists in the game
     * @return true if a new nutritionist should be spawned, false otherwise
     */
    public boolean update(float deltaTime, boolean nutritionistExists) {
        // Update spawn timer
        spawnTimer += deltaTime;
        
        // Check if we need to spawn a nutritionist
        if (spawnTimer >= spawnInterval && !nutritionistExists) {
            spawnTimer = 0; // Reset timer
            return true;
        }
        
        return false;
    }
    
    /**
     * Creates a nutritionist at a random valid position in the maze.
     * 
     * @param texture Texture for the nutritionist
     * @param speed Movement speed for the nutritionist
     * @return A new Nutritionist entity, or null if creation failed
     */
    public Nutritionist generateNutritionist(Texture texture, float speed) {
        // Try to find a valid position
        int maxAttempts = 100;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Generate a random position in the grid
            int gridX = MathUtils.random(1, MazeGenerator.getGridWidth() - 2);
            int gridY = MathUtils.random(1, MazeGenerator.getGridHeight() - 2);
            
            // Skip if it's a wall
            if (MazeGenerator.isWall(gridX, gridY)) {
                continue;
            }
            
            // Convert to screen coordinates
            Vector2 position = mazeGenerator.gridToScreenTopLeft(gridX, gridY);
            
            // Get random nutritionist fact
            String funFact = foodDictionary.getRandomNutritionistFact();
            
            // Create nutritionist with appropriate size for the maze
            Nutritionist nutritionist = new Nutritionist("Nutritionist", position, texture, speed, funFact);
            
            // Set nutritionist size to fit within a cell (slightly smaller for movement clearance)
            nutritionist.setSize(cellSize * 0.95f, cellSize * 0.95f);
            
            // Center the nutritionist within the cell
            float offset = cellSize * 0.05f; // 5% offset for the 90% sized entity
            nutritionist.setPosition(new Vector2(
                position.x + offset,
                position.y + offset
            ));
            
            return nutritionist;
        }
        
        // If we couldn't find a valid position, return null
        System.err.println("Could not find a valid position for the nutritionist after " + maxAttempts + " attempts");
        return null;
    }
}