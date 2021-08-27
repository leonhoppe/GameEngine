package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.var.Animation;
import de.craftix.engine.var.Mesh;
import de.craftix.engine.var.Transform;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class ScreenObject implements Serializable {
    public Transform transform;
    protected transient Sprite sprite;
    protected Mesh mesh;
    protected Animation animation;
    protected float layer;
    protected boolean visible;
    protected boolean renderBounds;

    protected void render(Graphics2D g) {
        Point pos = Screen.calculateScreenPosition(transform);
        AffineTransform original = (AffineTransform) g.getTransform().clone();

        g.translate(pos.x + ((transform.scale.width * (GameEngine.getCamera().getScale())) / 2f), pos.y + (transform.scale.height * (GameEngine.getCamera().getScale())) / 2f);
        g.rotate(transform.rotation.getAngle(), transform.position.x * (GameEngine.getCamera().getScale()), -transform.position.y * (GameEngine.getCamera().getScale()));

        if (sprite == null) {
            mesh.render(g, true, transform);
            return;
        }

        if (sprite.texture != null && (animation == null || !animation.isRunning()))
            sprite.render(g, transform);

        if (animation != null)
            animation.getImage().render(g, transform);

        g.setColor(Color.BLACK);
        g.setTransform(original);
    }

    protected ScreenObject() { transform = new Transform(); sprite = new Sprite(); layer = 0f; visible = false; renderBounds = false; }

    public void fixedUpdate() {}
    public void update() {}
    public void start() {}
    public void stop() {}

    public Sprite getSprite() { return sprite; }
    public Animation getAnimation() { return animation; }
    public Mesh getMesh() {
        if (mesh != null) return mesh;
        return new Mesh(Color.BLACK, Shape.RECTANGLE);
    }
    public float getLayer() { return layer; }
    public boolean isVisible() { return visible; }

    public void renderBounds(boolean value) { renderBounds = value; }

    public Area getShape() {
        return new Area(Screen.getRawTransform(transform).createTransformedShape(getRawShape()));
    }
    public Area getScreenShape() {
        if (sprite != null) {
            return new Area(Screen.getTransform(transform).createTransformedShape(new Mesh(Color.BLACK, Shape.RECTANGLE).getMesh(true, transform)));
        }else
            return new Area(Screen.getTransform(transform).createTransformedShape(mesh.getMesh(true, transform)));
    }
    public Area getRawShape() {
        if (sprite.texture != null || animation != null)
            return Shape.RECTANGLE.getRender(transform, false);

        return new Mesh(Color.BLACK, Shape.RECTANGLE).getMesh(false, transform);
    }
}
