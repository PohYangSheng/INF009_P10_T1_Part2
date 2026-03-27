package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.graphics.Color;

public final class MenuStyleConfig {

    private static final MenuStyleConfig INSTANCE = new MenuStyleConfig();

    private final Color selectedColor = new Color(Color.GOLD);
    private final Color unselectedColor = new Color(Color.WHITE);
    private final Color titleColor = new Color(Color.WHITE);
    private final Color accentTitleColor = new Color(Color.YELLOW);
    private final float buttonFontScale = 0.5f;
    private final float compactButtonFontScale = 0.45f;
    private final float subtitleFontScale = 0.7f;
    private final float standardButtonWidth = 180f;
    private final float standardButtonHeight = 50f;
    private final float compactButtonWidth = 150f;
    private final float compactButtonHeight = 45f;
    private final float sliderWidth = 200f;

    private MenuStyleConfig() {
    }

    public static MenuStyleConfig getInstance() {
        return INSTANCE;
    }

    public Color getSelectedColor() {
        return new Color(selectedColor);
    }

    public Color getUnselectedColor() {
        return new Color(unselectedColor);
    }

    public Color getTitleColor() {
        return new Color(titleColor);
    }

    public Color getAccentTitleColor() {
        return new Color(accentTitleColor);
    }

    public float getButtonFontScale() {
        return buttonFontScale;
    }

    public float getCompactButtonFontScale() {
        return compactButtonFontScale;
    }

    public float getSubtitleFontScale() {
        return subtitleFontScale;
    }

    public float getStandardButtonWidth() {
        return standardButtonWidth;
    }

    public float getStandardButtonHeight() {
        return standardButtonHeight;
    }

    public float getCompactButtonWidth() {
        return compactButtonWidth;
    }

    public float getCompactButtonHeight() {
        return compactButtonHeight;
    }

    public float getSliderWidth() {
        return sliderWidth;
    }
}
