package de.craftix.engine.ui.elements;

import de.craftix.engine.render.Screen;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.components.UITextComponent;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Transform;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

public class UIText extends UIElement {

    private String text;
    private Font font;
    private Color color;

    public UIText(String text, Font font, Color color, Transform transform, UIAlignment alignment) {
        renderObject = false;
        this.text = text;
        this.color = color;
        this.font = font;
        this.transform = transform;
        this.alignment = alignment;

        BufferedImage temp = new BufferedImage(Screen.width(), Screen.height(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) temp.getGraphics();
        FontRenderContext render = g.getFontRenderContext();
        GlyphVector vector = font.createGlyphVector(render, text);
        Rectangle bounds = vector.getPixelBounds(null, 0, 0);
        this.transform.scale = new Dimension(bounds.width, bounds.height);

        addComponent(new UITextComponent(text, font, color, true));
    }
    public UIText(String text, Color color, Transform transform, UIAlignment alignment) { this(text, new Font("Arial", Font.PLAIN, 10), color, transform, alignment); }

    private void updateText() {
        removeComponent(UITextComponent.class);
        addComponent(new UITextComponent(text, font, color, true));
    }

    public String getText() { return text; }
    public Font getFont() { return font; }
    public Color getColor() { return color; }

    public void setText(String text) { this.text = text; updateText(); }
    public void setFont(Font font) { this.font = font; updateText(); }
    public void setColor(Color color) { this.color = color; updateText(); }
}
