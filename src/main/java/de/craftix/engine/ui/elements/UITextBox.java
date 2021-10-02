package de.craftix.engine.ui.elements;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.render.Mesh;
import de.craftix.engine.render.MShape;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.components.UIInteractionComponent;
import de.craftix.engine.ui.components.UITextComponent;
import de.craftix.engine.var.Input;
import de.craftix.engine.var.Transform;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

public class UITextBox extends UIElement implements ActionListener {
    private String text = "";
    private Type type;

    private Color backgroundColor = new Color(0, 0, 0, 100);
    private Color borderColor = Color.BLACK;
    private Color textColor = Color.WHITE;
    private Cursor hoverCursor = new Cursor(Cursor.TEXT_CURSOR);
    private Font font = new Font("Arial", Font.PLAIN, 11);
    private Color placeholderColor = Color.LIGHT_GRAY;
    private String placeholder;
    private Color focusColor = Color.WHITE;

    private UITextComponent textComponent;
    private UIInteractionComponent interactionComponent;

    private Cursor origCursor;
    private boolean focus = false;
    private boolean multiline = false;
    private int maxlength = 100;

    public UITextBox(String placeholder, Transform transform, UIAlignment alignment, Type type) {
        this.placeholder = placeholder;
        this.transform = transform;
        this.alignment = alignment;
        this.type = type;
        this.mesh = new Mesh(MShape.RECTANGLE, backgroundColor);
        GameEngine.addInputs(new Inputs(this));
        textComponent = new UITextComponent(this.placeholder, font, placeholderColor, false);
        addComponent(textComponent);
        interactionComponent = new UIInteractionComponent(this, this);
        addComponent(interactionComponent);
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);
        if (!focus) g.setColor(borderColor);
        else g.setColor(focusColor);
        g.draw(mesh.getMesh(false, transform));
    }

    public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }
    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; }
    public void setHoverCursor(Cursor hoverCursor) { this.hoverCursor = hoverCursor; }
    public void setFont(Font font) { this.font = font; updateText(); }
    public void setTextColor(Color textColor) { this.textColor = textColor; updateText(); }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; updateText(); }
    public void setPlaceholderColor(Color placeholderColor) { this.placeholderColor = placeholderColor; updateText(); }
    public void setMultiline(boolean multiline) { this.multiline = multiline; }
    public void setMaxlength(int maxlength) { this.maxlength = maxlength; }
    public void setType(Type type) { this.type = type; }
    public String getText() { return text; }

    private void updateText() {
        removeComponent(UITextComponent.class);
        if (text.equals(""))
            textComponent = new UITextComponent(placeholder, font, placeholderColor, false);
        else textComponent = new UITextComponent(type.getText(text), font, textColor, false);
        addComponent(textComponent);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("start")) {
            origCursor = InputManager.getCursor();
            InputManager.setCursor(hoverCursor);
        }
        else if (e.getActionCommand().equals("stop")) InputManager.setCursor(origCursor);
        else if (e.getActionCommand().equals("click")) {
            focus = true;
            InputManager.setActivated(false);
        }
    }

    private static class Inputs extends Input {
        private static long lastUpdate = System.currentTimeMillis();
        private static final List<Character> additionalAllowedChars = Arrays.asList(' ', 'ß', 'Ä', 'Ö', 'Ü', 'ä', 'ö', 'ü');
        private final UITextBox element;

        public Inputs(UITextBox element) { this.element = element; }

        @Override
        public void mousePressed(MouseEvent e) {
            element.focus = false;
            InputManager.setActivated(true);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (GameEngine.getScene() != element.scene) return;
            if (!element.focus) return;

            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                if (System.currentTimeMillis() - lastUpdate < 100) return;
                UIElement[] elements = GameEngine.getUIManager().getElementTypes(UITextBox.class);
                if (elements.length == 1) return;
                int index = 0;
                for (int i = 0; i < elements.length; i++) {
                    if (elements[i].equals(element)) {
                        index = i + 1;
                        break;
                    }
                }
                if (index >= elements.length) index = 0;
                element.focus = false;
                ((UITextBox) elements[index]).focus = true;
                lastUpdate = System.currentTimeMillis();
            }

            char key = e.getKeyChar();

            if (!element.multiline && key == '\n') return;
            if (key == '\b' && element.text.length() == 0) return;
            if (key == '\b') element.text = element.text.substring(0, element.text.length() - 1);
            if (element.text.length() + 1 > element.maxlength) return;
            if (additionalAllowedChars.contains(key)) element.text += key;
            if (KeyEvent.getKeyText(e.getKeyCode()).length() == 1) element.text += key;

            element.updateText();
        }
    }

    public enum Type {
        TEXT() {
            @Override
            public String getText(String text) {
                return text;
            }
        },
        PASSWORD() {
            @Override
            public String getText(String text) {
                return "*".repeat(text.length());
            }
        };

        public abstract String getText(String text);
    }
}
