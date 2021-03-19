package de.craftix.engine.var;

import java.io.Serializable;

public class Transform implements Serializable {
    public static Transform parse(String str) {
        Transform trans = new Transform();
        str = str.replace("Transform", "").replaceAll("\\{", "").replaceAll("}", "").replace(" ", "");
        str = str.replace("position=Vector2", "").replace("scale=Dimension", "").replace("rotation=Quaternion", "");
        str = str.replace("Vector2", "").replace("java.awt.Dimension", "").replace("Quaternion", "").replace("[", "").replace("]", "");
        String[] split = str.split(",");
        for (String s : split) {
            if (s.contains("x="))
                trans.position.x = Float.parseFloat(s.replace("x=", ""));
            if (s.contains("y="))
                trans.position.y = Float.parseFloat(s.replace("y=", ""));
            if (s.contains("width="))
                trans.scale.width = Integer.parseInt(s.replace("width=", ""));
            if (s.contains("height="))
                trans.scale.height = Integer.parseInt(s.replace("height=", ""));
            if (s.contains("angle="))
                trans.rotation = Quaternion.euler(Float.parseFloat(s.replace("angle=", "")));
        }
        return trans;
    }

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

    @Override
    public String toString() {
        return "Transform{" +
                "position=" + position +
                ", scale=Dimension{width=" + scale.width + ", height=" + scale.height + "}" +
                ", rotation=" + rotation +
                '}';
    }
}
