package de.craftix.engine.ui;

import de.craftix.engine.render.Screen;

import java.awt.*;
import java.awt.geom.Area;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class UIManager implements Serializable {

    private ArrayList<UIElement> elements = new ArrayList<>();
    private ArrayList<Float> layers = new ArrayList<>();

    public UIManager() { layers.add(-10f); layers.add(0f); layers.add(10f); }

    public void addElement(UIElement component) { elements.add(component); }
    public void removeElement(UIElement component) { elements.remove(component); }
    public boolean containsElement(UIElement component) { return elements.contains(component); }
    public void removeElement() { elements.clear(); }
    public ArrayList<UIElement> getElements() { return elements; }

    public void addLayer(float layer) { layers.add(layer); }
    public void removeLayer(float layer) { layers.remove(layer); }
    public ArrayList<Float> getLayers() { return layers; }

    public void renderComponents(Graphics2D g) {
        List<Float> sortedLayers = (List<Float>) layers.clone();
        Collections.sort(sortedLayers);

        Area screen = new Area(new Rectangle(0, 0, Screen.width(), Screen.height()));
        for (Float layer : sortedLayers) {
            for (UIElement element : elements) {
                if (!layer.equals(element.getLayer())) continue;
                Area area = element.getShape();
                area.intersect(screen);
                if (area.isEmpty()) continue;
                element.render(g);
            }
        }
    }

}
