package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.var.Animation;
import de.craftix.engine.var.Mesh;
import de.craftix.engine.var.Transform;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

public class ScreenObject implements Serializable {
    public Transform transform;
    protected Sprite sprite;
    protected Mesh mesh;
    protected Animation animation;
    protected float layer;
    protected boolean visible;
    protected boolean renderBounds;

    protected void render(Graphics2D g) {
        Point pos = Screen.calculateScreenPosition(transform);
        AffineTransform original = g.getTransform();

        if (sprite == null) {
            g.draw(mesh.getMesh(true));
            return;
        }

        if (sprite.texture == null && sprite.color != null && animation == null) {
            g.setColor(sprite.color);
            g.fill(getScreenShape());
        }else {
            g.translate(pos.x + ((transform.scale.width * (GameEngine.getCamera().getScale())) / 2f), pos.y + (transform.scale.height * (GameEngine.getCamera().getScale())) / 2f);
            g.rotate(transform.rotation.getAngle(), transform.position.x * (GameEngine.getCamera().getScale()), -transform.position.y * (GameEngine.getCamera().getScale()));
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

    public ScreenObject copy() {
        ScreenObject copy = new ScreenObject();
        copy.transform = transform.copy();
        copy.sprite = sprite.copy();
        copy.visible = visible;
        copy.layer = layer;
        return copy;
    }

    public Sprite getSprite() { return sprite; }
    public Animation getAnimation() { return animation; }
    public float getLayer() { return layer; }
    public boolean isVisible() { return visible; }

    public void renderBounds(boolean value) { renderBounds = value; }

    public Area getShape() {
        return new Area(Screen.getRawTransform(transform).createTransformedShape(getRawShape()));
    }
    public Area getScreenShape() {
        return new Area(Screen.getTransform(transform).createTransformedShape(new Mesh(sprite.getShape(animation), transform).getMesh(true)));
    }
    public Area getRawShape() {
        if (sprite.texture != null || animation != null)
            return Shape.RECTANGLE.getRender(transform, false);

        return sprite.shape.getRender(transform, false);
    }
}
