package de.craftix.engine.ui;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class UIManager implements Serializable {

    private ArrayList<UIComponent> components = new ArrayList<>();
    private ArrayList<Float> layers = new ArrayList<>();

    public UIManager() { layers.add(-10f); layers.add(0f); layers.add(10f); }

    public void addComponent(UIComponent component) { components.add(component); }
    public void removeComponent(UIComponent component) { components.remove(component); }
    public boolean containsComponent(UIComponent component) { return components.contains(component); }
    public void removeComponents() { components.clear(); }
    public ArrayList<UIComponent> getComponents() { return components; }

    public void addLayer(float layer) { layers.add(layer); }
    public void removeLayer(float layer) { layers.remove(layer); }
    public ArrayList<Float> getLayers() { return layers; }

    public void renderComponents(Graphics2D g) {
        List<Float> sortedLayers = (List<Float>) layers.clone();
        Collections.sort(sortedLayers);

        for (Float layer : sortedLayers) {
            for (UIComponent component : components) {
                if (!layer.equals(component.layer)) continue;
                component.render(g);
            }
        }
    }

}
