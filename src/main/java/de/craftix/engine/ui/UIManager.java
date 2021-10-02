package de.craftix.engine.ui;

import de.craftix.engine.render.Screen;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.var.Scene;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class UIManager implements Serializable {

    private ArrayList<UIElement> elements = new ArrayList<>();
    private ArrayList<Float> layers = new ArrayList<>();
    private Scene scene;

    public UIManager(Scene scene) { layers.add(-10f); layers.add(0f); layers.add(10f); this.scene = scene; }

    public void addElement(UIElement component) { component.initialise(scene); elements.add(component); }
    public void removeElement(UIElement component) { elements.remove(component); }
    public boolean containsElement(UIElement component) { return elements.contains(component); }
    public void removeElements() { elements.clear(); }
    public ArrayList<UIElement> getElements() { return elements; }
    public UIElement[] getElementTypes(Class<? extends UIElement> elementType) {
        ArrayList<UIElement> typeElements = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            UIElement element = elements.get(i);
            if (element.getClass() == elementType)
                typeElements.add(element);
        }
        return typeElements.toArray(new UIElement[0]);
    }

    public void addLayer(float layer) { layers.add(layer); }
    public void removeLayer(float layer) { layers.remove(layer); }
    public ArrayList<Float> getLayers() { return layers; }

    public void renderComponents(Graphics2D g) {
        List<Float> sortedLayers = (List<Float>) layers.clone();
        Collections.sort(sortedLayers);

        Area screen = new Area(new Rectangle(0, 0, Screen.width(), Screen.height()));
        for (Float layer : sortedLayers) {
            for (int i = 0; i < elements.size(); i++) {
                UIElement element = elements.get(i);
                if (!element.isVisible()) continue;
                if (!layer.equals(element.getLayer())) continue;
                Area area = element.getShape();
                area.intersect(screen);
                if (area.isEmpty()) continue;
                AffineTransform orig = g.getTransform();
                element.render(g);
                g.setTransform(orig);
            }
        }
    }

}
