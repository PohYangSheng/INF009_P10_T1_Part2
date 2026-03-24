package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Food;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.HealthyFood;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.UnhealthyFood;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manages the lifecycle of all food items in the Munch Maze play field.
 *
 * Responsibilities (SRP):
 *   1. Pre-load food textures once at construction.
 *   2. Spawn new food items at timed intervals onto valid maze cells.
 *   3. Track active items and prune those that have expired or been eaten.
 *
 * OOP:
 *   Encapsulation – texture caches and spawn state are private.
 *   Polymorphism  – produces Food references; callers treat HealthyFood and
 *                   UnhealthyFood uniformly through the Food base type.
 *   Generics      – EnumMap<Food.FoodType, Map<String,Texture>> enforces
 *                   type-safe texture lookup without magic strings.
 *
 * SOLID:
 *   SRP  – only manages food; collision and rendering are elsewhere.
 *   OCP  – adding a new food type requires adding an entry in FoodDictionary
 *          and a texture file; no logic changes here.
 *   DIP  – depends on Food (abstract), MazeGenerator and FoodDictionary;
 *          no coupling to PlayScene internals.
 *
 * Design Pattern:
 *   Factory Method – trySpawnFood() decides which concrete Food subclass to
 *   create at runtime based on HEALTHY_SPAWN_RATE.
 */
public class FoodGenerator {

    // ── Tuning constants ──────────────────────────────────────────────────

    /** Fraction of spawned items that will be healthy (rest are unhealthy). */
    private static final float HEALTHY_SPAWN_RATE  = 0.65f;

    /** Seconds between automatic spawns during gameplay. */
    private static final float SPAWN_COOLDOWN_SECS = 3.5f;

    /** Upper bound on simultaneous food items in the maze. */
    private static final int   FOOD_CAPACITY       = 10;

    /** Seconds a food item remains before expiring if uncollected. */
    private static final float ITEM_LIFESPAN_SECS  = 12.0f;

    /** How many grid cells to inset food from the raw cell corner. */
    private static final float CELL_PADDING_RATIO  = 0.15f;

    /** Maximum random placement attempts before giving up on a spawn cycle. */
    private static final int   PLACEMENT_TRIES     = 50;

    /** Size of each food sprite relative to its grid cell. */
    private static final float SPRITE_SCALE        = 0.75f;

    private static final int HEALTH_BONUS_MIN = 4;
    private static final int HEALTH_BONUS_MAX = 18;
    private static final int DAMAGE_MIN       = 4;
    private static final int DAMAGE_MAX       = 18;

    // ── State ─────────────────────────────────────────────────────────────

    private final List<Food> activeFoodItems = new ArrayList<>();

    /**
     * Texture cache: Food.FoodType → asset filename → Texture.
     * EnumMap gives O(1) lookup and compile-time type safety.
     */
    private final Map<Food.FoodType, Map<String, Texture>> textureCache =
            new EnumMap<>(Food.FoodType.class);

    private final float          cellSize;
    private final MazeGenerator  mazeGenerator;
    private final FoodDictionary foodDictionary;
    private       float          spawnAccumulator = 0f;

    // ── Construction ──────────────────────────────────────────────────────

    public FoodGenerator(float cellSize,
                         MazeGenerator mazeGenerator,
                         FoodDictionary foodDictionary) {
        this.cellSize       = cellSize;
        this.mazeGenerator  = mazeGenerator;
        this.foodDictionary = foodDictionary;
        preloadTextures();
    }

    // ── Texture loading ───────────────────────────────────────────────────

    private void preloadTextures() {
        Map<String, Texture> healthyCache   = new java.util.LinkedHashMap<>();
        Map<String, Texture> unhealthyCache = new java.util.LinkedHashMap<>();

        String[] healthyAssets   = { "watermelon.png", "broccoli.png", "orange.png", "grapes.png" };
        String[] unhealthyAssets = { "donut.png", "hotdog.png", "pizza.png", "candy.png" };

        for (String asset : healthyAssets)
            healthyCache.put(asset,
                    new Texture(Gdx.files.internal("entities_images/" + asset)));
        for (String asset : unhealthyAssets)
            unhealthyCache.put(asset,
                    new Texture(Gdx.files.internal("entities_images/" + asset)));

        textureCache.put(Food.FoodType.HEALTHY,   Collections.unmodifiableMap(healthyCache));
        textureCache.put(Food.FoodType.UNHEALTHY, Collections.unmodifiableMap(unhealthyCache));
    }

    // ── Per-frame update ──────────────────────────────────────────────────

    /**
     * Prunes consumed/expired items then spawns a new one when the cooldown
     * has elapsed and the field is below capacity.
     *
     * NOTE: does NOT call food.update(dt) — EntityManager is the sole authority
     * for updating entity state each frame. Calling update here would cause
     * food to age at twice the intended rate.
     *
     * @param dt seconds since last frame
     * @return list of newly created Food items to register with EntityManager
     */
    public List<Food> update(float dt) {
        pruneInactiveItems();

        List<Food> freshItems = new ArrayList<>();
        spawnAccumulator += dt;

        if (spawnAccumulator >= SPAWN_COOLDOWN_SECS
                && activeFoodItems.size() < FOOD_CAPACITY) {
            Food spawned = trySpawnFood();
            if (spawned != null) {
                activeFoodItems.add(spawned);
                freshItems.add(spawned);
            }
            spawnAccumulator = 0f;
        }
        return freshItems;
    }

    /**
     * Spawns a fixed number of items at scene start.
     *
     * @param count desired number of items
     * @return list of created Food items
     */
    public List<Food> generateFood(int count) {
        List<Food> batch = new ArrayList<>();
        int tries = 0;
        while (batch.size() < count && tries++ < PLACEMENT_TRIES * count) {
            Food item = trySpawnFood();
            if (item != null) {
                activeFoodItems.add(item);
                batch.add(item);
            }
        }
        return batch;
    }

    // ── Internal factory ──────────────────────────────────────────────────

    /**
     * Attempts to place a single food item at a random open cell.
     * Returns null if no valid position found within PLACEMENT_TRIES attempts.
     *
     * Design Pattern: Factory Method — decides at runtime whether to create
     * a HealthyFood or UnhealthyFood.
     */
    private Food trySpawnFood() {
        for (int t = 0; t < PLACEMENT_TRIES; t++) {
            int col = MathUtils.random(1, MazeGenerator.getGridWidth()  - 2);
            int row = MathUtils.random(1, MazeGenerator.getGridHeight() - 2);
            if (MazeGenerator.isWall(col, row)) continue;

            Vector2 origin  = mazeGenerator.gridToScreenTopLeft(col, row);
            float   padding = cellSize * CELL_PADDING_RATIO;
            float   px      = origin.x + padding;
            float   py      = origin.y + padding;
            float   spriteW = cellSize * SPRITE_SCALE;

            if (isCellOccupied(px, py, spriteW)) continue;

            if (MathUtils.random() < HEALTHY_SPAWN_RATE) {
                String[] entry = foodDictionary.getRandomHealthyFood();
                Texture  tex   = textureCache.get(Food.FoodType.HEALTHY).get(entry[0]);
                int      bonus = MathUtils.random(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);
                return new HealthyFood("HF_" + System.currentTimeMillis(),
                        px, py, tex, bonus, ITEM_LIFESPAN_SECS, entry[1], spriteW);
            } else {
                String[] entry  = foodDictionary.getRandomUnhealthyFood();
                Texture  tex    = textureCache.get(Food.FoodType.UNHEALTHY).get(entry[0]);
                int      damage = MathUtils.random(DAMAGE_MIN, DAMAGE_MAX);
                return new UnhealthyFood("UF_" + System.currentTimeMillis(),
                        px, py, tex, damage, ITEM_LIFESPAN_SECS, entry[1], spriteW);
            }
        }
        return null;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private boolean isCellOccupied(float px, float py, float size) {
        for (Food existing : activeFoodItems) {
            if (existing.shouldBeRemoved()) continue;
            com.badlogic.gdx.math.Rectangle zone =
                    new com.badlogic.gdx.math.Rectangle(px, py, size, size);
            if (existing.getBounds().overlaps(zone)) return true;
        }
        return false;
    }

    private void pruneInactiveItems() {
        Iterator<Food> it = activeFoodItems.iterator();
        while (it.hasNext()) {
            Food f = it.next();
            if (f.shouldBeRemoved() || f.isExpired()) it.remove();
        }
    }

    // ── Accessors ─────────────────────────────────────────────────────────

    public int getFoodListSize() { return activeFoodItems.size(); }

    // ── Lifecycle ─────────────────────────────────────────────────────────

    public void dispose() {
        for (Map<String, Texture> cache : textureCache.values())
            cache.values().forEach(Texture::dispose);
        textureCache.clear();
    }
}
