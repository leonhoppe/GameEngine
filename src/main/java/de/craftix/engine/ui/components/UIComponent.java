package de.craftix.engine.ui.components;

import de.craftix.engine.ui.UIElement;

import java.awt.*;
import java.io.Serializable;

public abstract class UIComponent implements Serializable {
    protected UIElement element;

    public abstract void render(Graphics2D g);
    public void fixedUpdate() {}
    public void update() {}

    public void initialise(UIElement element) { this.element = element; }

    public UIElement getElement() { return element; }
}
