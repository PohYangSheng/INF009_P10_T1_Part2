package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.some_example_name.lwjgl3.Abstract_Engine.IOManager.IOManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// lets you navigate menus with keyboard (wasd/arrows + enter)
public class MenuKeyboardNavigator {

    private final IOManager ioManager;
    private final List<MenuItem> items = new ArrayList<>();
    private final Color selectedColor;
    private final Color unselectedColor;
    private int selectedIndex = -1;

    // constructor
    public MenuKeyboardNavigator(IOManager ioManager) {
        this(ioManager, MenuStyleConfig.getInstance().getSelectedColor(), MenuStyleConfig.getInstance().getUnselectedColor());
    }

    // constructor
    public MenuKeyboardNavigator(IOManager ioManager, Color selectedColor, Color unselectedColor) {
        this.ioManager = ioManager;
        this.selectedColor = new Color(selectedColor);
        this.unselectedColor = new Color(unselectedColor);
    }

    // register a menu item for keyboard nav
    public void register(Actor actor, int row, int col, Runnable onActivate) {
        register(actor, row, col, null, onActivate);
    }

    // register a menu item for keyboard nav
    public void register(Actor actor, int row, int col, Runnable onFocus, Runnable onActivate) {
        items.add(new MenuItem(actor, row, col, onFocus, onActivate));
        if (selectedIndex == -1) {
            selectIndex(0);
        } else {
            applyVisualState(items.size() - 1, false);
        }
    }

    // check for nav key presses
    public void update() {
        if (items.isEmpty()) {
            return;
        }
        for (int key : ioManager.getKeyboard().getKeysPressedThisFrame()) {
            switch (key) {
                case Input.Keys.W:
                case Input.Keys.UP:
                    moveVertical(-1);
                    break;
                case Input.Keys.S:
                case Input.Keys.DOWN:
                    moveVertical(1);
                    break;
                case Input.Keys.A:
                case Input.Keys.LEFT:
                    moveHorizontal(-1);
                    break;
                case Input.Keys.D:
                case Input.Keys.RIGHT:
                    moveHorizontal(1);
                    break;
                case Input.Keys.ENTER:
                case Input.Keys.SPACE:
                    activateSelected();
                    break;
                default:
                    break;
            }
        }
    }

    // select the first item
    public void selectFirst() {
        if (!items.isEmpty()) {
            selectIndex(0);
        }
    }

    // move selection up or down
    private void moveVertical(int direction) {
        MenuItem current = items.get(selectedIndex);
        MenuItem target = items.stream()
            .filter(item -> item != current && Integer.signum(item.row - current.row) == direction)
            .min(Comparator
                .comparingInt((MenuItem item) -> Math.abs(item.row - current.row))
                .thenComparingInt(item -> Math.abs(item.col - current.col)))
            .orElse(null);

        if (target != null) {

            selectIndex(items.indexOf(target));

        }
    }

    // move selection left or right
    private void moveHorizontal(int direction) {
        MenuItem current = items.get(selectedIndex);
        MenuItem target = items.stream()
            .filter(item -> item != current && Integer.signum(item.col - current.col) == direction)
            .filter(item -> item.row == current.row)
            .min(Comparator.comparingInt(item -> Math.abs(item.col - current.col)))
            .orElse(null);

        if (target == null) {
            target = items.stream()
                .filter(item -> item != current && Integer.signum(item.col - current.col) == direction)
                .min(Comparator
                    .comparingInt((MenuItem item) -> Math.abs(item.col - current.col))
                    .thenComparingInt(item -> Math.abs(item.row - current.row)))
                .orElse(null);
        }

        if (target != null) {

            selectIndex(items.indexOf(target));

        }
    }

    // activate whatever is selected
    private void activateSelected() {
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            Runnable action = items.get(selectedIndex).onActivate;
            if (action != null) {
                action.run();
            }
        }
    }

    // highlight the newly selected item
    private void selectIndex(int newIndex) {
        if (newIndex < 0 || newIndex >= items.size()) {
            return;
        }
        if (selectedIndex != -1) {

            applyVisualState(selectedIndex, false);

        }
        selectedIndex = newIndex;
        applyVisualState(selectedIndex, true);

        Runnable onFocus = items.get(selectedIndex).onFocus;
        if (onFocus != null) {
            onFocus.run();
        }
    }

    // update the color of a menu item
    private void applyVisualState(int index, boolean selected) {
        MenuItem item = items.get(index);
        item.actor.setColor(selected ? selectedColor : unselectedColor);
    }

    private static class MenuItem {
        private final Actor actor;
        private final int row;
        private final int col;
        private final Runnable onFocus;
        private final Runnable onActivate;

        private MenuItem(Actor actor, int row, int col, Runnable onFocus, Runnable onActivate) {
            this.actor = actor;
            this.row = row;
            this.col = col;
            this.onFocus = onFocus;
            this.onActivate = onActivate;
        }
    }
}
