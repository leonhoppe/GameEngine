package de.craftix.engine.ui;

import java.awt.*;
import java.io.Serializable;

public abstract class UIComponent implements Serializable {

    protected UIElement element;

    public abstract void render(Graphics2D g);

    protected void initialise(UIElement element) { this.element = element; }

    public UIElement getElement() { return element; }
}
