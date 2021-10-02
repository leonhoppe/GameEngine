package de.craftix.engine.objects.components;

import de.craftix.engine.GameEngine;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Mesh;
import de.craftix.engine.render.Screen;
import de.craftix.engine.var.*;

import java.awt.geom.Area;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Collider extends Component implements Serializable {
    public static Collider[] calculateCollisions(GameObject object) {
        ArrayList<Collider> collisions = new ArrayList<>();
        for (GameObject other : GameEngine.getScene().getGameObjects()) {
            if (object == other || !other.hasComponent(Collider.class)) continue;
            Collider otherCol = other.getComponent(Collider.class);
            if (otherCol.isTrigger()) continue;
            Area current = object.getShape();
            current.intersect(getArea(otherCol.transform, otherCol.mesh));
            if (!current.isEmpty())
                collisions.add(otherCol);
        }
        return collisions.toArray(new Collider[0]);
    }

    private static Area getArea(Transform transform, Mesh mesh) {
        return new Area(Screen.getTransform(transform).createTransformedShape(mesh.getMesh(true, transform)));
    }

    public Mesh mesh;
    public Transform transform;

    private final boolean trigger;
    private final List<CollisionHandler> handlers = new ArrayList<>();
    private final List<Collider> colliding = new ArrayList<>();
    private final List<Collider> triggering = new ArrayList<>();
    private List<Collider> lastTriggers = new ArrayList<>();
    private List<Collider> lastColliders = new ArrayList<>();

    public Collider(Mesh mesh, boolean isTrigger) { this.mesh = mesh; trigger = isTrigger; }
    public Collider(Mesh mesh, boolean isTrigger, Transform transform) {
        this(mesh, isTrigger);
        this.transform = transform;
    }

    @Override
    public void initialise(GameObject object) {
        super.initialise(object);
        if (transform == null)
            transform = object.transform;
    }

    public void update() {
        update(null);
    }

    protected void update(Object orig) {
        for (GameObject other : GameEngine.getScene().getGameObjects()) {
            if (other == object || other == orig) continue;
            if (!other.hasComponent(Collider.class)) continue;
            Collider otherCol = other.getComponent(Collider.class);
            Area collision = getArea(transform, mesh);
            collision.intersect(getArea(otherCol.transform, otherCol.mesh));
            if (!collision.isEmpty()) {
                if (otherCol.isTrigger()) {
                    boolean triggerEnter = !lastTriggers.contains(otherCol);
                    for (CollisionHandler handler : handlers) {
                        if (triggerEnter) handler.onTriggerEnter(otherCol);
                        handler.onTrigger(otherCol);
                    }
                    triggering.add(otherCol);
                }else {
                    boolean triggerEnter = !lastColliders.contains(otherCol);
                    for (CollisionHandler handler : handlers) {
                        if (triggerEnter) handler.onCollisionEnter(otherCol);
                        handler.onCollision(otherCol);
                    }
                    colliding.add(otherCol);
                }
            }else {
                if (otherCol.isTrigger()) {
                    if (lastTriggers.contains(otherCol)) {
                        for (CollisionHandler handler : handlers)
                            handler.onTriggerExit(otherCol);
                    }
                    triggering.remove(otherCol);
                }else {
                    if (lastColliders.contains(otherCol)) {
                        for (CollisionHandler handler : handlers)
                            handler.onCollisionExit(otherCol);
                    }
                    colliding.remove(otherCol);
                }
            }
        }
        lastTriggers = Arrays.asList(triggering.toArray(new Collider[0]));
        lastColliders = Arrays.asList(colliding.toArray(new Collider[0]));
        colliding.clear();
        triggering.clear();
    }

    public void addCollisionHandler(CollisionHandler handler) { handlers.add(handler); }

    public boolean isTrigger() { return trigger; }

    public boolean isColliding() { return lastColliders.size() >= 1; }
    public boolean isCollidingWithTrigger() { return lastTriggers.size() >= 1; }
}
