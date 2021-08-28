package de.craftix.engine.objects.components;

import de.craftix.engine.GameEngine;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Mesh;
import de.craftix.engine.render.Screen;
import de.craftix.engine.var.*;

import java.awt.geom.Area;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Collider extends Component implements Serializable {
    public static Collider[] calculateCollisions(GameObject object) {
        ArrayList<Collider> collisions = new ArrayList<>();
        for (GameObject other : GameEngine.getActiveScene().getGameObjects()) {
            if (object == other || !other.hasComponent(Collider.class)) continue;
            Collider otherCol = (Collider) other.getComponent(Collider.class);
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
    private boolean isColliding;

    private final boolean trigger;
    private final List<CollisionHandler> handlers = new ArrayList<>();
    private final HashMap<Collider, Boolean> colliding = new HashMap<>();

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

    private HashMap<Collider, Boolean> lastFrame = new HashMap<>();
    public void update() {
        isColliding = false;
        for (GameObject other : GameEngine.getActiveScene().getGameObjects()) {
            if (other == object) continue;
            if (!other.hasComponent(Collider.class)) continue;
            Collider otherCol = (Collider) other.getComponent(Collider.class);
            Area collision = getArea(transform, mesh);
            collision.intersect(getArea(otherCol.transform, otherCol.mesh));
            if (!collision.isEmpty()) {
                for (CollisionHandler handler : handlers) {
                    if (colliding.containsKey(otherCol) && !colliding.get(otherCol)) {
                        if (otherCol.isTrigger()) handler.onTriggerEnter(otherCol);
                        else handler.onCollisionEnter(otherCol);
                    }
                    if (otherCol.isTrigger()) handler.onTrigger(otherCol);
                    else handler.onCollision(otherCol);
                }
                colliding.remove(otherCol);
                this.colliding.put(otherCol, true);
                isColliding = true;
            }else {
                for (CollisionHandler handler : handlers) {
                    if (lastFrame.containsKey(otherCol) && lastFrame.get(otherCol)) {
                        if (otherCol.isTrigger()) handler.onTriggerExit(otherCol);
                        else handler.onCollisionExit(otherCol);
                    }

                }
                colliding.remove(otherCol);
                colliding.put(otherCol, false);
            }
        }
        lastFrame = (HashMap<Collider, Boolean>) colliding.clone();
    }

    protected void update(Object orig) {
        isColliding = false;
        for (GameObject other : GameEngine.getActiveScene().getGameObjects()) {
            if (other == object || other == orig) continue;
            if (!other.hasComponent(Collider.class)) continue;
            Collider otherCol = (Collider) other.getComponent(Collider.class);
            Area collision = getArea(transform, mesh);
            collision.intersect(getArea(otherCol.transform, otherCol.mesh));
            if (!collision.isEmpty()) {
                for (CollisionHandler handler : handlers) {
                    if (colliding.containsKey(otherCol) && !colliding.get(otherCol)) {
                        if (otherCol.isTrigger()) handler.onTriggerEnter(otherCol);
                        else handler.onCollisionEnter(otherCol);
                    }
                    if (otherCol.isTrigger()) handler.onTrigger(otherCol);
                    else handler.onCollision(otherCol);
                }
                colliding.remove(otherCol);
                this.colliding.put(otherCol, true);
                isColliding = true;
            }else {
                for (CollisionHandler handler : handlers) {
                    if (lastFrame.containsKey(otherCol) && lastFrame.get(otherCol)) {
                        if (otherCol.isTrigger()) handler.onTriggerExit(otherCol);
                        else handler.onCollisionExit(otherCol);
                    }

                }
                colliding.remove(otherCol);
                colliding.put(otherCol, false);
            }
        }
        lastFrame = (HashMap<Collider, Boolean>) colliding.clone();
    }

    public void addCollisionHandler(CollisionHandler handler) { handlers.add(handler); }

    public boolean isColliding() { return isColliding; }
    public boolean isTrigger() { return trigger; }
}
