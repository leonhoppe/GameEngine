package de.craftix.engine.ui.elements;

import de.craftix.engine.InputManager;
import de.craftix.engine.render.Screen;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.components.UIInteractionComponent;
import de.craftix.engine.ui.components.UITextComponent;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class UICheckBox extends UIElement implements ActionListener {

    private boolean checked;
    private Color boxColor;
    private String text;
    private Font textFont;
    private Color textColor;

    private Dimension textBounds;
    private Dimension fullBounds;

    private ActionListener listener;

    public UICheckBox(boolean checked, String text, Transform transform, UIAlignment alignment) {
        this.renderObject = false;
        this.checked = checked;
        this.text = text;
        this.transform = transform;
        this.alignment = alignment;
        this.boxColor = Color.BLACK;
        this.textFont = new Font("Arial", Font.PLAIN, 11);
        this.textColor = Color.BLACK;
        updateText();
        addComponent(new UIInteractionComponent(this, this));
    }

    private void updateText() {
        BufferedImage temp = new BufferedImage(Screen.width(), Screen.height(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) temp.getGraphics();
        FontRenderContext render = g.getFontRenderContext();
        GlyphVector vector = textFont.createGlyphVector(render, text);
        Rectangle bounds = vector.getPixelBounds(null, 0, 0);
        textBounds = new Dimension(bounds.width, bounds.height);

        removeComponent(UITextComponent.class);
        UITextComponent textComponent = new UITextComponent(text, textFont, textColor, true);
        addComponent(textComponent);
    }

    public void setValueChangedListener(ActionListener listener) { this.listener = listener; }
    public void setColor(Color color) { this.boxColor = color; }
    public void setText(String text) { this.text = text; updateText(); }
    public void setFont(Font font) { this.textFont = font; updateText(); }
    public void setTextColor(Color color) { this.textColor = color; updateText(); }
    public void setChecked(boolean checked) { this.checked = checked; }
    public boolean isChecked() { return checked; }

    @Override
    public void render(Graphics2D g) {
        fullBounds = new Dimension(textBounds.width + 10 + transform.scale.width, Mathf.max(textBounds.height, transform.scale.height));
        applyTransform(g);
        g.translate(transform.scale.width / 2f + 5, 0);
        getComponent(UITextComponent.class).render(g);
        g.translate(-textBounds.width / 2f - transform.scale.width - 10, -(transform.scale.height / 2f));
        g.setColor(Color.BLACK);
        g.draw(new Rectangle2D.Float(0, 0, transform.scale.width, transform.scale.height));
        if (checked) {
            g.setColor(boxColor);
            g.fill(new Rectangle2D.Float(2, 2, transform.scale.width - 4, transform.scale.height - 4));
        }
    }

    @Override
    public Area getScreenShape() {
        Vector2 pos = alignment.getScreenPosition(new Transform(transform.position, fullBounds, transform.rotation), getContainer());
        return new Area(new Rectangle2D.Float(pos.x, pos.y, fullBounds.width, fullBounds.height));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("click")) {
            checked = !checked;
            if (listener != null)
                listener.actionPerformed(new ActionEvent(this, 0, "ValueChanged"));
        }
        else if (e.getActionCommand().equals("start")) {
            InputManager.setCursor(Cursor.HAND_CURSOR);
        }
        else if (e.getActionCommand().equals("stop")) {
            InputManager.setCursor(Cursor.DEFAULT_CURSOR);
        }
    }
}
