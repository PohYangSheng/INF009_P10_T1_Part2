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
        healthy.add(new FoodEntry("apple.png",
            "Power-Up: Apple!\nEating apples regularly can lower the risk of type 2 diabetes by up to 28%!"));
        healthy.add(new FoodEntry("banana.png",
            "Power-Up: Banana!\nBananas contain tryptophan, which the body converts into mood-boosting serotonin."));
        healthy.add(new FoodEntry("blueberry.png",
            "Power-Up: Blueberry!\nJust one cup of blueberries gives you 24% of your daily vitamin C needs."));
        healthy.add(new FoodEntry("carrot.png",
            "Power-Up: Carrot!\nThe beta-carotene in carrots is converted into vitamin A, which protects your eyesight."));

        // ── Unhealthy entries ─────────────────────────────────────────────
        unhealthy.add(new FoodEntry("icecream.png",
            "Danger: Ice Cream!\nA single large scoop can pack over 300 calories – nearly a full meal for a child."));
        unhealthy.add(new FoodEntry("friedchicken.png",
            "Danger: Fried Chicken!\nDeep frying doubles the fat content of chicken compared to grilling it."));
        unhealthy.add(new FoodEntry("coke.png",
            "Danger: Soda!\nRegular soda drinkers have a 26% higher risk of developing type 2 diabetes."));
        unhealthy.add(new FoodEntry("fries.png",
            "Danger: Fries!\nTrans-fats in deep-fried foods raise bad cholesterol and lower good cholesterol simultaneously."));

        catalogue.put(Food.FoodType.HEALTHY,   Collections.unmodifiableList(healthy));
        catalogue.put(Food.FoodType.UNHEALTHY, Collections.unmodifiableList(unhealthy));

        // ── Nutritionist tips ─────────────────────────────────────────────
        nutritionistTips.add("Eating slowly gives your brain 20 minutes to register fullness – chew more, eat less!");
        nutritionistTips.add("Half your plate should be vegetables and fruit at every meal.");
        nutritionistTips.add("Staying hydrated improves concentration and reaction time – drink water before you feel thirsty!");
        nutritionistTips.add("Whole grains release energy slowly, keeping you alert without the sugar crash.");
        nutritionistTips.add("Protein at breakfast reduces hunger hormones for the rest of the day.");
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
