package de.craftix.engine.render;

import de.craftix.engine.var.Transform;

import java.io.Serializable;

public class Camera implements Serializable {

    public Transform transform;
    public float z;

    private float scalingFactor = 500;

    public Camera() { transform = new Transform(); z = 1; }

    public float getScale() { return (z * 0.1f) * scalingFactor; }
    public float getScalingFactor() { return scalingFactor; }
    public void setScalingFactor(float scalingFactor) { this.scalingFactor = scalingFactor; }

}
