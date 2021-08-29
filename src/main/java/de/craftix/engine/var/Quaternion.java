package de.craftix.engine.var;

import java.io.Serializable;
import java.util.Objects;

public class Quaternion implements Serializable, Animatable {
    public static Quaternion IDENTITY() { return new Quaternion(Math.toRadians(0)); }
    public static Quaternion ANGLE90() { return new Quaternion(Math.toRadians(90)); }
    public static Quaternion ANGLE180() { return new Quaternion(Math.toRadians(180)); }
    public static Quaternion ANGLE270() { return new Quaternion(Math.toRadians(270)); }

    public static Quaternion euler(float degrees) { return new Quaternion(Math.toRadians(degrees)); }

    protected double angle;
    public Quaternion(double angle) { this.angle = angle; }
    public double getAngle() { return angle; }



    public Quaternion copy() { return new Quaternion(angle); }
    public String toString() {
        return "Quaternion{" +
                "angle=" + Math.toDegrees(angle) +
                '}';
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quaternion that = (Quaternion) o;
        return Double.compare(that.angle, angle) == 0;
    }
    public int hashCode() {
        return Objects.hash(angle);
    }
}
