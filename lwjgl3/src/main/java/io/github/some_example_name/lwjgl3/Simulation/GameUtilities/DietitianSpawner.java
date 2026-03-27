package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Dietitian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// controls when/where the dietitian spawns
public class DietitianSpawner {

    private static final float RESPAWN_DELAY_SECS  = 6.0f;

    private static final int   MAX_PLACEMENT_TRIES = 80;

    private static final float OCCUPANCY_RATIO = 0.9f;

    private final float          cellSize;
    private final MazeBuilder  mazeBuilder;
    private final FoodRepository foodRepository;

    private float   countdown    = RESPAWN_DELAY_SECS;
    private boolean readyToSpawn = false;

    // constructor
    public DietitianSpawner(float cellSize,
                                  MazeBuilder mazeBuilder,
                                  FoodRepository foodRepository) {
        this.cellSize       = cellSize;
        this.mazeBuilder  = mazeBuilder;
        this.foodRepository = foodRepository;
    }

    // runs every frame
    public boolean update(float dt, boolean dietitianPresent) {
        if (dietitianPresent) {
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

    // spawn a dietitian on an open cell
    public Dietitian spawnDietitian(Texture texture, float speed) {
        readyToSpawn = false;
        List<int[]> openCells = collectOpenCells();
        if (openCells.isEmpty()) return null;

        Collections.shuffle(openCells, new Random());

        int tries = Math.min(MAX_PLACEMENT_TRIES, openCells.size());
        for (int i = 0; i < tries; i++) {
            int[]   cell    = openCells.get(i);
            Vector2 origin  = mazeBuilder.getScreenPosition(cell[0], cell[1]);
            float   padding = cellSize * ((1f - OCCUPANCY_RATIO) / 2f);
            float   px      = origin.x + padding;
            float   py      = origin.y + padding;
            float   size    = cellSize * OCCUPANCY_RATIO;
            String  tip     = foodRepository.getDietitianTip();

            return new Dietitian("Dietitian_" + System.currentTimeMillis(),
                    px, py, texture, speed, size, tip);
        }
        return null;
    }

    // find all open cells we can spawn on
    private List<int[]> collectOpenCells() {
        List<int[]> cells = new ArrayList<>();
        int cols = MazeBuilder.getColCount();
        int rows = MazeBuilder.getRowCount();
        for (int r = 1; r < rows - 1; r++)
            for (int c = 1; c < cols - 1; c++)
                if (!MazeBuilder.isWall(c, r))
                    cells.add(new int[]{ c, r });
        return cells;
    }
}
