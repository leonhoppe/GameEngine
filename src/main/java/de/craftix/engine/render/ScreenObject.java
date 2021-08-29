package de.craftix.engine.render;

import de.craftix.engine.var.Transform;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.io.Serializable;

public class ScreenObject implements Serializable {
    public Transform transform;
    protected transient Sprite sprite;
    protected Mesh mesh;
    protected float layer;
    protected boolean visible;
    protected boolean renderBounds;

    protected void render(Graphics2D g) {
        AffineTransform original = (AffineTransform) g.getTransform().clone();

        g.setTransform(Screen.getTransform(transform));

        if (sprite == null) {
            mesh.render(g, true, transform);
        }else {
            sprite.render(g, transform);
        }

        g.setColor(Color.BLACK);
        g.setTransform(original);
    }

    protected ScreenObject() { transform = new Transform(); sprite = new Sprite(); layer = 0f; visible = false; renderBounds = false; }

    public void fixedUpdate() {}
    public void update() {}
    public void start() {}
    public void stop() {}

    public Sprite getSprite() { return sprite; }
    public Mesh getMesh() {
        if (mesh != null) return mesh;
        return new Mesh(Shape.RECTANGLE, Color.BLACK);
    }
    public float getLayer() { return layer; }
    public boolean isVisible() { return visible; }

    public void renderBounds(boolean value) { renderBounds = value; }

    public Area getShape() {
        return new Area(Screen.getRawTransform(transform).createTransformedShape(getRawShape()));
    }
    public Area getScreenShape() {
        if (sprite != null) {
            return new Area(Screen.getTransform(transform).createTransformedShape(new Mesh(Shape.RECTANGLE, Color.BLACK).getMesh(true, transform)));
        }else
            return new Area(Screen.getTransform(transform).createTransformedShape(mesh.getMesh(true, transform)));
    }
    public Area getRawShape() {
        if (sprite.texture != null)
            return Shape.RECTANGLE.getRender(transform, false);

        return new Mesh(Shape.RECTANGLE, Color.BLACK).getMesh(false, transform);
    }
}
