package de.craftix.engine.ui;

import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Shape;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.ui.components.UIComponent;
import de.craftix.engine.render.Mesh;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.io.Serializable;
import java.util.ArrayList;

public class UIElement extends ScreenObject implements Serializable {
    protected UIAlignment alignment;
    protected boolean renderObject = true;
    protected final ArrayList<UIComponent> components = new ArrayList<>();

    public UIElement(Sprite sprite, Transform transform,  UIAlignment alignment) {
        this.transform = transform;
        this.sprite = sprite;
        this.alignment = alignment;
    }
    public UIElement(Mesh mesh, Transform transform, UIAlignment alignment) {
        this.transform = transform;
        this.mesh = mesh;
        this.alignment = alignment;
    }

    public void render(Graphics2D g) {
        if (renderObject) {
            AffineTransform original = (AffineTransform) g.getTransform().clone();
            Vector2 pos = alignment.getScreenPosition(transform);
            g.translate(pos.x + (transform.scale.width / 2f), pos.y + (transform.scale.height / 2f));
            g.rotate(transform.rotation.getAngle(), 0, 0);

            if (sprite != null) {
                sprite.renderRaw(g, transform);
            }else {
                mesh.render(g, false, transform);
            }

            g.setTransform(original);
        }

        for (UIComponent component : components)
            component.render(g);
    }

    public void addComponent(UIComponent component) { component.initialise(this); components.add(component); }
    public void removeComponent(Class<? extends UIComponent> component) {
        for (UIComponent all : components)
            if (all.getClass() == component) {
                components.remove(all);
                return;
            }
    }
    public boolean hasComponent(Class<? extends UIComponent> component) {
        for (UIComponent all : components) {
            if (all.getClass() == component) return true;
        }
        return false;
    }
    public UIComponent getComponent(Class<? extends UIComponent> component) {
        for (UIComponent all : components) {
            if (all.getClass() == component) return all;
        }
        return null;
    }
    public UIComponent[] getComponents() { return components.toArray(new UIComponent[0]); }

    public Area getShape() {
        AffineTransform at = new AffineTransform();
        Vector2 pos = alignment.getScreenPosition(transform);
        at.rotate(transform.rotation.getAngle(), pos.x + (transform.scale.width / 2f), pos.y + (transform.scale.height / 2f));
        at.translate(pos.x + (transform.scale.width / 2f), pos.y + (transform.scale.height / 2f));
        return new Area(at.createTransformedShape(new Mesh(Shape.RECTANGLE, Color.BLACK).getMesh(false, transform)));
    }

    public UIAlignment getAlignment() { return alignment; }
    public boolean renderObject() { return renderObject; }

    public void setAlignment(UIAlignment alignment) { this.alignment = alignment; }
    public void renderObject(boolean renderObject) { this.renderObject = renderObject; }
}
