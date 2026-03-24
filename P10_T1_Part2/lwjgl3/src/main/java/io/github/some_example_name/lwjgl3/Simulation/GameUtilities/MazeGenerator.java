package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Floor;
import io.github.some_example_name.lwjgl3.Simulation.GameEntities.Wall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builds and owns the static maze structure for Munch Maze.
 *
 * The maze is represented as a 2-D integer grid where {@code 1} = wall and
 * {@code 0} = open corridor. This class converts that grid into libGDX entity
 * objects ({@link Wall}, {@link Floor}) positioned and centred on the screen.
 *
 * OOP:
 *   Encapsulation  – the raw layout array is private; the rest of the game
 *                    accesses maze data only through typed, named methods.
 *   Abstraction    – callers ask for "all walls" or "grid cell → screen pos"
 *                    without knowing how the grid is stored or centred.
 *
 * SOLID:
 *   SRP – one responsibility: own the maze layout and expose it as entity
 *         lists and coordinate-conversion helpers.
 *   OCP – swapping to a different maze design only requires editing LAYOUT;
 *         no method signatures change.
 *
 * Scalability:
 *   Enemy spawn points are defined as data in SPAWN_POSITIONS.
 *   Adding a new enemy = adding one row to that array; PlayScene never changes.
 */
public class MazeGenerator {

    // ── Layout ────────────────────────────────────────────────────────────

    /**
     * Munch Maze corridor layout (1 = wall, 0 = open corridor).
     * All border cells are walls so entities can never leave the play field.
     */
    private static final int[][] LAYOUT = {
        { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 },
        { 1,0,0,0,1,0,0,0,0,0,1,0,0,0,1 },
        { 1,0,1,0,1,0,1,1,1,0,1,0,1,0,1 },
        { 1,0,1,0,0,0,0,0,0,0,0,0,1,0,1 },
        { 1,0,1,1,1,0,1,0,1,0,1,1,1,0,1 },
        { 1,0,0,0,0,0,1,0,1,0,0,0,0,0,1 },
        { 1,1,1,0,1,0,1,0,1,0,1,0,1,1,1 },
        { 1,0,0,0,1,0,0,0,0,0,1,0,0,0,1 },
        { 1,0,1,0,1,1,1,0,1,1,1,0,1,0,1 },
        { 1,0,1,0,0,0,0,0,0,0,0,0,1,0,1 },
        { 1,0,1,1,1,0,1,0,1,0,1,1,1,0,1 },
        { 1,0,0,0,0,0,1,0,1,0,0,0,0,0,1 },
        { 1,0,1,0,1,0,1,0,1,0,1,0,1,0,1 },
        { 1,0,0,0,0,0,0,0,0,0,0,0,0,0,1 },
        { 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1 }
    };

    private static final int COLS = 15;
    private static final int ROWS = 15;

    /**
     * Data-driven enemy spawn positions { col, row }.
     * Adding a new enemy = append one row here; PlayScene loops this array
     * and never needs to be edited.
     *
     * Scalability: O(1) effort to add more enemies.
     */
    private static final int[][] SPAWN_POSITIONS = {
        { COLS - 2, ROWS - 2 },
        {         1, ROWS - 2 },
        { COLS - 2,          1 },
    };

    // ── Instance state ────────────────────────────────────────────────────

    private final float      cellSize;
    private       float      originX;
    private       float      originY;

    private final List<Wall>  walls  = new ArrayList<>();
    private final List<Floor> floors = new ArrayList<>();

    // ── Construction ──────────────────────────────────────────────────────

    public MazeGenerator(float cellSize) { this.cellSize = cellSize; }

    // ── Generation ────────────────────────────────────────────────────────

    /**
     * Instantiates Wall entities for every cell marked 1 in LAYOUT.
     * Legacy alias: generateMaze() is kept so PlayScene compiles unchanged.
     */
    public void generateMaze(Texture wallTexture, int screenW, int screenH) {
        computeOrigin(screenW, screenH);
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (LAYOUT[r][c] == 1)
                    walls.add(new Wall("W_" + c + "_" + r,
                            originX + c * cellSize,
                            originY + r * cellSize,
                            wallTexture, cellSize));
    }

    /**
     * Instantiates Floor entities for every cell marked 0 in LAYOUT.
     * Legacy alias: generateFloor() is kept so PlayScene compiles unchanged.
     */
    public void generateFloor(Texture floorTexture, int screenW, int screenH) {
        computeOrigin(screenW, screenH);
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (LAYOUT[r][c] == 0)
                    floors.add(new Floor("F_" + c + "_" + r,
                            originX + c * cellSize,
                            originY + r * cellSize,
                            floorTexture, cellSize));
    }

    // ── Coordinate helpers ────────────────────────────────────────────────

    /**
     * Converts a grid cell to the screen coordinate of its top-left corner.
     *
     * @param col grid column (0-indexed from left)
     * @param row grid row    (0-indexed from bottom)
     * @return screen position as a libGDX Vector2
     */
    public Vector2 gridToScreenTopLeft(int col, int row) {
        return new Vector2(originX + col * cellSize, originY + row * cellSize);
    }

    /**
     * Returns true when the given grid cell is a wall or out of bounds.
     *
     * @param col grid column
     * @param row grid row
     * @return true if the cell is a wall
     */
    public static boolean isWall(int col, int row) {
        if (col < 0 || col >= COLS || row < 0 || row >= ROWS) return true;
        return LAYOUT[row][col] == 1;
    }

    // ── Accessors ─────────────────────────────────────────────────────────

    public List<Wall>     getWalls()            { return Collections.unmodifiableList(walls);  }
    public List<Floor>    getFloors()           { return Collections.unmodifiableList(floors); }
    public float          getCellSize()         { return cellSize;  }
    public static int     getGridWidth()        { return COLS;      }
    public static int     getGridHeight()       { return ROWS;      }
    public static int[][] getEnemySpawnPoints() { return SPAWN_POSITIONS; }

    // ── Helpers ───────────────────────────────────────────────────────────

    /** Calculates pixel origin so the maze is centred on the screen. */
    private void computeOrigin(int screenW, int screenH) {
        originX = (screenW - COLS * cellSize) / 2f;
        originY = (screenH - ROWS * cellSize) / 2f;
    }
}
