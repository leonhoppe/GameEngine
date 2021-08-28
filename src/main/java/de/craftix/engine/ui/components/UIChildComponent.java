package de.craftix.engine.ui.components;

import de.craftix.engine.GameEngine;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIChildComponent extends UIComponent {
    private final ArrayList<UIElement> children = new ArrayList<>();

    @Override
    public void render(Graphics2D g) {
        AffineTransform orig = g.getTransform();
        Vector2 pos = element.getAlignment().getScreenPosition(element.transform);
        g.rotate(element.transform.rotation.getAngle(), pos.x + (element.transform.scale.width / 2f), pos.y + (element.transform.scale.height / 2f));
        List<Float> sortedLayers = (List<Float>) GameEngine.getScene().getUIManager().getLayers().clone();
        Collections.sort(sortedLayers);

        for (float layer : sortedLayers) {
            for (UIElement child : children) {
                if (child.getLayer() != layer) continue;
                Transform original = child.transform.copy();
                Transform self = element.transform.copy();
                self.scale = new Dimension();
                child.transform.position.add(self.position);
                child.render(g);
                child.transform = original;
            }
        }

        g.setTransform(orig);
    }

    public void addChild(UIElement child) { children.add(child); }
    public void removeChild(UIElement child) { children.remove(child); }
    public ArrayList<UIElement> getChildren() { return children; }
}
