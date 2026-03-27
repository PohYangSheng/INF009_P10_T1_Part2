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

// the popup dialog that shows food facts
public class FoodFactPopUp {

    private static final float CARD_WIDTH    = 320f;
    private static final float CARD_PADDING  = 18f;
    private static final float ICON_SIZE     = 72f;
    private static final float BODY_WIDTH    = 270f;
    private static final float OVERLAY_ALPHA = 0.65f;
    private static final float TITLE_PAD     = 12f;
    private static final float BODY_PAD      = 18f;
    private static final float BTN_PAD       = 10f;

    private final Stage  overlayStage;
    private final Skin   skin;
    private final Table  rootTable;

    private final Texture dimTexture;

    private boolean  open          = false;
    private Runnable dismissAction = null;

    // constructor
    public FoodFactPopUp(Stage stage, Skin skin) {
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

    // show the popup with given title and message
    public void show(String title, String body) {
        show(title, body, null);
    }

    // show the popup with given title and message
    public void show(String title, String body, Texture iconTexture) {
        rootTable.clear();
        rootTable.add(buildCard(title, body, iconTexture)).width(CARD_WIDTH);
        overlayStage.addActor(rootTable);
        open = true;
        Gdx.input.setInputProcessor(overlayStage);
    }

    // close the popup
    public void hide() {
        open = false;
        rootTable.clear();
    }

    // check if showing
    public boolean isShowing() {
        return open;
    }
    // setter for dismiss callback
    public void    setDismissCallback(Runnable action) {
        this.dismissAction = action;
    }

    // render the popup overlay
    public void render(SpriteBatch batch) {
        if (!open) return;
        drawDimOverlay(batch);
        overlayStage.act(Gdx.graphics.getDeltaTime());
        overlayStage.draw();
    }

    // draw dim overlay
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

    // build card
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
            // clicked
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                if (dismissAction != null) dismissAction.run();
            }
        });
        card.add(dismissBtn).padTop(BTN_PAD);
        return card;
    }

    // clean up textures/resources so we dont leak memory
    public void dispose() {
        if (dimTexture != null) dimTexture.dispose();
    }
}
