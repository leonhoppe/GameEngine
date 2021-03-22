package de.craftix.engine.objects;

import de.craftix.engine.GameEngine;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.Shape;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Collider extends Component {
    public Shape shape;
    public Mesh mesh;
    public Transform transform;
    private boolean isColliding;

    private final boolean trigger;
    private final List<CollisionHandler> handlers = new ArrayList<>();
    private final HashMap<Collider, Boolean> colliding = new HashMap<>();

    public Collider(Shape shape, boolean isTrigger) { this.shape = shape; trigger = isTrigger; }
    public Collider(Shape shape, boolean isTrigger, Vector2 pos, Dimension size) {
        this(shape, isTrigger);
        transform = new Transform();
        transform.position = pos;
        transform.scale = size;
        transform.rotation = Quaternion.IDENTITY();
    }

    public Collider(Mesh mesh, boolean isTrigger) { this.mesh = mesh; trigger = isTrigger; }
    public Collider(Mesh mesh, boolean isTrigger, Vector2 pos, Dimension size) {
        this(mesh, isTrigger);
        transform = new Transform();
        transform.position = pos;
        transform.scale = size;
        transform.rotation = Quaternion.IDENTITY();
    }

    @Override
    protected void initialise(GameObject object) {
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
            Area collision = getArea();
            collision.intersect(otherCol.getArea());
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

    public Area getArea() {
        if (mesh != null)
            return new Area(Screen.getTransform(transform).createTransformedShape(mesh.getMesh()));

        return new Area(Screen.getTransform(transform).createTransformedShape(new Mesh(shape, transform).getMesh()));
    }

    public void addCollisionHandler(CollisionHandler handler) { handlers.add(handler); }

    public boolean isColliding() { return isColliding; }
    public boolean isTrigger() { return trigger; }
}
