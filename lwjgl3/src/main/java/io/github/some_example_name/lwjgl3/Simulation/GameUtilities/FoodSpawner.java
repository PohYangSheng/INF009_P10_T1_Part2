package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Food;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// handles spawning and removing food in the maze
public class FoodSpawner {

    private static final float HEALTHY_SPAWN_RATE  = 0.65f;

    private static final float SPAWN_COOLDOWN_SECS = 3.5f;

    private static final int   FOOD_CAPACITY       = 10;

    private static final float ITEM_LIFESPAN_SECS  = 12.0f;

    private static final float CELL_PADDING_RATIO  = 0.15f;

    private static final int   PLACEMENT_TRIES     = 50;

    private static final float SPRITE_SCALE        = 0.75f;

    private final List<Food> activeFoodItems = new ArrayList<>();

    private final Map<Food.FoodType, Map<String, Texture>> textureCache =
            new EnumMap<>(Food.FoodType.class);

    private final float          cellSize;
    private final MazeBuilder    mazeBuilder;
    private final FoodRepository foodRepository;
    private       float          spawnAccumulator = 0f;

    private IFoodFactory healthyFactory;
    private IFoodFactory unhealthyFactory;

    // constructor
    public FoodSpawner(float cellSize,
                       MazeBuilder mazeBuilder,
                       FoodRepository foodRepository) {
        this.cellSize       = cellSize;
        this.mazeBuilder  = mazeBuilder;
        this.foodRepository = foodRepository;
        preloadTextures();
        
        healthyFactory   = new HealthyFoodFactory(textureCache.get(Food.FoodType.HEALTHY));
        unhealthyFactory = new UnhealthyFoodFactory(textureCache.get(Food.FoodType.UNHEALTHY));
    }

    // load all food textures at start
    private void preloadTextures() {
        Map<String, Texture> healthyCache   = new java.util.LinkedHashMap<>();
        Map<String, Texture> unhealthyCache = new java.util.LinkedHashMap<>();

        for (FoodRepository.FoodEntry e : foodRepository.getAllHealthyEntries())
            healthyCache.put(e.getTextureName(),
                new Texture(Gdx.files.internal("sprite_assets/" + e.getTextureName())));

        for (FoodRepository.FoodEntry e : foodRepository.getAllUnhealthyEntries())
            unhealthyCache.put(e.getTextureName(),
                new Texture(Gdx.files.internal("sprite_assets/" + e.getTextureName())));

        textureCache.put(Food.FoodType.HEALTHY,   Collections.unmodifiableMap(healthyCache));
        textureCache.put(Food.FoodType.UNHEALTHY, Collections.unmodifiableMap(unhealthyCache));
    }

    // tick the spawn timer and spawn food if ready
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

    // spawn a batch of food items
    public List<Food> spawnFood(int count) {
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

    // try to place one food item on a random open cell
    private Food trySpawnFood() {
        for (int t = 0; t < PLACEMENT_TRIES; t++) {
            int col = MathUtils.random(1, MazeBuilder.getColCount()  - 2);
            int row = MathUtils.random(1, MazeBuilder.getRowCount() - 2);
            if (MazeBuilder.isWall(col, row)) continue;

            Vector2 origin  = mazeBuilder.getScreenPosition(col, row);
            float   padding = cellSize * CELL_PADDING_RATIO;
            float   px      = origin.x + padding;
            float   py      = origin.y + padding;
            float   spriteW = cellSize * SPRITE_SCALE;

            if (isCellOccupied(px, py, spriteW)) continue;

            boolean spawnHealthy = MathUtils.random() < HEALTHY_SPAWN_RATE;
            IFoodFactory factory  = spawnHealthy ? healthyFactory : unhealthyFactory;
            String[]     entry    = spawnHealthy
                    ? foodRepository.getHealthyFoodItem()
                    : foodRepository.getUnhealthyFoodItem();

            return factory.create("F_" + System.currentTimeMillis(),
                    px, py, spriteW, ITEM_LIFESPAN_SECS, entry);
        }
        return null;
    }

    // check if the target maze cell already contains an active food item
    private boolean isCellOccupied(float px, float py, float size) {
        for (Food existing : activeFoodItems) {
            if (existing.isPendingRemoval()) continue;
            com.badlogic.gdx.math.Rectangle zone =
                    new com.badlogic.gdx.math.Rectangle(px, py, size, size);
            if (existing.getBounds().overlaps(zone)) return true;
        }
        return false;
    }

    // remove expired/collected food from the list
    private void pruneInactiveItems() {
        Iterator<Food> it = activeFoodItems.iterator();
        while (it.hasNext()) {
            Food f = it.next();
            if (f.isPendingRemoval() || f.hasExpired()) it.remove();
        }
    }

    // getter for food count
    public int getFoodCount() {
        return activeFoodItems.size();
    }

    // clean up textures/resources so we dont leak memory
    public void dispose() {
        for (Map<String, Texture> cache : textureCache.values())
            cache.values().forEach(Texture::dispose);
        textureCache.clear();
    }
}
