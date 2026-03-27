package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

// creates menu buttons/labels/sliders with the kenney skin
public class KenneyMenuWidgetFactory implements MenuWidgetFactory {

    private final Skin skin;
    private final MenuStyleConfig styleConfig;

    // constructor
    public KenneyMenuWidgetFactory(Skin skin) {
        this.skin = skin;
        this.styleConfig = MenuStyleConfig.getInstance();
    }

    // make a normal button
    @Override
    public TextButton createButton(String text) {
        TextButton button = new TextButton(text, skin);
        button.getLabel().setFontScale(styleConfig.getButtonFontScale());
        return button;
    }

    // make a smaller button
    @Override
    public TextButton createCompactButton(String text) {
        TextButton button = new TextButton(text, skin);
        button.getLabel().setFontScale(styleConfig.getCompactButtonFontScale());
        return button;
    }

    // make the back button
    @Override
    public TextButton createBackButton() {
        return createButton("Back");
    }

    // make a subtitle label
    @Override
    public Label createSubtitleLabel(String text) {
        Label label = new Label(text, skin, "subtitle");
        label.setColor(styleConfig.getTitleColor());
        label.setFontScale(styleConfig.getSubtitleFontScale());
        return label;
    }

    // make a subtitle label
    @Override
    public Label createSubtitleLabel(String text, Color color) {
        Label label = createSubtitleLabel(text);
        label.setColor(color);
        return label;
    }

    // make a regular label
    @Override
    public Label createStandardLabel(String text) {
        Label label = new Label(text, skin);
        label.setColor(styleConfig.getTitleColor());
        return label;
    }

    // make a regular label
    @Override
    public Label createStandardLabel(String text, Color color) {
        Label label = createStandardLabel(text);
        label.setColor(color);
        return label;
    }

    // make a volume slider
    @Override
    public Slider createVolumeSlider(float min, float max, float step) {
        return new Slider(min, max, step, false, skin);
    }
}
