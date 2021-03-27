package de.craftix.engine.var;

import java.awt.*;
import java.io.Serializable;

public class Vector2 implements Serializable {
    public static Vector2 forward() { return new Vector2(0, 1); }
    public static Vector2 backward() { return new Vector2(0, -1); }
    public static Vector2 right() { return new Vector2(1, 0); }
    public static Vector2 left() { return new Vector2(-1, 0); }

    public float x;
    public float y;

    public Vector2() { this(0, 0); }
    public Vector2(float x, float y) { this.x = x; this.y = y; }
    public Vector2(float xy) { this(xy, xy); }
    public Vector2(Vector2 vector) { this(vector.x, vector.y); }
    public Vector2(Point point) { this(point.x, point.y); }

    public Point toPoint() { return new Point(Math.round(x), Math.round(y)); }
    public Point toPointFloored() { return new Point((int) x, (int) y); }

    public int getX() { return Math.round(x); }
    public int getY() { return Math.round(y); }

    @Override
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    //Vector Math
    public Vector2 add(Vector2 vec) { return new Vector2(x + vec.x, y + vec.y); }
    public Vector2 sub(Vector2 vec) { return new Vector2(x - vec.x, y - vec.y); }
    public Vector2 mul(Vector2 vec) { return new Vector2(x * vec.x, y * vec.y); }
    public Vector2 div(Vector2 vec) { return new Vector2(x / vec.x, y / vec.y); }

    public void addSelf(Vector2 vec) { this.x += vec.x; this.y += vec.y; }
    public void subSelf(Vector2 vec) { this.x -= vec.x; this.y -= vec.y; }
    public void mulSelf(Vector2 vec) { this.x *= vec.x; this.y *= vec.y; }
    public void divSelf(Vector2 vec) { this.x /= vec.x; this.y /= vec.y; }

    //Float Math
    public Vector2 add(float value) { return add(new Vector2(value)); }
    public Vector2 sub(float value) { return sub(new Vector2(value)); }
    public Vector2 mul(float value) { return mul(new Vector2(value)); }
    public Vector2 div(float value) { return div(new Vector2(value)); }

    public void addSelf(float value) { addSelf(new Vector2(value)); }
    public void subSelf(float value) { subSelf(new Vector2(value)); }
    public void mulSelf(float value) { mulSelf(new Vector2(value)); }
    public void divSelf(float value) { divSelf(new Vector2(value)); }
}
