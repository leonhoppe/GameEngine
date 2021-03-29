package de.craftix.engine.objects.components;

import de.craftix.engine.render.Screen;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
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
        object.renderObject(!onlyText);
        Font ram = g.getFont();
        AffineTransform original = g.getTransform();
        AffineTransform trans = Screen.getTransform(object.transform);
        g.setTransform(trans);
        g.setColor(color);
        g.setFont(font);

        if (text.contains("\n")) {
            FontRenderContext context = g.getFontRenderContext();
            TextLayout layout = new TextLayout(text, font, context);

            Vector2 middle = new Vector2();
            Rectangle2D bounds = layout.getBounds();
            String[] texts = text.split("\n");
            middle.x -= (bounds.getWidth() / texts.length) / 2f;

            if (texts.length > 1)
                middle.y -= (bounds.getHeight() * texts.length) / 4f;
            else
                middle.y += (bounds.getHeight() * texts.length) / 4f;

            for (int i = 0; i < texts.length; i++) {
                g.drawString(texts[i], middle.x, ((float) (middle.y + (i * layout.getBounds().getHeight()))) + i);
            }
        }else {
            Vector2 middle = new Vector2();
            FontRenderContext context = new FontRenderContext(new AffineTransform(), Screen.antialiasing(), true);
            Rectangle2D textBounds = font.getStringBounds(text, context);
            middle.x -= textBounds.getWidth() / 2f;
            middle.y += textBounds.getHeight() / 4f;
            g.drawString(text, middle.x, middle.y);
        }

        g.setFont(ram);
        g.setTransform(original);
    }

    public void setText(String text) { this.text = text; }
    public void setOnlyText(boolean onlyText) { this.onlyText = onlyText; }
    public void setFont(Font font) { this.font = font; }
    public void setColor(Color color) { this.color = color; }
}
