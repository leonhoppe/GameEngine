package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.var.Animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

public class Sprite {
    private static Resizer resizingMethod = null;

    public static Sprite load(String path) {
        try {
            return new Sprite(ImageIO.read(Objects.requireNonNull(Sprite.class.getClassLoader().getResource(path))));
        }catch (Exception e) { e.printStackTrace(); }
        return new Sprite();
    }
    public static Sprite loadExt(String path) {
        try {
            return new Sprite(ImageIO.read(new File(path)));
        }catch (Exception e) { e.printStackTrace(); }
        return new Sprite();
    }

    public Color color;
    public Shape shape;

    public BufferedImage texture;
    public BufferedImage bufferedTexture;
    public BufferedImage bufferedOriginal;

    public Sprite(Color color, Shape shape) { this.color = color; this.shape = shape; }
    public Sprite(BufferedImage texture) { this.texture = texture; }
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

    public Shape getShape(Animation animation) {
        if (texture != null || animation != null)
            return Shape.RECTANGLE;
        else
            return shape;
    }

    public static void setResizingMethod(Resizer resizingMethod) { Sprite.resizingMethod = resizingMethod; }

}
