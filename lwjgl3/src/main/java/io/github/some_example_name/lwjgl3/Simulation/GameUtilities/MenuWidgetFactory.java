package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

// interface for creating menu UI elements (buttons, labels, sliders)
public interface MenuWidgetFactory {

    TextButton createButton(String text);

    TextButton createCompactButton(String text);

    TextButton createBackButton();

    Label createSubtitleLabel(String text);

    Label createSubtitleLabel(String text, Color color);

    Label createStandardLabel(String text);

    Label createStandardLabel(String text, Color color);

    Slider createVolumeSlider(float min, float max, float step);
}
