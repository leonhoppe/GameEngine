package de.craftix.engine.objects;

import de.craftix.engine.GameEngine;
import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.var.Animation;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameObject extends ScreenObject {
    private final List<Component> components = new ArrayList<>();

    public GameObject(Sprite sprite, Vector2 position) {
        super();
        this.sprite = sprite;
        transform.position = position;
        if (sprite.texture != null) {
            transform.scale.width = sprite.texture.getWidth();
            transform.scale.height = sprite.texture.getHeight();
        }
        visible = true;
    }
    public GameObject(Sprite sprite, Vector2 position, Dimension size) {
        this(sprite, position);
        this.transform.scale = size;
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

    public void addComponent(Component component) {
        if (hasComponent(component.getClass())) return;
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
    public Component getComponent(Class<? extends Component> component) {
        for (Component all : components) {
            if (all.getClass() == component) return all;
        }
        return null;
    }

    public GameObject copy() {
        GameObject copy = new GameObject(super.copy());
        for (Component component : components) copy.addComponent(component.copy(copy));
        return copy;
    }

    public void setLayer(String layer) { this.layer = GameEngine.getLayer(layer); }
    public void setAnimation(Animation animation) { this.animation = animation; }
    public void setSprite(Sprite texture) { this.sprite = texture; }
    public void setVisible(boolean visible) { this.visible = visible; }
}
