package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Nutritionist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Controls the timed appearance of the Nutritionist NPC in Munch Maze.
 *
 * The Nutritionist roams open corridors and rewards the player with health
 * and a nutrition tip on contact. This generator tracks the countdown
 * until the next spawn and places the entity at a random open cell.
 *
 * OOP:
 *   Encapsulation – spawn countdown and placement state are private.
 *   Abstraction   – callers use two clean methods: update() and
 *                   generateNutritionist(); no internal timing details leak.
 *
 * SOLID:
 *   SRP  – only responsible for deciding WHEN and WHERE to spawn; no movement
 *          or collision logic is present here.
 *   OCP  – changing the placement algorithm (e.g. preferring cells near the
 *          player) only requires modifying collectOpenCells(); callers unchanged.
 *   DIP  – depends on MazeGenerator and FoodDictionary, not on any scene type.
 */
public class NutritionistGenerator {

    // ── Configuration ─────────────────────────────────────────────────────

    /** Minimum seconds between consecutive nutritionist spawns. */
    private static final float RESPAWN_DELAY_SECS  = 6.0f;

    /** Maximum placement attempts per spawn cycle before giving up. */
    private static final int   MAX_PLACEMENT_TRIES = 80;

    /**
     * Fraction of the cell the nutritionist occupies.
     * Kept slightly below 1 so the sprite clears adjacent walls.
     */
    private static final float OCCUPANCY_RATIO = 0.9f;

    // ── Dependencies ──────────────────────────────────────────────────────

    private final float          cellSize;
    private final MazeGenerator  mazeGenerator;
    private final FoodDictionary foodDictionary;

    // ── State ─────────────────────────────────────────────────────────────

    private float   countdown    = RESPAWN_DELAY_SECS;
    private boolean readyToSpawn = false;

    // ── Construction ──────────────────────────────────────────────────────

    public NutritionistGenerator(float cellSize,
                                  MazeGenerator mazeGenerator,
                                  FoodDictionary foodDictionary) {
        this.cellSize       = cellSize;
        this.mazeGenerator  = mazeGenerator;
        this.foodDictionary = foodDictionary;
    }

    // ── Per-frame update ──────────────────────────────────────────────────

    /**
     * Advances the spawn countdown.
     *
     * Returns true exactly once per cooldown period when no nutritionist
     * is currently on the field — the signal for PlayScene to call
     * generateNutritionist().
     *
     * @param dt                  seconds since last frame
     * @param nutritionistPresent whether a Nutritionist entity currently exists
     * @return true if a new nutritionist should be spawned this frame
     */
    public boolean update(float dt, boolean nutritionistPresent) {
        if (nutritionistPresent) {
            countdown    = RESPAWN_DELAY_SECS;
            readyToSpawn = false;
            return false;
        }

        countdown -= dt;
        if (countdown <= 0f && !readyToSpawn) {
            readyToSpawn = true;
            countdown    = RESPAWN_DELAY_SECS;
            return true;
        }
        return false;
    }

    // ── Spawning ──────────────────────────────────────────────────────────

    /**
     * Creates a Nutritionist at a randomly chosen open maze cell.
     *
     * Collects all valid corridor cells, shuffles them, then picks the first
     * that fits — giving a uniform distribution across the whole maze.
     *
     * @param texture sprite texture for the nutritionist
     * @param speed   movement speed in pixels per second
     * @return a new Nutritionist ready to be added to EntityManager, or null
     *         if no valid position was found
     */
    public Nutritionist generateNutritionist(Texture texture, float speed) {
        readyToSpawn = false;
        List<int[]> openCells = collectOpenCells();
        if (openCells.isEmpty()) return null;

        Collections.shuffle(openCells, new Random());

        int tries = Math.min(MAX_PLACEMENT_TRIES, openCells.size());
        for (int i = 0; i < tries; i++) {
            int[]   cell    = openCells.get(i);
            Vector2 origin  = mazeGenerator.gridToScreenTopLeft(cell[0], cell[1]);
            float   padding = cellSize * ((1f - OCCUPANCY_RATIO) / 2f);
            float   px      = origin.x + padding;
            float   py      = origin.y + padding;
            float   size    = cellSize * OCCUPANCY_RATIO;
            String  tip     = foodDictionary.getRandomNutritionistFact();

            return new Nutritionist("Nutritionist_" + System.currentTimeMillis(),
                    px, py, texture, speed, size, tip);
        }
        return null;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Collects every open (non-wall) grid cell as a candidate spawn point.
     * Building the full list and shuffling gives uniform random placement.
     */
    private List<int[]> collectOpenCells() {
        List<int[]> cells = new ArrayList<>();
        int cols = MazeGenerator.getGridWidth();
        int rows = MazeGenerator.getGridHeight();
        for (int r = 1; r < rows - 1; r++)
            for (int c = 1; c < cols - 1; c++)
                if (!MazeGenerator.isWall(c, r))
                    cells.add(new int[]{ c, r });
        return cells;
    }
}
