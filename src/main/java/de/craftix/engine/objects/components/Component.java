package de.craftix.engine.objects.components;

import de.craftix.engine.objects.GameObject;

import java.io.Serializable;

public class Component implements Serializable {
    protected GameObject object;

    private Component(GameObject object) {this.object = object;}
    public Component() {}

    public void update() {}
    public void fixedUpdate() {}

    public void initialise(GameObject object) { this.object = object; }

    public Component copy(GameObject object) { return new Component(object); }
}
