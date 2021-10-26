package de.craftix.engine.ui;

import de.craftix.engine.render.MShape;
import de.craftix.engine.render.Mesh;
import de.craftix.engine.var.Transform;

import java.awt.*;

public abstract class UIContainer extends UIElement {
    private boolean autoDimensions = false;

    public UIContainer(Transform transform, UIAlignment alignment) {
        this.transform = transform;
        this.alignment = alignment;
        this.renderObject = false;
        if (transform.scale.width == 0 || transform.scale.height == 0) autoDimensions = true;
    }

    public abstract Transform getTransform(UIElement element);

    @Override
    public void update() {
        if (autoDimensions) transform.scale = getContainer().scale;
    }

    public void setBackgroundColor(Color color) {
        mesh = new Mesh(MShape.RECTANGLE, color);
        renderObject = true;
    }
}
