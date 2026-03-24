package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Food;

/**
 * Repository of food knowledge for Munch Maze.
 *
 * Stores nutrition facts keyed by food category (HEALTHY / UNHEALTHY)
 * and provides random selection for spawning and popup display.
 *
 * OOP:
 *   Encapsulation  – all data is private; access is only through typed methods.
 *   Abstraction    – callers ask for "a random healthy food" without knowing
 *                    how entries are stored or shuffled.
 *   Inner class    – FoodEntry is a value object that logically belongs here.
 *
 * SOLID:
 *   SRP – one responsibility: own and serve food knowledge data.
 *   OCP – add new foods by appending to the initialiser lists; no method changes.
 *
 * No libGDX dependencies – can be unit-tested without a running application.
 */
public class FoodDictionary {

    /**
     * Immutable value object pairing a texture asset name with a nutrition fact.
     *
     * OOP: Inner class – logically belongs to FoodDictionary.
     * Encapsulation: fields are final and accessible only via getters.
     */
    public static final class FoodEntry {
        private final String textureName;
        private final String nutritionFact;

        public FoodEntry(String textureName, String nutritionFact) {
            this.textureName   = textureName;
            this.nutritionFact = nutritionFact;
        }

        public String getTextureName()   { return textureName;   }
        public String getNutritionFact() { return nutritionFact; }

        /** Convenience: returns the pair as a String[] for legacy callers. */
        public String[] toArray() { return new String[]{ textureName, nutritionFact }; }
    }

    // ── Data ──────────────────────────────────────────────────────────────

    private final Map<Food.FoodType, List<FoodEntry>> catalogue =
        new EnumMap<>(Food.FoodType.class);

    private final List<String> nutritionistTips = new ArrayList<>();
    private final Random       rng              = new Random();

    // ── Construction ──────────────────────────────────────────────────────

    public FoodDictionary() {
        List<FoodEntry> healthy   = new ArrayList<>();
        List<FoodEntry> unhealthy = new ArrayList<>();

        // ── Healthy entries ───────────────────────────────────────────────
        healthy.add(new FoodEntry("watermelon.png",
            "Power-Up: Watermelon!\nWatermelon is 92% water, making it one of the best fruits for staying hydrated on a hot day."));
        healthy.add(new FoodEntry("broccoli.png",
            "Power-Up: Broccoli!\nA single cup of broccoli has more vitamin C than an orange and strengthens your immune system."));
        healthy.add(new FoodEntry("orange.png",
            "Power-Up: Orange!\nOranges contain hesperidin, a compound that helps lower blood pressure and supports heart health."));
        healthy.add(new FoodEntry("grapes.png",
            "Power-Up: Grapes!\nGrapes are packed with antioxidants called polyphenols that protect your brain and improve memory."));

        // ── Unhealthy entries ─────────────────────────────────────────────
        unhealthy.add(new FoodEntry("donut.png",
            "Danger: Donut!\nA single glazed donut contains about 12 grams of sugar – nearly half the daily limit for children."));
        unhealthy.add(new FoodEntry("hotdog.png",
            "Danger: Hot Dog!\nProcessed meats like hot dogs contain nitrates linked to increased risk of heart disease."));
        unhealthy.add(new FoodEntry("pizza.png",
            "Danger: Pizza!\nA single slice of pepperoni pizza can contain over 600mg of sodium – a quarter of your daily limit."));
        unhealthy.add(new FoodEntry("candy.png",
            "Danger: Candy!\nSugary sweets cause rapid blood sugar spikes followed by energy crashes that hurt concentration."));

        catalogue.put(Food.FoodType.HEALTHY,   Collections.unmodifiableList(healthy));
        catalogue.put(Food.FoodType.UNHEALTHY, Collections.unmodifiableList(unhealthy));

        // ── Nutritionist tips ─────────────────────────────────────────────
        nutritionistTips.add("Colourful plates are healthy plates – try to eat fruits and vegetables of at least three different colours each day!");
        nutritionistTips.add("Your body is about 60% water. Drinking enough water helps your brain work faster and keeps your energy up.");
        nutritionistTips.add("Eating breakfast kickstarts your metabolism and helps you focus better at school or work.");
        nutritionistTips.add("Too much screen time during meals leads to overeating – put down your phone and enjoy your food mindfully!");
        nutritionistTips.add("Reading nutrition labels is a superpower – check the sugar content before choosing a snack.");
    }

    // ── Public API ────────────────────────────────────────────────────────

    /** @return random healthy FoodEntry */
    public FoodEntry getRandomHealthyEntry()   { return randomFrom(catalogue.get(Food.FoodType.HEALTHY));   }

    /** @return random unhealthy FoodEntry */
    public FoodEntry getRandomUnhealthyEntry() { return randomFrom(catalogue.get(Food.FoodType.UNHEALTHY)); }

    /** Legacy adapter – returns String[]{ textureName, fact } */
    public String[] getRandomHealthyFood()   { return getRandomHealthyEntry().toArray();   }

    /** Legacy adapter – returns String[]{ textureName, fact } */
    public String[] getRandomUnhealthyFood() { return getRandomUnhealthyEntry().toArray(); }

    /**
     * Returns a random nutritionist tip.
     * Method kept as getRandomNutritionistFact() for backward compatibility
     * with any existing callers.
     */
    public String getRandomNutritionistFact() {
        return nutritionistTips.get(rng.nextInt(nutritionistTips.size()));
    }

    /**
     * Returns all entries for the given food type.
     * OOP Generics: typed list, not raw Object[].
     */
    public List<FoodEntry> getAllEntries(Food.FoodType type) {
        return catalogue.getOrDefault(type, Collections.emptyList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private <T> T randomFrom(List<T> list) {
        if (list == null || list.isEmpty())
            throw new IllegalStateException("FoodDictionary has no entries for requested type");
        return list.get(rng.nextInt(list.size()));
    }
}
