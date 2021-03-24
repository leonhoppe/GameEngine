package de.craftix.engine.objects;

import java.io.Serializable;

public class Component implements Serializable {
    protected GameObject object;

    private Component(GameObject object) {this.object = object;}
    public Component() {}

    public void update() {}

    protected void initialise(GameObject object) { this.object = object; }

    public Component copy(GameObject object) { return new Component(object); }
}
