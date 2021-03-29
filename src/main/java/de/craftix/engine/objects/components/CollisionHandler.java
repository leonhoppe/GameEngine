package de.craftix.engine.objects.components;

import java.io.Serializable;

public interface CollisionHandler extends Serializable {

    void onCollisionEnter(Collider other);
    void onCollision(Collider other);
    void onCollisionExit(Collider other);

    void onTriggerEnter(Collider other);
    void onTrigger(Collider other);
    void onTriggerExit(Collider other);

}
