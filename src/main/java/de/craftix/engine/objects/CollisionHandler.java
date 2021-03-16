package de.craftix.engine.objects;

public interface CollisionHandler {

    void onCollisionEnter(Collider other);
    void onCollision(Collider other);
    void onCollisionExit(Collider other);

    void onTriggerEnter(Collider other);
    void onTrigger(Collider other);
    void onTriggerExit(Collider other);

}
