package de.craftix.engine.ui.containers.grid;

import java.io.Serializable;

public class Row implements Serializable {
    public float height;
    public final boolean fit;

    public Row(float height) { this.height = height; fit = false; }
    public Row() { fit = true; }
}
