package io.github.some_example_name.lwjgl3.application_classes.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.lwjgl3.application_classes.entities.Food;
import io.github.some_example_name.lwjgl3.application_classes.entities.HealthyFood;
import io.github.some_example_name.lwjgl3.application_classes.entities.UnhealthyFood;

/**
 * Utility class responsible for generating and managing food items in the maze.
 * Handles spawning, positioning, and lifecycle management of food entities.
 */
public class FoodGenerator {
    /** List of all active food items */
    private final List<Food> foodList;
    
    /** Size of each cell in the maze grid */
    private final float cellSize;
    
    /** Reference to the maze generator for positioning */
    private final MazeGenerator mazeGenerator;
    
    /** Percentage of generated food that will be healthy (0.0-1.0) */
    private static final float HEALTHY_FOOD_RATIO = 0.7f; // 70% healthy, 30% unhealthy
    
    /** Maximum attempts to find a valid position for new food */
    private static final int MAX_FOOD_ATTEMPTS = 100;

    /** Timer for food spawning */
    private float spawnTimer;
    
    /** Time between food spawns (seconds) */
    private final float spawnInterval = 2.0f;
    
    /** Maximum number of food items allowed simultaneously */
    private final int maxFoodCount = 10;
    
    /** How long food items last before expiring (seconds) */
    private final float foodLifespan = 10.0f;

    /** Cache of healthy food textures */
    private final Map<String, Texture> healthyFoodTextures = new HashMap<>();
    
    /** Cache of unhealthy food textures */
    private final Map<String, Texture> unhealthyFoodTextures = new HashMap<>();
    
    /** Reference to the food dictionary for facts */
    private final FoodDictionary foodDictionary;
    
    /**
     * Creates a new food generator.
     *
     * @param cellSize Size of each cell in the maze
     * @param mazeGenerator Reference to the maze generator
     * @param foodDictionary Reference to the food dictionary
     */
    public FoodGenerator(float cellSize, MazeGenerator mazeGenerator, FoodDictionary foodDictionary) {
        this.foodList = new ArrayList<>();
        this.cellSize = cellSize;
        this.mazeGenerator = mazeGenerator;
        this.spawnTimer = 0;
        this.foodDictionary = foodDictionary;
        
        // Initialize texture caches
        initializeTextures();
    }
    
    /**
     * Loads and caches textures for all food types.
     * Prevents repeated loading of the same textures.
     */
    private void initializeTextures() {
        // Load healthy food textures
        String[] healthyKeys = {"apple.png", "banana.png", "blueberry.png", "carrot.png"};
        for (String key : healthyKeys) {
            healthyFoodTextures.put(key, new Texture(Gdx.files.internal("entities_images/" + key)));
        }
        
        // Load unhealthy food textures
        String[] unhealthyKeys = {"icecream.png", "friedchicken.png", "coke.png", "fries.png"};
        for (String key : unhealthyKeys) {
            unhealthyFoodTextures.put(key, new Texture(Gdx.files.internal("entities_images/" + key)));
        }
    }

    /**
     * Releases all texture resources.
     * Should be called when the game is closing.
     */
    public void dispose() {
        // Dispose healthy food textures
        for (Texture texture : healthyFoodTextures.values()) {
            texture.dispose();
        }
        
        // Dispose unhealthy food textures
        for (Texture texture : unhealthyFoodTextures.values()) {
            texture.dispose();
        }
        
        healthyFoodTextures.clear();
        unhealthyFoodTextures.clear();
    }

    /**
     * Updates all food items and generates new ones if needed.
     * Should be called once per frame.
     * 
     * @param deltaTime Time elapsed since last frame (seconds)
     * @return List of new food items generated during this update
     */
    public List<Food> update(float deltaTime) {
        List<Food> newFoodItems = new ArrayList<>();

        // Update all existing food items and remove expired ones
        updateExistingFood(deltaTime);

        // Update spawn timer
        spawnTimer += deltaTime;

        // Check if we need to spawn more food
        if (spawnTimer >= spawnInterval && foodList.size() < maxFoodCount) {
            Food newFood = generateSingleFood();
            if (newFood != null) {
                foodList.add(newFood);
                newFoodItems.add(newFood);
            }
            spawnTimer = 0; // Reset timer
        }

        return newFoodItems;
    }

    /**
     * Updates existing food items and removes expired ones.
     * 
     * @param deltaTime Time elapsed since last frame (seconds)
     */
    private void updateExistingFood(float deltaTime) {
        List<Food> expiredFood = new ArrayList<>();

        for (Food food : foodList) {
            // Update food's elapsed time
            food.update(deltaTime);

            // Check if food has expired or been consumed
            if (food.isExpired() || food.shouldBeRemoved()) {
                expiredFood.add(food);
            }
        }

        // Remove expired/consumed food
        foodList.removeAll(expiredFood);
    }

    /**
     * Generates a specified number of food items in the maze.
     *
     * @param count Number of food items to generate
     * @return List of generated food items
     */
    public List<Food> generateFood(int count) {
        List<Food> generatedFood = new ArrayList<>();
        int foodsGenerated = 0;
        int attempts = 0;

        while (foodsGenerated < count && attempts < MAX_FOOD_ATTEMPTS) {
            Food food = generateSingleFood();
            if (food != null) {
                foodList.add(food);
                generatedFood.add(food);
                foodsGenerated++;
            }
            attempts++;
        }

        return generatedFood;
    }

    /**
     * Attempts to generate a single food item in a valid position.
     * 
     * @return The generated food item, or null if generation failed
     */
    private Food generateSingleFood() {
        // Generate a random position in the grid
        int gridX = MathUtils.random(1, MazeGenerator.getGridWidth() - 2);
        int gridY = MathUtils.random(1, MazeGenerator.getGridHeight() - 2);

        // Skip if it's a wall
        if (MazeGenerator.isWall(gridX, gridY)) {
            return null;
        }

        // Convert to screen coordinates
        Vector2 position = mazeGenerator.gridToScreenTopLeft(gridX, gridY);

        float sizeFactor = 0.8f; // Adjust this to your preferred size (70% of cell size)
        // Add offset to center food in cell
        float offset = cellSize * (1.0f - sizeFactor) / 2;// 25% offset to create a smaller food item
        position.add(offset, offset);

        // Check for collision with existing food items
        Rectangle foodBounds = new Rectangle(position.x, position.y, cellSize * 0.5f, cellSize * 0.5f);

        for (Food existingFood : foodList) {
            if (existingFood.getBoundingBox().overlaps(foodBounds)) {
                return null; // Collision detected, can't place food here
            }
        }

        // Determine food type
        boolean isHealthy = MathUtils.random() < HEALTHY_FOOD_RATIO;
        Food food;

        if (isHealthy) {
            // Get random healthy food
            String[] healthyFood = foodDictionary.getRandomHealthyFood();
            String textureName = healthyFood[0];
            String foodFact = healthyFood[1];
            
            // Get texture from cache
            Texture foodTexture = healthyFoodTextures.get(textureName);
            
            // Create healthy food with random health boost (5-15)
            int healthBoost = MathUtils.random(5, 15);
            food = new HealthyFood("HealthyFood_" + System.currentTimeMillis(), position, 
                                  foodTexture, healthBoost, foodLifespan, foodFact);
        } else {
            // Get random unhealthy food
            String[] unhealthyFood = foodDictionary.getRandomUnhealthyFood();
            String textureName = unhealthyFood[0];
            String foodFact = unhealthyFood[1];
            
            // Get texture from cache
            Texture foodTexture = unhealthyFoodTextures.get(textureName);
            
            // Create unhealthy food with random damage (5-15)
            int damage = MathUtils.random(5, 15);
            food = new UnhealthyFood("UnhealthyFood_" + System.currentTimeMillis(), position,
                                    foodTexture, damage, foodLifespan, foodFact);
        }

        // Set food size to half a cell
        food.setSize(cellSize * sizeFactor, cellSize * sizeFactor);

        return food;
    }

    /**
     * Gets a specific food item by index.
     * 
     * @param index Index of the food item
     * @return Food item at the specified index
     */
    public Food getFood(int index) {
        return foodList.get(index);
    }

    /**
     * Gets the number of food items currently in the game.
     * 
     * @return Number of food items
     */
    public int getFoodListSize() {
        return foodList.size();
    }

    /**
     * Gets all current food entities.
     * 
     * @return List of all food entities
     */
    public List<Food> getAllFood() {
        return new ArrayList<>(foodList);
    }

    /**
     * Removes a food item from the list.
     * 
     * @param food The food item to remove
     */
    public void removeFood(Food food) {
        foodList.remove(food);
    }
}