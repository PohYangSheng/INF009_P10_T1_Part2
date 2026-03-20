package io.github.some_example_name.lwjgl3.abstract_engine.collision;

import com.badlogic.gdx.math.MathUtils;

import io.github.some_example_name.lwjgl3.abstract_engine.entity.Entity;

/**
 * Detects and resolves collisions between an entity and screen boundaries.
 * 
 * @param <T> Type of the collidable entity
 */
public abstract class BoundaryCollisionDetector<T extends Collidable> extends CollisionDetector {
    private final T entity;
    private final float screenWidth, screenHeight, textureWidth, textureHeight;
    
    /**
     * Creates a new boundary collision detector for the specified entity.
     * 
     * @param entity The entity to check for boundary collisions
     */
    protected BoundaryCollisionDetector(T entity) {
        this.entity = entity;
        this.screenWidth = com.badlogic.gdx.Gdx.graphics.getWidth();
        this.screenHeight = com.badlogic.gdx.Gdx.graphics.getHeight();
        this.textureWidth = ((Entity) entity).getTexture().getWidth();
        this.textureHeight = ((Entity) entity).getTexture().getHeight();
    }
    
    /**
     * Checks if the entity is colliding with screen boundaries.
     * 
     * @return true if the entity is outside screen bounds, false otherwise
     */
    @Override
    public boolean checkCollision() {
        Entity entityObj = (Entity) entity;
        return entityObj.getPosition().x < 0 || 
               entityObj.getPosition().x > screenWidth - textureWidth ||
               entityObj.getPosition().y < 0 || 
               entityObj.getPosition().y > screenHeight - textureHeight;      
    }
    
    /**
     * Resolves boundary collisions by clamping the entity position to screen bounds.
     */
    @Override
    public void resolveCollision() {
        Entity entityObj = (Entity) entity;
        
        // Check and resolve horizontal boundary collision
        if (entityObj.getPosition().x < 0 || 
            entityObj.getPosition().x > getScreenWidth() - getTextureWidth()) {
            
            entityObj.setPositionX(MathUtils.clamp(
                entityObj.getPosition().x, 
                0, 
                getScreenWidth() - getTextureWidth()
            ));
        }
        
        // Check and resolve vertical boundary collision
        if (entityObj.getPosition().y < 0 || 
            entityObj.getPosition().y > getScreenHeight() - getTextureHeight()) {
                
            entityObj.setPositionY(MathUtils.clamp(
                entityObj.getPosition().y, 
                0, 
                getScreenHeight() - getTextureHeight()
            ));
        }
    }
    
    /**
     * Gets the entity monitored by this detector.
     * 
     * @return The monitored entity
     */
    public T getEntity() {
        return entity;
    }
    
    /**
     * Gets the screen width used for boundary checking.
     * 
     * @return The screen width
     */
    public float getScreenWidth() {
        return screenWidth;
    }
    
    /**
     * Gets the screen height used for boundary checking.
     * 
     * @return The screen height
     */
    public float getScreenHeight() {
        return screenHeight;
    }
    
    /**
     * Gets the texture width used for boundary calculations.
     * 
     * @return The texture width
     */
    public float getTextureWidth() {
        return textureWidth;
    }
    
    /**
     * Gets the texture height used for boundary calculations.
     * 
     * @return The texture height
     */
    public float getTextureHeight() {
        return textureHeight;
    }
}