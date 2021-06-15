package de.craftix.engine.objects;

import de.craftix.engine.GameEngine;
import de.craftix.engine.objects.components.Component;
import de.craftix.engine.objects.components.RenderingComponent;
import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.var.*;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameObject extends ScreenObject implements Serializable {
    private final List<de.craftix.engine.objects.components.Component> components = new ArrayList<>();
    private boolean renderObject = true;

    public GameObject(Sprite sprite, Transform transform) {
        super();
        this.sprite = sprite;
        this.transform = transform;
        visible = true;
    }
    public GameObject(Mesh mesh, Transform transform) {
        super();
        this.mesh = mesh;
        this.transform = transform;
        visible = true;
    }
    protected GameObject() { super(); }
    private GameObject(ScreenObject object) {
        super();
        this.transform = object.transform;
        this.sprite = object.getSprite();
        this.layer = object.getLayer();
        this.visible = object.isVisible();
        this.animation = object.getAnimation();
    }

    public void addComponent(de.craftix.engine.objects.components.Component component) {
        components.add(component);
        component.initialise(this);
    }
    public de.craftix.engine.objects.components.Component[] getComponents() { return components.toArray(new de.craftix.engine.objects.components.Component[0]); }

    public void removeComponent(Class<? extends de.craftix.engine.objects.components.Component> component) {
        for (de.craftix.engine.objects.components.Component all : components) {
            if (all.getClass() == component) {
                components.remove(all);
                return;
            }
        }
    }
    public boolean hasComponent(Class<? extends de.craftix.engine.objects.components.Component> component) {
        for (de.craftix.engine.objects.components.Component all : components) {
            if (all.getClass() == component) return true;
        }
        return false;
    }
    public de.craftix.engine.objects.components.Component getComponent(Class<? extends de.craftix.engine.objects.components.Component> component) {
        for (de.craftix.engine.objects.components.Component all : components) {
            if (all.getClass() == component) return all;
        }
        return null;
    }

    public GameObject copy() {
        GameObject copy = new GameObject(super.copy());
        for (de.craftix.engine.objects.components.Component component : components) copy.addComponent(component.copy(copy));
        return copy;
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
}
