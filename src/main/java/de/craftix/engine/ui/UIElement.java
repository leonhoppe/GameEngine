package de.craftix.engine.ui;

import de.craftix.engine.render.*;
import de.craftix.engine.ui.components.UIComponent;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Quaternion;
import de.craftix.engine.var.Scene;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class UIElement extends ScreenObject implements Serializable {
    protected UIManager manager;
    protected UIAlignment alignment;
    protected UIContainer container;
    protected boolean renderObject = true;
    private boolean initialised = false;
    protected final ArrayList<UIComponent> components = new ArrayList<>();

    protected UIElement() { alignment = UIAlignment.CENTER; transform = new Transform(); mesh = new Mesh(MShape.RECTANGLE, Color.BLACK); }
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

    public UIElement(Sprite sprite, Transform transform,  UIAlignment alignment, UIContainer container) {
        this.transform = transform;
        this.sprite = sprite;
        this.alignment = alignment;
        this.container = container;
    }
    public UIElement(Mesh mesh, Transform transform, UIAlignment alignment, UIContainer container) {
        this.transform = transform;
        this.mesh = mesh;
        this.alignment = alignment;
        this.container = container;
    }

    public void render(Graphics2D g) {
        if (!initialised) return;
        if (transform.rotation.getAngle() >= Math.PI * 2) transform.rotation = new Quaternion(transform.rotation.getAngle() % Math.toRadians(360));
        applyTransform(g);
        if (renderObject) {
            if (sprite != null) {
                sprite.renderRaw(g, transform);
            }else {
                mesh.render(g, false, transform);
            }
        }

        for (int i = 0; i < components.size(); i++)
            components.get(i).render(g);
    }

    public Transform getContainer() {
        if (!initialised) return new Transform(new Dimension(Screen.width(), Screen.height()));
        return container != null ? container.getTransform(this) : new Transform(new Dimension(Screen.width(), Screen.height()));
    }

    public void initialise(Scene scene, UIManager manager) {
        super.initialise(scene);
        this.manager = manager;
        if (container == null && manager.getRootContainer() != this) container = manager.getRootContainer();
        initialised = true;
    }

    protected void applyTransform(Graphics2D g) {
        AffineTransform original = (AffineTransform) g.getTransform().clone();
        Vector2 pos = alignment.getScreenPosition(transform, getContainer());
        g.translate(pos.x + (transform.scale.width / 2f), pos.y + (transform.scale.height / 2f));
        g.rotate(transform.rotation.getAngle(), 0, 0);
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
    public <T extends UIComponent> T getComponent(Class<? extends UIComponent> component) {
        for (UIComponent all : components) {
            if (all.getClass() == component) return (T) all;
        }
        return null;
    }
    public UIComponent[] getComponents() { return components.toArray(new UIComponent[0]); }

    public Area getShape() {
        AffineTransform at = new AffineTransform();
        Vector2 pos = alignment.getScreenPosition(transform, getContainer());
        at.rotate(transform.rotation.getAngle(), pos.x + (transform.scale.width / 2f), pos.y + (transform.scale.height / 2f));
        at.translate(pos.x + (transform.scale.width / 2f), pos.y + (transform.scale.height / 2f));
        return new Area(at.createTransformedShape(new Mesh(MShape.RECTANGLE, Color.BLACK).getMesh(false, transform)));
    }

    public UIAlignment getAlignment() { return alignment; }
    public boolean renderObject() { return renderObject; }

    public void setAlignment(UIAlignment alignment) { this.alignment = alignment; }
    public void setContainer(UIContainer container) { this.container = container; }
    public void renderObject(boolean renderObject) { this.renderObject = renderObject; }

    @Override
    public Area getScreenShape() {
        Vector2 pos = alignment.getScreenPosition(transform, getContainer());
        Area area;
        area = Objects.requireNonNullElseGet(mesh, () -> new Mesh(MShape.RECTANGLE, Color.BLACK)).getMesh(false, transform);
        AffineTransform trans = new AffineTransform();
        trans.translate(pos.x, pos.y);
        trans.rotate(transform.rotation.getAngle(), transform.scale.width / 2f, transform.scale.height / 2f);
        trans.translate(transform.scale.width / 2f, transform.scale.height / 2f);
        return new Area(trans.createTransformedShape(area));
    }
}
