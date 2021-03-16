package de.craftix.engine.objects;

import de.craftix.engine.GameEngine;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.Shape;
import de.craftix.engine.var.Quaternion;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Collider extends Component {

    public final Shape shape;
    public Transform transform;

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

    @Override
    protected void initialise(GameObject object) {
        super.initialise(object);
        if (transform == null)
            transform = object.transform;
    }

    private HashMap<Collider, Boolean> lastFrame = new HashMap<>();
    @Override
    public void update() {
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
        java.awt.Shape dimensions = null;
        AffineTransform trans = new AffineTransform();
        Point pos = Screen.calculateScreenPosition(transform);
        trans.translate(pos.x + (transform.scale.width / 2f), pos.y + (transform.scale.height / 2f));
        trans.rotate(transform.rotation.getAngle(), transform.position.x, -transform.position.y);
        switch (shape) {
            case CIRCLE:
                dimensions = new Ellipse2D.Float(-transform.scale.width / 2f, -transform.scale.height / 2f, transform.scale.width, transform.scale.height);
                break;
            case RECTANGLE:
                dimensions = new Rectangle(-transform.scale.width / 2, -transform.scale.height / 2, transform.scale.width, transform.scale.height);
                break;
            case TRIANGLE:
                Point top = new Point(0, -transform.scale.height / 2);
                Point right = new Point(-transform.scale.width / 2, transform.scale.height / 2);
                Point left = new Point(transform.scale.width / 2, transform.scale.height / 2);
                dimensions = new Polygon(new int[]{ top.x, right.x, left.x },
                        new int[]{ top.y, right.y, left.y },
                        3);
                break;
        }
        return new Area(trans.createTransformedShape(dimensions));
    }

    public void addCollisionHandler(CollisionHandler handler) { handlers.add(handler); }

    public boolean isColliding() { return colliding.size() != 0; }
    public boolean isTrigger() { return trigger; }
}
