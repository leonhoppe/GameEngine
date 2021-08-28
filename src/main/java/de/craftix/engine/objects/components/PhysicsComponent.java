package de.craftix.engine.objects.components;

import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Screen;
import de.craftix.engine.var.Vector2;
import org.apache.commons.lang.SerializationUtils;

public class PhysicsComponent extends Component {
    public static final float EARTH_MASS = 9.81f;

    private static final double G = 0.0000000000674;
    private static final double MULTIPLICATOR = 1.5 * 10E9;

    //Gravity
    private boolean gravity = true;
    private float mass = 1.0f;
    private Vector2 initialVelocity = new Vector2();
    private Vector2 currentVelocity;

    public PhysicsComponent() {}
    public PhysicsComponent(float mass) { this.mass = mass; }

    @Override
    public void initialise(GameObject object) {
        super.initialise(object);
        currentVelocity = initialVelocity;
    }

    @Override
    public void update() {
        calculateGravity();
        calculateDrag();

        // TODO: Fix the movement issue
        if (checkMovement(currentVelocity)) {
            object.transform.translate(currentVelocity.copy().mul(Screen.getDeltaTime()));
        }
    }

    private void calculateGravity() {
        if (!hasGravity() || onGround()) return;
        Vector2 dir = Vector2.down();
        Vector2 force = new Vector2(dir.mul((float) (G * (mass * EARTH_MASS))));
        Vector2 acceleration = force.mul(mass).mul((float) MULTIPLICATOR).mul(Screen.getDeltaTime());
        currentVelocity.add(acceleration);
    }
    private void calculateDrag() {
        // TODO: calculate the Drag Force of the Object
    }
    private boolean checkMovement(Vector2 velocity) {
        boolean valid = true;
        if (object.hasComponent(Collider.class)) {
            GameObject temp = (GameObject) SerializationUtils.clone(object);
            temp.transform.translate(velocity.copy().mul(Screen.getDeltaTime()));
            Collider col = (Collider) temp.getComponent(Collider.class);
            col.update(object);
            if (col.isColliding()) valid = false;
        }
        return valid;
    }

    public boolean onGround() {
        return !checkMovement(new Vector2(0, currentVelocity.y - 0.1f));
    }

    public float getMass() { return mass; }
    public boolean hasGravity() { return gravity; }
    public Vector2 getVelocity() { return currentVelocity; }

    public void setMass(float mass) { this.mass = mass; }
    public void setGravity(boolean gravity) { this.gravity = gravity; }
    public void setInitialVelocity(Vector2 initialVelocity) { this.initialVelocity = initialVelocity; }
    public void setVelocity(Vector2 velocity) { this.currentVelocity = velocity; }
    public void addVelocity(Vector2 velocity) { this.currentVelocity.add(velocity); }
}
