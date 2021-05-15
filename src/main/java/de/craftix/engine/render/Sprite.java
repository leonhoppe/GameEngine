package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.var.Animation;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

public class Sprite {
    private static Resizer resizingMethod = null;

    public static Sprite load(String path) {
        try {
            return new Sprite(ImageIO.read(Objects.requireNonNull(Sprite.class.getClassLoader().getResource(path))), false);
        }catch (Exception e) { GameEngine.throwError(e); }
        return new Sprite();
    }
    public static Sprite loadExt(String path) {
        try {
            return new Sprite(ImageIO.read(new File(path)), false);
        }catch (Exception e) { GameEngine.throwError(e); }
        return new Sprite();
    }

    public Color color;
    public Shape shape;

    public transient BufferedImage texture;
    public transient BufferedImage bufferedTexture;
    public transient BufferedImage bufferedOriginal;
    public boolean repeat = false;

    public Sprite(Color color, Shape shape) { this.color = color; this.shape = shape; }
    public Sprite(BufferedImage texture, boolean repeat) { this.texture = texture; this.repeat = repeat; }
    public Sprite() {}

    public Sprite copy() {
        Sprite copy = new Sprite();
        copy.color = color;
        copy.shape = shape;
        copy.texture = texture;
        return copy;
    }

    public BufferedImage getTexture(float width, float height) {
        width *= GameEngine.getCamera().getScale();
        height *= GameEngine.getCamera().getScale();
        return getTextureRaw(width, height);
    }
    public BufferedImage getTextureRaw(float width, float height) {
        if (texture != null) {
            if (texture.getWidth() != width || texture.getHeight() != height) {
                if (bufferedTexture == null ||
                        bufferedOriginal != texture ||
                        bufferedTexture.getWidth() != width || bufferedTexture.getHeight() != height) {
                    bufferedOriginal = texture;
                    if (resizingMethod != null)
                        bufferedTexture = resizingMethod.resize(texture, Math.round(width), Math.round(height));
                    else
                        if (Screen.antialiasingEffectTextures())
                            bufferedTexture = Resizer.BILINEAR.resize(texture, Math.round(width), Math.round(height));
                        else
                            bufferedTexture = Resizer.AVERAGE.resize(texture, Math.round(width), Math.round(height));
                }
                return bufferedTexture;
            }
        }
        return texture;
    }

    public void render(Graphics2D g, Transform transform) {
        if (!repeat)
            g.drawImage(getTexture(transform.scale.width, transform.scale.height), (int) -((transform.scale.width * GameEngine.getCamera().getScale()) / 2f), (int) (-transform.scale.height * GameEngine.getCamera().getScale() / 2), null);
        else {
            //Repeat
            AffineTransform orig = g.getTransform();
            g.setTransform(Screen.getTransform(transform));
            g.translate(-g.getTransform().getTranslateX(), -g.getTransform().getTranslateY());
            Point pos = Screen.calculateScreenPosition(transform);
            final float width = transform.scale.width * GameEngine.getCamera().getScale();
            final float height = transform.scale.height * GameEngine.getCamera().getScale();

            BufferedImage render = getTexture(transform.scale.width, transform.scale.height);
            Area screen = new Area(new Rectangle2D.Float(0, 0, Screen.width(), Screen.height()));
            Rectangle2D self = new Rectangle2D.Float(1, 1, width, height);
            Vector2 min = new Vector2(pos);
            Area intersection = new Area(self);

            //top
            while (!intersection.isEmpty()) {
                min.subSelf(new Vector2(0, height));
                self = new Rectangle2D.Float(1, min.y, width, height);
                intersection = new Area(g.getTransform().createTransformedShape(self));
                intersection.intersect(screen);
            }

            intersection = new Area(new Rectangle2D.Float(1, 1, width, height));
            //Left
            while (!intersection.isEmpty()) {
                min.subSelf(new Vector2(width, 0));
                self = new Rectangle2D.Float(min.x, 1, width, height);
                intersection = new Area(g.getTransform().createTransformedShape(self));
                intersection.intersect(screen);
            }

            //Draw Images
            for (int x = (int) min.x; x < Screen.width(); x += width)
                for (int y = (int) min.y; y < Screen.height(); y += height)
                    g.drawImage(render, x, y, null);
        }
    }
    public void renderRaw(Graphics2D g, Transform transform) {
        if (!repeat)
            g.drawImage(getTextureRaw(transform.scale.width, transform.scale.height), (int) -(transform.scale.width / 2f), (int) -(transform.scale.height / 2), null);
        else {
            //Repeat
            AffineTransform orig = g.getTransform();
            g.setTransform(Screen.getTransform(transform));
            g.translate(-g.getTransform().getTranslateX(), -g.getTransform().getTranslateY());
            Point pos = Screen.calculateScreenPosition(transform);
            final float width = transform.scale.width;
            final float height = transform.scale.height;

            BufferedImage render = getTextureRaw(transform.scale.width, transform.scale.height);
            Area screen = new Area(new Rectangle2D.Float(0, 0, Screen.width(), Screen.height()));
            Rectangle2D self = new Rectangle2D.Float(1, 1, width, height);
            Vector2 min = new Vector2(pos);
            Area intersection = new Area(self);

            //top
            while (!intersection.isEmpty()) {
                min.subSelf(new Vector2(0, height));
                self = new Rectangle2D.Float(1, min.y, width, height);
                intersection = new Area(g.getTransform().createTransformedShape(self));
                intersection.intersect(screen);
            }

            intersection = new Area(new Rectangle2D.Float(1, 1, width, height));
            //Left
            while (!intersection.isEmpty()) {
                min.subSelf(new Vector2(width, 0));
                self = new Rectangle2D.Float(min.x, 1, width, height);
                intersection = new Area(g.getTransform().createTransformedShape(self));
                intersection.intersect(screen);
            }

            //Draw Images
            for (int x = (int) min.x; x < Screen.width(); x += width)
                for (int y = (int) min.y; y < Screen.height(); y += height)
                    g.drawImage(render, x, y, null);
        }
    }

    public Shape getShape(Animation animation) {
        if (texture != null || animation != null)
            return Shape.RECTANGLE;
        else
            return shape;
    }

    public Sprite resize(int width, int height, Resizer method) {
        return new Sprite(method.resize(texture, width, height), repeat);
    }

    public static void setResizingMethod(Resizer resizingMethod) { Sprite.resizingMethod = resizingMethod; }
}
