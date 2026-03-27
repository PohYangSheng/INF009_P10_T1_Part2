package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Food;

// stores all the food data - textures, facts, tips
public class FoodRepository {

    public static final class FoodEntry {
        private final String textureName;
        private final String dietitianFact;

        public FoodEntry(String textureName, String dietitianFact) {
            this.textureName   = textureName;
            this.dietitianFact = dietitianFact;
        }

        // getter for texture name
        public String getTextureName() {
            return textureName;
        }
        // getter for dietitian fact
        public String getDietitianFact() {
            return dietitianFact;
        }

        // to array
        public String[] toArray() { return new String[]{ textureName, dietitianFact }; }
    }

    private final Map<Food.FoodType, List<FoodEntry>> catalogue =
        new EnumMap<>(Food.FoodType.class);

    private final List<String> dietitianTips = new ArrayList<>();
    private final Random       rng              = new Random();

    // constructor
    public FoodRepository() {
        List<FoodEntry> healthy   = new ArrayList<>();
        List<FoodEntry> unhealthy = new ArrayList<>();

        healthy.add(new FoodEntry("watermelon_pickup.png",
            "Power-Up: Watermelon!\nWatermelon is 92% water, making it one of the best fruits for staying hydrated on a hot day."));
        healthy.add(new FoodEntry("broccoli_pickup.png",
            "Power-Up: Broccoli!\nA single cup of broccoli has more vitamin C than an orange and strengthens your immune system."));
        healthy.add(new FoodEntry("orange_pickup.png",
            "Power-Up: Orange!\nOranges contain hesperidin, a compound that helps lower blood pressure and supports heart health."));
        healthy.add(new FoodEntry("grapes_pickup.png",
            "Power-Up: Grapes!\nGrapes are packed with antioxidants called polyphenols that protect your brain and improve memory."));

        unhealthy.add(new FoodEntry("donut_pickup.png",
            "Danger: Donut!\nA single glazed donut contains about 12 grams of sugar – nearly half the daily limit for children."));
        unhealthy.add(new FoodEntry("hotdog_pickup.png",
            "Danger: Hot Dog!\nProcessed meats like hot dogs contain nitrates linked to increased risk of heart disease."));
        unhealthy.add(new FoodEntry("pizza_pickup.png",
            "Danger: Pizza!\nA single slice of pepperoni pizza can contain over 600mg of sodium – a quarter of your daily limit."));
        unhealthy.add(new FoodEntry("candy_pickup.png",
            "Danger: Candy!\nSugary sweets cause rapid blood sugar spikes followed by energy crashes that hurt concentration."));

        catalogue.put(Food.FoodType.HEALTHY,   Collections.unmodifiableList(healthy));
        catalogue.put(Food.FoodType.UNHEALTHY, Collections.unmodifiableList(unhealthy));

        dietitianTips.add("Colourful plates are healthy plates – try to eat fruits and vegetables of at least three different colours each day!");
        dietitianTips.add("Your body is about 60% water. Drinking enough water helps your brain work faster and keeps your energy up.");
        dietitianTips.add("Eating breakfast kickstarts your metabolism and helps you focus better at school or work.");
        dietitianTips.add("Too much screen time during meals leads to overeating – put down your phone and enjoy your food mindfully!");
        dietitianTips.add("Reading nutrition labels is a superpower – check the sugar content before choosing a snack.");
    }

    // getter for all healthy entries
    public List<FoodEntry> getAllHealthyEntries() {
        return catalogue.get(Food.FoodType.HEALTHY);
    }

    // getter for all unhealthy entries
    public List<FoodEntry> getAllUnhealthyEntries() {
        return catalogue.get(Food.FoodType.UNHEALTHY);
    }

    // getter for random healthy entry
    public FoodEntry getRandomHealthyEntry() {
        return randomFrom(catalogue.get(Food.FoodType.HEALTHY));
    }

    // getter for random unhealthy entry
    public FoodEntry getRandomUnhealthyEntry() {
        return randomFrom(catalogue.get(Food.FoodType.UNHEALTHY));
    }

    // getter for healthy food item
    public String[] getHealthyFoodItem() {
        return getRandomHealthyEntry().toArray();
    }

    // getter for unhealthy food item
    public String[] getUnhealthyFoodItem() {
        return getRandomUnhealthyEntry().toArray();
    }

    // getter for dietitian tip
    public String getDietitianTip() {
        return dietitianTips.get(rng.nextInt(dietitianTips.size()));
    }

    // pick a random item from the list
    private <T> T randomFrom(List<T> list) {
        if (list == null || list.isEmpty())
            throw new IllegalStateException("FoodRepository has no entries for requested type");
        return list.get(rng.nextInt(list.size()));
    }
}
