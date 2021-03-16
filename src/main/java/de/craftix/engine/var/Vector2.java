package de.craftix.engine.var;

import java.awt.*;

public class Vector2 {

    public float x;
    public float y;

    public Vector2() { this(0, 0); }
    public Vector2(float x, float y) { this.x = x; this.y = y; }
    public Vector2(Vector2 vector) { this(vector.x, vector.y); }
    public Vector2(Point point) { this(point.x, point.y); }

    public Point toPoint() { return new Point(Math.round(x), Math.round(y)); }
}
