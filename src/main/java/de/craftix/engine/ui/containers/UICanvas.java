package de.craftix.engine.ui.containers;

import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIContainer;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.var.Transform;

public class UICanvas extends UIContainer {
    public UICanvas(Transform transform, UIAlignment alignment) {
        super(transform, alignment);
    }

    @Override
    public Transform getTransform(UIElement element) {
        Transform trans = transform.copy();
        trans.position = alignment.getScreenPosition(transform, getContainer());
        return trans;
    }
}
