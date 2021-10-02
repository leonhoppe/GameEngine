package de.craftix.engine.ui.elements;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.render.Mesh;
import de.craftix.engine.render.MShape;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.components.UIInteractionComponent;
import de.craftix.engine.ui.components.UITextComponent;
import de.craftix.engine.var.Transform;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIButton extends UIElement implements ActionListener {

    private UIInteractionComponent interactionComponent;
    private UITextComponent textComponent;

    private Color innerColor = new Color(0, 0, 0, 100);
    private Color outerColor = Color.BLACK;
    private Color hoverColor = new Color(0, 0, 0, 150);
    private Color clickColor = new Color(0, 0, 0, 80);

    private String text = "";
    private Font font = new Font("Arial", Font.PLAIN, 11);
    private Color textColor = Color.WHITE;

    private ActionListener clickListener;
    private Cursor hoverCursor;

    public UIButton(Transform transform, UIAlignment alignment) {
        this.transform = transform;
        this.alignment = alignment;
        mesh = new Mesh(MShape.RECTANGLE, innerColor);
        interactionComponent = new UIInteractionComponent(this, this);
        addComponent(interactionComponent);
        hoverCursor = new Cursor(Cursor.HAND_CURSOR);
    }
    public UIButton(String text, Transform transform, UIAlignment alignment) {
        this(transform, alignment);
        this.text = text;
        this.textComponent = new UITextComponent(this.text, font, textColor, false);
        addComponent(this.textComponent);
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);
        g.setColor(outerColor);
        g.draw(mesh.getMesh(false, transform));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("start")) {
            mesh.setColors(hoverColor);
            InputManager.setCursor(hoverCursor);
        }
        else if (e.getActionCommand().equals("stop")) {
            mesh.setColors(innerColor);
            InputManager.setCursor(Cursor.DEFAULT_CURSOR);
        }
        else if (e.getActionCommand().equals("click")) {
            if (clickListener != null) clickListener.actionPerformed(new ActionEvent(this, 0, "click"));
            new Thread(() -> {
                mesh.setColors(clickColor);
                try { Thread.sleep(100); } catch (InterruptedException ex) { GameEngine.throwError(ex); }
                if (interactionComponent.isHovering()) mesh.setColors(hoverColor);
                else mesh.setColors(innerColor);
            }).start();
        }
    }

    public void setClickListener(ActionListener clickListener) { this.clickListener = clickListener; }
    public void setInnerColor(Color innerColor) { this.innerColor = innerColor; }
    public void setOuterColor(Color outerColor) { this.outerColor = outerColor; }
    public void setHoverColor(Color hoverColor) { this.hoverColor = hoverColor; }
    public void setClickColor(Color clickColor) { this.clickColor = clickColor; }
    public void setText(String text) { this.text = text; updateText(); }
    public void setFont(Font font) { this.font = font; updateText(); }
    public void setTextColor(Color textColor) { this.textColor = textColor; updateText(); }
    public void setHoverCursor(Cursor hoverCursor) { this.hoverCursor = hoverCursor; }

    private void updateText() {
        removeComponent(UITextComponent.class);
        textComponent = new UITextComponent(text, font, textColor, false);
        addComponent(textComponent);
    }
}
