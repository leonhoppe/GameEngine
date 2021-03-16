package de.craftix.engine.var;

public class Quaternion {
    public static final Quaternion IDENTITY() { return new Quaternion(Math.toRadians(0)); }
    public static final Quaternion ANGLE90() { return new Quaternion(Math.toRadians(90)); }
    public static final Quaternion ANGLE180() { return new Quaternion(Math.toRadians(180)); }
    public static final Quaternion ANGLE270() { return new Quaternion(Math.toRadians(270)); }

    public static Quaternion euler(int degrees) { return new Quaternion(Math.toRadians(degrees)); }

    private double angle;
    public Quaternion(double angle) { this.angle = angle; }
    public void rotate(float angle) { this.angle += Math.toRadians(angle); }
    public double getAngle() { return angle; }

    public Quaternion copy() { return new Quaternion(angle); }
}
