package de.craftix.engine.ui;

import de.craftix.engine.render.Screen;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.var.Animation;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

public class UIComponent implements Serializable {

    public ElementAlignment alignment;
    public Sprite sprite;
    public Animation animation;
    public Transform transform;
    public float layer;

    public UIComponent(Transform transform, Sprite sprite, ElementAlignment alignment) {
        this.transform = transform;
        this.sprite = sprite;
        this.alignment = alignment;
    }

    public void render(Graphics2D g) {
        AffineTransform original = g.getTransform();
        g.setTransform(Screen.getRawTransform(transform));
        g.translate(-(transform.position.x + (transform.scale.width / 2f)), -(transform.position.y + (transform.scale.height / 2f)));

        Vector2 pos = alignment.getScreenPosition(transform);
        if (animation != null) {
            g.drawImage(animation.getImage().getTextureRaw(transform.scale.width, transform.scale.height), pos.getX(), pos.getY(), null);
        }else if (sprite.texture != null) {
            g.drawImage(sprite.getTextureRaw(transform.scale.width, transform.scale.height), pos.getX(), pos.getY(), null);
        }else {
            g.setColor(sprite.color);
            //Render Shape
        }

        g.setTransform(original);
    }

}
