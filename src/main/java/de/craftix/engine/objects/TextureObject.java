package de.craftix.engine.objects;

import de.craftix.engine.GameEngine;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.var.Animation;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

public class TextureObject extends ScreenObject implements Serializable {
    private boolean scaleAffected = false;

    public TextureObject(Sprite texture, Point position, Dimension size) {
        super();
        this.visible = true;
        this.sprite = texture;
        this.transform.position = new Vector2(position);
        this.transform.scale = size;
    }
    public TextureObject(Animation animation, Point position, Dimension size) {
        super();
        this.visible = true;
        this.animation = animation;
        this.transform.position = new Vector2(position);
        this.transform.scale = size;
    }

    public void setSprite(Sprite texture) { this.sprite = texture; }
    public void setAnimation(Animation animation) { this.animation = animation; }
    public void setScaleAffected(boolean scaleAffected) { this.scaleAffected = scaleAffected; }

    protected void render(Graphics2D g) {
        AffineTransform original = g.getTransform();

        if (sprite.texture == null && sprite.color != null && animation == null) {
            g.setColor(sprite.color);
            if (scaleAffected) g.fill(getScreenShape());
            else g.fill(getShape());
        }else {
            g.translate(transform.position.x + (transform.scale.width / 2f), transform.position.y + (transform.scale.height / 2f));
            g.rotate(transform.rotation.getAngle(), 0, 0);
        }

        if (sprite.texture != null && (animation == null || !animation.isRunning())) {
            if (scaleAffected)
                g.drawImage(sprite.getTexture(transform.scale.getWidth(), transform.scale.getHeight()), (int) -((transform.scale.width * GameEngine.getCamera().getScale()) / 2f), (int) -(transform.scale.height * GameEngine.getCamera().getScale() / 2), null);
            else
                g.drawImage(sprite.getTextureRaw(transform.scale.getWidth(), transform.scale.getHeight()), (int) -(transform.scale.width / 2f), (int) -(transform.scale.height / 2f), null);
        }

        if (animation != null) {
            if (scaleAffected)
                g.drawImage(animation.getImage().getTexture(transform.scale.getWidth(), transform.scale.getHeight()), (int) -((transform.scale.width * GameEngine.getCamera().getScale()) / 2f), (int) (-transform.scale.height * GameEngine.getCamera().getScale() / 2), null);
            else
                g.drawImage(animation.getImage().getTextureRaw(transform.scale.getWidth(), transform.scale.getHeight()), (int) -(transform.scale.width / 2f), (int) -(transform.scale.height / 2f), null);
        }

        g.setColor(Color.BLACK);
        g.setTransform(original);
    }

    public Area getShape() {
        return new Area(Screen.getRawTransform(transform).createTransformedShape(getRawShape()));
    }
    public Area getScreenShape() {
        if (scaleAffected) {
            Shape shape = null;
            if (sprite.texture != null || animation != null)
                shape = new Rectangle((int) (-(transform.scale.width * GameEngine.getCamera().getScale()) / 2f), (int) (-(transform.scale.height * GameEngine.getCamera().getScale()) / 2f),
                        (int) (transform.scale.width * GameEngine.getCamera().getScale()), (int) (transform.scale.height * GameEngine.getCamera().getScale()));
            else
                switch (sprite.shape) {
                    case CIRCLE:
                        shape = new Ellipse2D.Float(-(transform.scale.width * GameEngine.getCamera().getScale()) / 2f, -(transform.scale.height * GameEngine.getCamera().getScale()) / 2f,
                                transform.scale.width * GameEngine.getCamera().getScale(), transform.scale.height * GameEngine.getCamera().getScale());
                        break;
                    case RECTANGLE:
                        shape = new Rectangle((int) (-(transform.scale.width * GameEngine.getCamera().getScale()) / 2f), (int) (-(transform.scale.height * GameEngine.getCamera().getScale()) / 2f),
                                (int) (transform.scale.width * GameEngine.getCamera().getScale()), (int) (transform.scale.height * GameEngine.getCamera().getScale()));
                        break;
                    case TRIANGLE:
                        Point top = new Point(0, (int) (-(transform.scale.height * GameEngine.getCamera().getScale()) / 2f));
                        Point right = new Point((int) (-(transform.scale.width * GameEngine.getCamera().getScale()) / 2f),
                                (int) ((transform.scale.height * GameEngine.getCamera().getScale()) / 2f));
                        Point left = new Point((int) ((transform.scale.width * GameEngine.getCamera().getScale()) / 2f),
                                (int) ((transform.scale.height * GameEngine.getCamera().getScale()) / 2));
                        shape = new Polygon(new int[]{top.x, right.x, left.x},
                                new int[]{top.y, right.y, left.y},
                                3);
                        break;
                }

            return new Area(Screen.getRawTransform(transform).createTransformedShape(shape));
        }else
            return getShape();
    }
}
