package de.craftix.engine.objects;

public class Component {
    protected GameObject object;

    private Component(GameObject object) {this.object = object;}
    public Component() {}

    public void start() {}
    public void stop() {}
    public void update() {}
    public void fixedUpdate() {}

    protected void initialise(GameObject object) { this.object = object; }

    public Component copy(GameObject object) { return new Component(object); }
}
