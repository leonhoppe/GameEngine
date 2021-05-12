package de.craftix.engine.var;

public class Keyframe<E> {
    public final E value;
    public final int timeInTPS;

    public Keyframe(E value, int timeInTPS) {
        this.value = value;
        this.timeInTPS = timeInTPS;
    }
}
