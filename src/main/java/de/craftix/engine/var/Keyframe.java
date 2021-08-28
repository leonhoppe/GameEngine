package de.craftix.engine.var;

public class Keyframe<E> {
    public final E value;
    public final int timeInMillis;
    public final int startPoint;

    public Keyframe(E value, int startPoint, int timeInMillis) {
        this.value = value;
        this.timeInMillis = timeInMillis;
        this.startPoint = startPoint;
    }
}
