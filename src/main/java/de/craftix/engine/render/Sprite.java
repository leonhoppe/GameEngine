package de.craftix.engine.render;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

public class Sprite {

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

    public BufferedImage getTexture(int width, int height) {
        if (texture != null) {
            if (texture.getWidth() != width || texture.getHeight() != height) {
                if (bufferedTexture == null ||
                        bufferedOriginal != texture ||
                        bufferedTexture.getWidth() != width || bufferedTexture.getHeight() != height) {
                    bufferedOriginal = texture;
                    bufferedTexture = Resizer.AVERAGE.resize(texture, width, height);
                }
                return bufferedTexture;
            }
        }
        return texture;
    }

}
