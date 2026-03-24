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
 */
public class MazeGenerator {

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

    private static final int[][] SPAWN_POSITIONS = {
        { COLS - 2, ROWS - 2 },
        { 1,        ROWS - 2 },
        { COLS - 2, 1 }
    };

    private final float cellSize;
    private float originX;
    private float originY;

    private final List<Wall> walls = new ArrayList<>();
    private final List<Floor> floors = new ArrayList<>();

    public MazeGenerator(float cellSize) {
        this.cellSize = cellSize;
    }

    public void generateMaze(Texture wallTexture, int screenW, int screenH) {
        computeOrigin(screenW, screenH);
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (LAYOUT[r][c] == 1) {
                    walls.add(new Wall(
                        "W_" + c + "_" + r,
                        originX + c * cellSize,
                        originY + r * cellSize,
                        wallTexture,
                        cellSize
                    ));
                }
            }
        }
    }

    public void generateFloor(Texture floorTexture, int screenW, int screenH) {
        computeOrigin(screenW, screenH);
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (LAYOUT[r][c] == 0) {
                    floors.add(new Floor(
                        "F_" + c + "_" + r,
                        originX + c * cellSize,
                        originY + r * cellSize,
                        floorTexture,
                        cellSize
                    ));
                }
            }
        }
    }

    public Vector2 gridToScreenTopLeft(int col, int row) {
        return new Vector2(originX + col * cellSize, originY + row * cellSize);
    }

    public static boolean isWall(int col, int row) {
        if (col < 0 || col >= COLS || row < 0 || row >= ROWS) {
            return true;
        }
        return LAYOUT[row][col] == 1;
    }

    public float clampX(float x, float entityWidth) {
        float minX = originX + cellSize;
        float maxX = originX + getMazeWidth() - cellSize - entityWidth;
        return Math.max(minX, Math.min(x, maxX));
    }

    public float clampY(float y, float entityHeight) {
        float minY = originY + cellSize;
        float maxY = originY + getMazeHeight() - cellSize - entityHeight;
        return Math.max(minY, Math.min(y, maxY));
    }

    public float getMazeWidth() {
        return COLS * cellSize;
    }

    public float getMazeHeight() {
        return ROWS * cellSize;
    }

    public List<Wall> getWalls() {
        return Collections.unmodifiableList(walls);
    }

    public List<Floor> getFloors() {
        return Collections.unmodifiableList(floors);
    }

    public float getCellSize() {
        return cellSize;
    }

    public static int getGridWidth() {
        return COLS;
    }

    public static int getGridHeight() {
        return ROWS;
    }

    public static int[][] getEnemySpawnPoints() {
        return SPAWN_POSITIONS;
    }

    private void computeOrigin(int screenW, int screenH) {
        originX = (screenW - COLS * cellSize) / 2f;
        originY = (screenH - ROWS * cellSize) / 2f;
    }
}
