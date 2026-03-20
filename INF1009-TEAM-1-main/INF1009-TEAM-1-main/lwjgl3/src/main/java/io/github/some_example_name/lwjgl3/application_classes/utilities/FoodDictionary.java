package io.github.some_example_name.lwjgl3.application_classes.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Manages food-related data including food types, facts, and nutritionist advice.
 * Provides random selection of food items and their associated information.
 */
public class FoodDictionary {
    private final Random random = new Random();
    private final Map<String, String> healthyDict = new HashMap<>();
    private final Map<String, String> unhealthyDict = new HashMap<>();
    private final String[] nutritionistFacts;
    
    /**
     * Creates a new food dictionary with predefined food items and facts.
     */
    public FoodDictionary() {
        // Initialize healthy food dictionary
        healthyDict.put("apple.png", "Healthy Food!\nApple contain fiber and vitamin C, and they float because they are 25% air!");
        healthyDict.put("banana.png", "Healthy Food!\nBanana help boost energy and improve mood due to natural sugars and serotonin.");
        healthyDict.put("blueberry.png", "Healthy Food!\nBlueberries are rich in antioxidants that help improve brain function and memory.");
        healthyDict.put("carrot.png", "Healthy Food!\nCarrots are originally purple! The orange ones were created through selective breeding.");
        
        // Initialize unhealthy food dictionary
        unhealthyDict.put("icecream.png", "Unhealthy Food!\nMany commercial brands add artificial thickeners and extra sugar to Ice Cream.");
        unhealthyDict.put("friedchicken.png", "Unhealthy Food!\nDeep-fried foods like fried chicken absorb unhealthy fats that can clog arteries.");
        unhealthyDict.put("coke.png", "Unhealthy Food!\nOne can of coke can have 10 teaspoons of sugar, leading to energy crashes.");
        unhealthyDict.put("fries.png", "Unhealthy Food!\nThe average person eats 30 pounds of fries per year!");
        
        // Initialize nutritionist facts
        nutritionistFacts = new String[] {
            "Skipping breakfast = starting a race with no fuel!",
            "Water is the best drink! It keeps your brain fresh and your body happy.",
            "Your plate should look like a rainbow! More colors = more vitamins!",
            "The slower you eat, the faster you feel full! Try chewing slowly!",
            "Feeling tired? Instead of junk food, try nuts or fruit for natural energy!"
        };
    }
    
    /**
     * Gets a random healthy food and its fact.
     * 
     * @return Array with [textureName, fact]
     */
    public String[] getRandomHealthyFood() {
        List<String> keys = new ArrayList<>(healthyDict.keySet());
        String key = keys.get(random.nextInt(keys.size()));
        return new String[] { key, healthyDict.get(key) };
    }
    
    /**
     * Gets a random unhealthy food and its fact.
     * 
     * @return Array with [textureName, fact]
     */
    public String[] getRandomUnhealthyFood() {
        List<String> keys = new ArrayList<>(unhealthyDict.keySet());
        String key = keys.get(random.nextInt(keys.size()));
        return new String[] { key, unhealthyDict.get(key) };
    }
    
    /**
     * Gets a random nutritionist fact.
     * 
     * @return A random fact from the nutritionist
     */
    public String getRandomNutritionistFact() {
        return nutritionistFacts[random.nextInt(nutritionistFacts.length)];
    }
}