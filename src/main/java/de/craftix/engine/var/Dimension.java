package de.craftix.engine.var;

public class Dimension {

    public float width;
    public float height;

    public Dimension() { this(0, 0); }
    public Dimension(float width, float height) { this.width = width; this.height = height; }
    public Dimension(Dimension d) { this(d.width, d.height); }

    public int getWidth() { return Math.round(width); }
    public int getHeight() { return Math.round(height); }

    @Override
    public String toString() {
        return "Dimension{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
