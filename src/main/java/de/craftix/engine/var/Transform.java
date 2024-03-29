package de.craftix.engine.var;

import java.io.Serializable;
import java.util.Objects;

public class Transform implements Serializable, Animatable {
    public static Transform parse(String str) {
        Transform trans = new Transform();
        str = str.replace("Transform", "").replaceAll("\\{", "").replaceAll("}", "").replace(" ", "");
        str = str.replace("position=Vector2", "").replace("scale=Dimension", "").replace("rotation=Quaternion", "");
        str = str.replace("Vector2", "").replace("Dimension", "").replace("Quaternion", "");
        String[] split = str.split(",");
        for (String s : split) {
            if (s.contains("x="))
                trans.position.x = Float.parseFloat(s.replace("x=", ""));
            if (s.contains("y="))
                trans.position.y = Float.parseFloat(s.replace("y=", ""));
            if (s.contains("width="))
                trans.scale.width = Float.parseFloat(s.replace("width=", ""));
            if (s.contains("height="))
                trans.scale.height = Float.parseFloat(s.replace("height=", ""));
            if (s.contains("angle="))
                trans.rotation = Quaternion.euler(Float.parseFloat(s.replace("angle=", "")));
        }
        return trans;
    }

    public Vector2 position;
    public Dimension scale;
    public Quaternion rotation;

    public Transform() { position = new Vector2(); scale = new Dimension(); rotation = Quaternion.IDENTITY(); }
    public Transform(Vector2 position, Dimension scale, Quaternion rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }
    public Transform(Vector2 pos, Dimension dim) { this(); position = pos; scale = dim; }
    public Transform(Vector2 pos, Quaternion rotation) { this(); position = pos; this.rotation = rotation; }
    public Transform(Dimension dim, Quaternion rotation) { this(); scale = dim; this.rotation = rotation; }
    public Transform(Vector2 pos) { this(); position = pos; }
    public Transform(Dimension dim) { this(); scale = dim; }
    public Transform(Quaternion rotation) { this(); this.rotation = rotation; }

    public void add(Transform transform) {
        position.add(transform.position);
        scale.width += transform.scale.width;
        scale.height += transform.scale.height;
        rotation.angle += transform.rotation.angle;
    }

    //Modify Rotation
    public void rotate(double angle) { rotation.angle += Math.toRadians(angle); }
    public void lookAt(Vector2 pos) {
        pos = pos.copy();
        pos.x -= position.x;
        pos.y -= position.y;
        rotation.angle = Math.atan2(pos.x, pos.y);
    }
    public void rotateAround(Vector2 pos, double angle) {
        pos = pos.copy();
        angle *= -1;

        double absAngle = Math.abs(angle);
        float s = (float) Math.sin(Math.toRadians(absAngle));
        float c = (float) Math.cos(Math.toRadians(absAngle));
        Vector2 pp = new Vector2(position);

        pp.sub(pos);
        float xNew;
        float yNew;
        if (angle > 0) {
            xNew = pp.x * c - pp.y * s;
            yNew = pp.x * s + pp.y * c;
        }else {
            xNew = pp.x * c + pp.y * s;
            yNew = -pp.x * s + pp.y * c;
        }

        pp.x = xNew + pos.x;
        pp.y = yNew + pos.y;
        position = pp;

        Vector2 rotVector = new Vector2(pos);
        rotVector.sub(position);
        rotation.angle = Math.atan2(rotVector.x, rotVector.y) + Math.toRadians(180);
    }

    //Modify Position
    public void translate(Vector2 pos) {
        position.add(pos);
    }
    public void translate(float value) {
        position.add(value);
    }

    //Get Transformed Vectors
    public Vector2 forward() {
        Vector2 forward = new Vector2();
        forward.x = (float) Math.sin(rotation.getAngle());
        forward.y = (float) Math.cos(rotation.getAngle());
        return forward;
    }
    public Vector2 backward() {
        return forward().mul(-1);
    }
    public Vector2 right() {
        Vector2 right = new Vector2();
        right.x = (float) Math.sin(rotation.getAngle() + Math.toRadians(90));
        right.y = (float) Math.cos(rotation.getAngle() + Math.toRadians(90));
        return right;
    }
    public Vector2 left() {
        return right().mul(-1);
    }

    public Transform copy() {
        Transform copy = new Transform();
        copy.position = new Vector2(position);
        copy.scale = new Dimension(scale);
        copy.rotation = new Quaternion(rotation.getAngle());
        return copy;
    }
    public String toString() {
        return "Transform{" +
                "position=" + position +
                ", scale=" + scale +
                ", rotation=" + rotation +
                '}';
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transform transform = (Transform) o;
        return Objects.equals(position, transform.position) && Objects.equals(scale, transform.scale) && Objects.equals(rotation, transform.rotation);
    }
    public int hashCode() {
        return Objects.hash(position, scale, rotation);
    }
}
