package io.github.some_example_name.lwjgl3.Abstract_Engine.SceneManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

// manages the scene stack - switching between menu, play, pause etc
public class SceneManager {

    private final Stack<AbstractScene> scenes = new Stack<>();

    // switch to a new scene (disposes the old one)
    public void setScene(AbstractScene scene) {
        if (!scenes.isEmpty()) scenes.pop().dispose();
        scenes.push(scene);
        scene.create();
    }

    // push scene on top (used for pause overlay)
    public void pushScene(AbstractScene scene) {
        scenes.push(scene);
        scene.create();
    }

    // pop the top scene off the stack
    public void popScene() {
        if (!scenes.isEmpty()) scenes.pop().dispose();
    }

    // getter for current scene
    public AbstractScene getCurrentScene() {
        return scenes.isEmpty() ? null : scenes.peek();
    }

    // check if empty
    public boolean isEmpty() {
        return scenes.isEmpty();
    }

    // runs every frame
    public void update(float dt) {
        if (!scenes.isEmpty()) scenes.peek().update(dt);
    }

    // draw everything to screen
    public void render(SpriteBatch batch) {
        for (AbstractScene scene : scenes) scene.render(batch);
    }

    // clean up textures/resources so we dont leak memory
    public void dispose() {
        while (!scenes.isEmpty()) scenes.pop().dispose();
    }
}
