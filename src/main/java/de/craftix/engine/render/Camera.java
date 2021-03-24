package de.craftix.engine.render;

import java.io.Serializable;

public class Camera implements Serializable {

    public float x;
    public float y;
    public float z;

    private float scalingFactor = 500;

    public Camera() { x = 0; y = 0; z = 1; }

    public float getScale() { return (z * 0.1f) * scalingFactor; }
    public float getScalingFactor() { return scalingFactor; }
    public void setScalingFactor(float scalingFactor) { this.scalingFactor = scalingFactor; }

}
