package de.craftix.engine.var;

public class Mathf {

    public static Vector2 normalizeVector(Vector2 point, float min, float max) {
        Vector2 out = new Vector2();
        out.x = normalise(point.x, min, max);
        out.y = normalise(point.y, min, max);
        return out;
    }

    public static Vector2 mapVector(Vector2 point, float min, float max, float mMin, float mMax) {
        Vector2 out = new Vector2();
        out.x = map(point.x, min, max, mMin, mMax);
        out.y = map(point.y, min, max, mMin, mMax);
        return out;
    }

    public static float normalise(float value, float min, float max) {
        return (value - min) / (max - min);
    }

    public static float map(float value, float min, float max, float mMin, float mMax) {
        float norm = normalise(value, min, max);
        return (mMax - mMin) * norm + mMin;
    }

}
