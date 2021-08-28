package de.craftix.engine.objects;

import de.craftix.engine.GameEngine;
import de.craftix.engine.objects.components.Component;
import de.craftix.engine.objects.components.RenderingComponent;
import de.craftix.engine.render.Mesh;
import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.var.*;
import org.apache.commons.lang.SerializationUtils;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameObject extends ScreenObject implements Serializable {
    private final List<Component> components = new ArrayList<>();
    private boolean renderObject = true;
    private final String name;

    public GameObject(String name, Sprite sprite, Transform transform) {
        super();
        this.sprite = sprite;
        this.transform = transform;
        visible = true;
        this.name = name;
    }
    public GameObject(String name, Mesh mesh, Transform transform) {
        super();
        this.sprite = null;
        this.mesh = mesh;
        this.transform = transform;
        visible = true;
        this.name = name;
    }
    protected GameObject() { super(); this.name = null; }

    public void addComponent(Component component) {
        components.add(component);
        component.initialise(this);
    }
    public Component[] getComponents() { return components.toArray(new Component[0]); }

    public void removeComponent(Class<? extends Component> component) {
        for (Component all : components) {
            if (all.getClass() == component) {
                components.remove(all);
                return;
            }
        }
    }
    public boolean hasComponent(Class<? extends Component> component) {
        for (Component all : components) {
            if (all.getClass() == component) return true;
        }
        return false;
    }
    public <T extends Component> T getComponent(Class<T> component) {
        for (Component all : components) {
            if (all.getClass() == component) return (T) all;
        }
        return null;
    }
    public <T extends Component> T[] getComponents(Class<T> component) {
        List<T> comps = new ArrayList<>();
        for (Component all : components) {
            if (all.getClass() == component) comps.add((T) all);
        }
        return comps.toArray(component.getEnumConstants());
    }

    @Override
    public void render(Graphics2D g) {
        if (renderObject)
            super.render(g);

        for (Component c : getComponents()) {
            if (!(c instanceof RenderingComponent)) continue;
            ((RenderingComponent) c).render(g);
        }
    }

    public void setLayer(String layer) { this.layer = GameEngine.getLayer(layer); }
    public void setAnimation(Animation animation) { this.animation = animation; }
    public void setSprite(Sprite texture) { this.sprite = texture; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void renderObject(boolean renderObject) { this.renderObject = renderObject; }

    public String getName() { return name; }
}
