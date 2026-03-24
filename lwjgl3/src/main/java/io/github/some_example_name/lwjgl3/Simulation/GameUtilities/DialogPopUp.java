package io.github.some_example_name.lwjgl3.Simulation.GameUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Renders a modal nutrition-fact card over the game world.
 *
 * The card pauses gameplay while visible, shows an optional icon, title
 * and body text, and fires a callback when the player dismisses it.
 *
 * OOP:
 *   Encapsulation – all Scene2D actors are built and owned privately;
 *                   callers only call show(), hide() and render().
 *   Abstraction   – callers supply a Runnable callback for "on close";
 *                   no knowledge of Scene2D internals is needed outside.
 *
 * SOLID:
 *   SRP – responsible only for displaying and dismissing the dialog card.
 *         Input-processor lifecycle is managed inside show() (once per open),
 *         not inside render() on every frame — this prevents permanently
 *         overwriting PlayScene's InputMultiplexer.
 *   OCP – card layout can be extended by overriding buildCard() without
 *         changing the show/render contract.
 *
 * Design Pattern: Template Method (light) – render() always draws the dim
 *   overlay first, then the card on top; the two steps are separated so
 *   either can be overridden independently in a subclass.
 */
public class DialogPopUp {

    // ── Layout constants ──────────────────────────────────────────────────

    private static final float CARD_WIDTH    = 320f;
    private static final float CARD_PADDING  = 18f;
    private static final float ICON_SIZE     = 72f;
    private static final float BODY_WIDTH    = 270f;
    private static final float OVERLAY_ALPHA = 0.65f;
    private static final float TITLE_PAD     = 12f;
    private static final float BODY_PAD      = 18f;
    private static final float BTN_PAD       = 10f;

    // ── Scene2D components ────────────────────────────────────────────────

    private final Stage  overlayStage;
    private final Skin   skin;
    private final Table  rootTable;

    /** Single-pixel white texture tinted for the dim overlay. */
    private final Texture dimTexture;

    // ── State ─────────────────────────────────────────────────────────────

    private boolean  open          = false;
    private Runnable dismissAction = null;

    // ── Construction ──────────────────────────────────────────────────────

    public DialogPopUp(Stage stage, Skin skin) {
        this.overlayStage = stage;
        this.skin         = skin;

        rootTable = new Table();
        rootTable.setFillParent(true);
        overlayStage.addActor(rootTable);

        Pixmap px = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        px.setColor(Color.WHITE);
        px.fill();
        dimTexture = new Texture(px);
        px.dispose();
    }

    // ── Public API ────────────────────────────────────────────────────────

    /** Show the card with no icon. */
    public void show(String title, String body) { show(title, body, null); }

    /**
     * Builds and displays the nutrition-fact card.
     *
     * The input processor is set to the overlay stage HERE (once), not inside
     * render() — this prevents the card from hijacking the input processor on
     * every frame while open and permanently breaking WASD controls.
     */
    public void show(String title, String body, Texture iconTexture) {
        rootTable.clear();
        rootTable.add(buildCard(title, body, iconTexture)).width(CARD_WIDTH);
        overlayStage.addActor(rootTable);
        open = true;
        Gdx.input.setInputProcessor(overlayStage);
    }

    /** Hides the card. PlayScene.onDialogClosed() restores the InputMultiplexer. */
    public void hide() {
        open = false;
        rootTable.clear();
    }

    public boolean isVisible()                      { return open; }
    public void    setCloseCallback(Runnable action){ this.dismissAction = action; }

    // ── Rendering ─────────────────────────────────────────────────────────

    /**
     * Template Method: draws the dim overlay then the card on top.
     * The two rendering steps are kept separate so either can be overridden.
     */
    public void render(SpriteBatch batch) {
        if (!open) return;
        drawDimOverlay(batch);
        overlayStage.act(Gdx.graphics.getDeltaTime());
        overlayStage.draw();
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Draws a full-screen semi-transparent rectangle using SpriteBatch
     * and a 1×1 white pixel tinted to the overlay colour.
     * This avoids a ShapeRenderer dependency and keeps resource usage minimal.
     */
    private void drawDimOverlay(SpriteBatch batch) {
        boolean wasBatching = batch.isDrawing();
        if (wasBatching) batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();
        batch.setColor(0f, 0f, 0f, OVERLAY_ALPHA);
        batch.draw(dimTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
        batch.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /** Constructs the visible card Table. Extracted so layout can be changed independently. */
    private Table buildCard(String title, String body, Texture iconTexture) {
        Table card = new Table();
        card.setBackground(skin.getDrawable("window"));
        card.pad(CARD_PADDING);

        Label titleLbl = new Label(title, skin);
        titleLbl.setColor(Color.YELLOW);
        card.add(titleLbl).padBottom(TITLE_PAD).row();

        if (iconTexture != null) {
            float ar = (float) iconTexture.getWidth() / iconTexture.getHeight();
            float w  = ar >= 1f ? ICON_SIZE : ICON_SIZE * ar;
            float h  = ar >= 1f ? ICON_SIZE / ar : ICON_SIZE;
            card.add(new Image(iconTexture)).size(w, h).padBottom(TITLE_PAD).row();
        }

        Label bodyLbl = new Label(body, skin);
        bodyLbl.setWrap(true);
        bodyLbl.setColor(Color.WHITE);
        card.add(bodyLbl).width(BODY_WIDTH).padBottom(BODY_PAD).row();

        TextButton dismissBtn = new TextButton("Got it!", skin);
        dismissBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                if (dismissAction != null) dismissAction.run();
            }
        });
        card.add(dismissBtn).padTop(BTN_PAD);
        return card;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────

    public void dispose() { if (dimTexture != null) dimTexture.dispose(); }
}
