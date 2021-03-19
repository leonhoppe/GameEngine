package de.craftix.engine.render;

public class Camera {

    public float x;
    public float y;
    public float z;

    public Camera() { x = 0; y = 0; z = 10; }

    public float getScale() { return z / 10; }

}
