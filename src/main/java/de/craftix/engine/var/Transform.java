package de.craftix.engine.var;

import java.awt.*;

public class Transform {

    public Vector2 position;
    public Dimension scale;
    public Quaternion rotation;

    public Transform() { position = new Vector2(); scale = new Dimension(); rotation = Quaternion.IDENTITY(); }

    public Transform copy() {
        Transform copy = new Transform();
        copy.position = new Vector2(position);
        copy.scale = new Dimension(scale);
        copy.rotation = new Quaternion(rotation.getAngle());
        return copy;
    }

}
