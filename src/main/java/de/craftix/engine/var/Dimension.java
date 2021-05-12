package de.craftix.engine.var;

import java.io.Serializable;
import java.util.Objects;

public class Dimension implements Serializable, Transformation {

    public float width;
    public float height;

    public Dimension() { this(0, 0); }
    public Dimension(float width, float height) { this.width = width; this.height = height; }
    public Dimension(Dimension d) { this(d.width, d.height); }
    public Dimension(float wh) { this(wh, wh); }

    public int getWidth() { return Math.round(width); }
    public int getHeight() { return Math.round(height); }

    public Dimension copy() { return new Dimension(width, height); }
    public String toString() {
        return "Dimension{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dimension dimension = (Dimension) o;
        return Float.compare(dimension.width, width) == 0 && Float.compare(dimension.height, height) == 0;
    }
    public int hashCode() {
        return Objects.hash(width, height);
    }
}
