package io.github.some_example_name.lwjgl3.Abstract_Engine.EntityManager;

// interface for things that can move
public interface iMovable {
    
    void  setVelocity(float vx, float vy);
    void  stop();
    float getSpeed();
    float getVx();
    float getVy();

    float getX();
    float getY();
    void  setPosition(float x, float y);
}
