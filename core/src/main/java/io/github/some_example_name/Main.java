package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

// minimal LibGDX entry point used by the core module sample
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    // creates the main resources or setup required by this main
    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
    }

    // draw everything to screen
    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    // clean up textures/resources so we dont leak memory
    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
