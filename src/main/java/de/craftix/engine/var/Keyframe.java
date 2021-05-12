package de.craftix.engine.var;

public class Keyframe<E> {
    public final E value;
    public final int timeInTPS;
    public final int startPoint;

    public Keyframe(E value, int startPoint, int timeInTPS) {
        this.value = value;
        this.timeInTPS = timeInTPS;
        this.startPoint = startPoint;
    }
}
