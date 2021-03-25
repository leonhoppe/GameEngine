package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.var.Animation;
import de.craftix.engine.var.Mesh;
import de.craftix.engine.var.Transform;

import java.awt.*;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

public class ScreenObject implements Serializable {
    public Transform transform;
    protected Sprite sprite;
    protected Animation animation;
    protected float layer;
    protected boolean visible;
    protected boolean renderBounds;

    protected void render(Graphics2D g) {
        Point pos = Screen.calculateScreenPosition(transform);
        AffineTransform original = g.getTransform();

        if (sprite.texture == null && sprite.color != null && animation == null) {
            g.setColor(sprite.color);
            g.fill(getScreenShape());
        }else {
            g.translate(pos.x + ((transform.scale.width * (GameEngine.getCamera().getScale())) / 2f), pos.y + (transform.scale.height * (GameEngine.getCamera().getScale())) / 2f);
            g.rotate(transform.rotation.getAngle(), transform.position.x * (GameEngine.getCamera().getScale()), -transform.position.y * (GameEngine.getCamera().getScale()));
        }

        if (sprite.texture != null && (animation == null || !animation.isRunning()))
            g.drawImage(sprite.getTexture(transform.scale.width, transform.scale.height), (int) -((transform.scale.width * GameEngine.getCamera().getScale()) / 2f), (int) (-transform.scale.height * GameEngine.getCamera().getScale() / 2), null);

        if (animation != null)
            g.drawImage(animation.getImage().getTexture(transform.scale.getWidth(), transform.scale.getHeight()), (int) -((transform.scale.width * GameEngine.getCamera().getScale()) / 2f), (int) (-transform.scale.height * GameEngine.getCamera().getScale() / 2), null);

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
        return new Area(Screen.getTransform(transform).createTransformedShape(new Mesh(sprite.getShape(animation), transform).getMesh()));
    }
    public Area getRawShape() {
        Shape shape = null;
        if (sprite.texture != null || animation != null)
            shape = new Rectangle((int) -(transform.scale.width / 2f), (int) -(transform.scale.height / 2f), transform.scale.getWidth(), transform.scale.getHeight());
        else
            switch (sprite.shape) {
                case CIRCLE:
                    shape = new Ellipse2D.Float(-transform.scale.width / 2f, -transform.scale.height / 2f, transform.scale.width, transform.scale.height);
                    break;
                case RECTANGLE:
                    shape = new Rectangle((int) -(transform.scale.width / 2f), (int) -(transform.scale.height / 2f), transform.scale.getWidth(), transform.scale.getHeight());
                    break;
                case TRIANGLE:
                    Point top = new Point(0, (int) -(transform.scale.height / 2f));
                    Point right = new Point((int) -(transform.scale.width / 2f), (int) (transform.scale.height / 2f));
                    Point left = new Point((int) (transform.scale.width / 2f), (int) (transform.scale.height / 2f));
                    shape = new Polygon(new int[]{ top.x, right.x, left.x },
                            new int[]{ top.y, right.y, left.y },
                            3);
                    break;
            }
        return new Area(shape);
    }
}
