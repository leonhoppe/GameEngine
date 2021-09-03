package de.craftix.engine.var;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Objects;

public class Vector2 implements Serializable, Animatable {
    public static Vector2 zero() { return new Vector2(0, 0); }
    public static Vector2 up() { return new Vector2(0, 1); }
    public static Vector2 down() { return new Vector2(0, -1); }
    public static Vector2 right() { return new Vector2(1, 0); }
    public static Vector2 left() { return new Vector2(-1, 0); }

    public float x;
    public float y;

    public Vector2() { this(0, 0); }
    public Vector2(float x, float y) { this.x = x; this.y = y; }
    public Vector2(double x, double y) { this.x = (float) x; this.y = (float) y; }
    public Vector2(float xy) { this(xy, xy); }
    public Vector2(Vector2 vector) { this(vector.x, vector.y); }
    public Vector2(Point point) { this(point.x, point.y); }
    public Vector2(Point2D point2D) { this(point2D.getX(), point2D.getY()); }

    public Point toPoint() { return new Point(Math.round(x), Math.round(y)); }
    public Point toPointFloored() { return new Point((int) Math.floor(x), (int) Math.floor(y)); }
    public Point2D toPoint2D() { return new Point2D.Float(x, y); }

    public int getX() { return Math.round(x); }
    public int getY() { return Math.round(y); }

    public Vector2 round() { x = Math.round(x); y = Math.round(y); return this; }
    public Vector2 floor() { x = (float) Math.floor(x); y = (float) Math.floor(y); return this; }

    public Vector2 copy() { return new Vector2(x, y); }
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return Float.compare(vector2.x, x) == 0 && Float.compare(vector2.y, y) == 0;
    }
    public int hashCode() {
        return Objects.hash(x, y);
    }

    //Vector Math
    public Vector2 add(Vector2 vec) { this.x += vec.x; this.y += vec.y; return this; }
    public Vector2 sub(Vector2 vec) { this.x -= vec.x; this.y -= vec.y; return this; }
    public Vector2 mul(Vector2 vec) { this.x *= vec.x; this.y *= vec.y; return this; }
    public Vector2 div(Vector2 vec) { this.x /= vec.x; this.y /= vec.y; return this; }

    //Float Math
    public Vector2 add(float value) { add(new Vector2(value)); return this; }
    public Vector2 sub(float value) { sub(new Vector2(value)); return this; }
    public Vector2 mul(float value) { mul(new Vector2(value)); return this; }
    public Vector2 div(float value) { div(new Vector2(value)); return this; }

    //Calculations
    public static float distance(Vector2 p1, Vector2 p2) {
        p1 = p1.copy(); p2 = p2.copy();
        float px = p1.x - p2.x;
        float py = p1.y - p2.y;
        return (float) Math.sqrt(px * px + py * py);
    }
    public static double angle(Vector2 p1, Vector2 p2) {
        p1 = p1.copy(); p2 = p2.copy();
        p2.x -= p1.x;
        p2.y -= p1.y;
        return Math.atan2(p2.x, p2.y);
    }
    public static Vector2 direction(Vector2 p1, Vector2 p2) {
        p1 = p1.copy(); p2 = p2.copy();
        double angle = angle(p1, p2);
        Vector2 dir = new Vector2();
        dir.x = (float) Math.sin(angle);
        dir.y = (float) Math.cos(angle);
        return dir;
    }
}
