package de.craftix.engine.objects.components;

import de.craftix.engine.GameEngine;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Screen;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Transform;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChildComponent extends RenderingComponent {
    private final ArrayList<GameObject> children = new ArrayList<>();

    @Override
    public void render(Graphics2D g) {
        AffineTransform orig = g.getTransform();
        g.setTransform(Screen.getTransform(object.transform));
        g.translate(-g.getTransform().getTranslateX(), -g.getTransform().getTranslateY());
        List<Float> sortedLayers = new ArrayList<>(GameEngine.getLayers().values());
        Collections.sort(sortedLayers);

        for (float layer : sortedLayers) {
            for (GameObject child : children) {
                if (child.getLayer() != layer) continue;
                Transform original = child.transform.copy();
                Transform self = object.transform.copy();
                self.scale = new Dimension();
                child.transform.position.addSelf(self.position);
                child.render(g);
                child.transform = original;
            }
        }

        g.setTransform(orig);
    }

    public void addChild(GameObject child) { children.add(child); }
    public void removeChild(GameObject child) { children.remove(child); }
    public ArrayList<GameObject> getChildren() { return children; }
}
