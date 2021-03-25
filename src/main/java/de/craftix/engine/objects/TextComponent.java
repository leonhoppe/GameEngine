package de.craftix.engine.objects;

import de.craftix.engine.objects.RenderingComponent;
import de.craftix.engine.render.Screen;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class TextComponent extends RenderingComponent {
    private String text;
    private Font font;
    private Color color;
    private boolean onlyText;

    public TextComponent(String text, Font font, Color color, boolean onlyText)
    { this.text = text; this.font = font; this.onlyText = onlyText; this.color = color; }

    @Override
    public void render(Graphics2D g) {
        if (!onlyText)
            object.render(g);

        AffineTransform original = g.getTransform();
        AffineTransform trans = Screen.getTransform(object.transform);
        g.setTransform(trans);
        g.setColor(color);
        Vector2 middle = new Vector2();
        FontRenderContext context = new FontRenderContext(new AffineTransform(), true, true);
        Rectangle2D textBounds = font.getStringBounds(text, context);
        middle.x -= textBounds.getWidth() / 2f;
        middle.y += textBounds.getHeight() / 4f;
        Font ram = g.getFont();
        g.setFont(font);
        g.drawString(text, middle.x, middle.y);
        g.setFont(ram);
        g.setTransform(original);
    }

    public void setText(String text) { this.text = text; }
    public void setOnlyText(boolean onlyText) { this.onlyText = onlyText; }
    public void setFont(Font font) { this.font = font; }
}
