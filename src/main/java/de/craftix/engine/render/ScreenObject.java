package de.craftix.engine.render;

import de.craftix.engine.var.Animation;
import de.craftix.engine.var.Transform;

import java.awt.*;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class ScreenObject {
    public Transform transform;
    protected Sprite sprite;
    protected Animation animation;
    protected float layer;
    protected boolean visible;

    protected void render(Graphics2D g) {
        Point pos = Screen.calculateScreenPosition(transform);
        AffineTransform original = g.getTransform();
        g.translate(pos.x + (transform.scale.width / 2f), pos.y + (transform.scale.height / 2f));
        g.rotate(transform.rotation.getAngle(), transform.position.x, -transform.position.y);

        if (sprite.texture == null && sprite.color != null && animation == null) {
            g.setColor(sprite.color);
            g.fill(getRawShape());
        }

        if (sprite.texture != null && (animation == null || !animation.isRunning()))
            g.drawImage(sprite.getTexture(transform.scale.width, transform.scale.height), -transform.scale.width / 2, -transform.scale.height / 2, null);

        if (animation != null)
            g.drawImage(animation.getImage().getTexture(transform.scale.width, transform.scale.height), -transform.scale.width / 2, -transform.scale.height / 2, null);

        g.setColor(Color.BLACK);
        g.setTransform(original);
    }

    protected ScreenObject() { transform = new Transform(); sprite = new Sprite(); layer = 0f; visible = false; }

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

    protected Area getShape() {
        AffineTransform trans = new AffineTransform();
        trans.translate(transform.position.x + (transform.scale.width / 2f), transform.position.y + (transform.scale.height / 2f));
        trans.rotate(transform.rotation.getAngle(), transform.position.x, -transform.position.y);
        Shape shape = null;
        if (sprite.texture != null || animation != null)
            shape = new Rectangle(-transform.scale.width / 2, -transform.scale.height / 2, transform.scale.width, transform.scale.height);
        else
            switch (sprite.shape) {
                case CIRCLE:
                    shape = new Ellipse2D.Float(-transform.scale.width / 2f, -transform.scale.height / 2f, transform.scale.width, transform.scale.height);
                    break;
                case RECTANGLE:
                    shape = new Rectangle(-transform.scale.width / 2, -transform.scale.height / 2, transform.scale.width, transform.scale.height);
                    break;
                case TRIANGLE:
                    Point top = new Point(0, -transform.scale.height / 2);
                    Point right = new Point(-transform.scale.width / 2, transform.scale.height / 2);
                    Point left = new Point(transform.scale.width / 2, transform.scale.height / 2);
                    shape = new Polygon(new int[]{ top.x, right.x, left.x },
                            new int[]{ top.y, right.y, left.y },
                            3);
                    break;
            }
        return new Area(trans.createTransformedShape(shape));
    }
    protected Area getRawShape() {
        Shape shape = null;
        if (sprite.texture != null || animation != null)
            shape = new Rectangle(-transform.scale.width / 2, -transform.scale.height / 2, transform.scale.width, transform.scale.height);
        else
            switch (sprite.shape) {
                case CIRCLE:
                    shape = new Ellipse2D.Float(-transform.scale.width / 2f, -transform.scale.height / 2f, transform.scale.width, transform.scale.height);
                    break;
                case RECTANGLE:
                    shape = new Rectangle(-transform.scale.width / 2, -transform.scale.height / 2, transform.scale.width, transform.scale.height);
                    break;
                case TRIANGLE:
                    Point top = new Point(0, -transform.scale.height / 2);
                    Point right = new Point(-transform.scale.width / 2, transform.scale.height / 2);
                    Point left = new Point(transform.scale.width / 2, transform.scale.height / 2);
                    shape = new Polygon(new int[]{ top.x, right.x, left.x },
                            new int[]{ top.y, right.y, left.y },
                            3);
                    break;
            }
        return new Area(shape);
    }
}
