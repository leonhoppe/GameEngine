package de.craftix.engine.objects;

import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.var.Animation;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class TextureObject extends ScreenObject {
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

    @Override
    protected void render(Graphics2D g) {
        AffineTransform original = g.getTransform();
        g.translate(transform.position.x + (transform.scale.width / 2f), transform.position.y + (transform.scale.height / 2f));
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
}
