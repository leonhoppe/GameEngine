package de.craftix.engine.ui.containers.grid;

import java.io.Serializable;

public class Collum implements Serializable {
    public float width;
    public final boolean fit;

    public Collum(float width) { this.width = width; fit = false; }
    public Collum() { fit = true;}
}
