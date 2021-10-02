package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.var.Quaternion;
import de.craftix.engine.var.Scene;
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
    protected boolean visible = true;
    protected Scene scene;

    protected void render(Graphics2D g) {
        if (transform.rotation.getAngle() >= Math.PI * 2) transform.rotation = new Quaternion(transform.rotation.getAngle() % Math.toRadians(360));
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

    protected ScreenObject() { transform = new Transform(); layer = 0f; }

    public void initialise(Scene scene) { this.scene = scene; }

    public void fixedUpdate() {}
    public void update() {}
    public void start() {}
    public void stop() {}

    public Sprite getSprite() { return sprite; }
    public Mesh getMesh() {
        if (mesh != null) return mesh;
        return new Mesh(MShape.RECTANGLE, Color.BLACK);
    }
    public float getLayer() { return layer; }
    public boolean isVisible() { return visible; }
    public Scene getScene() { return this.scene; }

    public void setVisible(boolean visible) { this.visible = visible; }

    public Area getShape() {
        return new Area(Screen.getRawTransform(transform).createTransformedShape(getRawShape()));
    }
    public Area getScreenShape() {
        if (sprite != null) {
            return new Area(Screen.getTransform(transform).createTransformedShape(new Mesh(MShape.RECTANGLE, Color.BLACK).getMesh(true, transform)));
        }else
            return new Area(Screen.getTransform(transform).createTransformedShape(mesh.getMesh(true, transform)));
    }
    public Area getRawShape() {
        if (sprite != null)
            return MShape.RECTANGLE.getRender(transform, false);

        return new Mesh(MShape.RECTANGLE, Color.BLACK).getMesh(false, transform);
    }
}
