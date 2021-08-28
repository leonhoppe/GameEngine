package de.craftix.engine.ui.components;

import de.craftix.engine.ui.UIElement;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import java.awt.*;
import java.util.ArrayList;

public class UIAnimationComponent extends UIComponent {
    @Override
    public void render(Graphics2D g) {

    }

    private final Keyframe<Vector2>[] posKeyframes;
    private final Keyframe<Dimension>[] scaleKeyframes;
    private final Keyframe<Quaternion>[] rotKeyframes;

    @SafeVarargs
    public UIAnimationComponent(Keyframe<? extends Transformation>... keyframes) {
        ArrayList<Keyframe<Vector2>> vectors = new ArrayList<>();
        ArrayList<Keyframe<Dimension>> dimensions = new ArrayList<>();
        ArrayList<Keyframe<Quaternion>> quaternions = new ArrayList<>();
        for (Keyframe<? extends Transformation> keyframe : keyframes) {
            if (keyframe.value instanceof Vector2)
                vectors.add(new Keyframe<>((Vector2) keyframe.value, keyframe.startPoint, keyframe.timeInMillis));
            if (keyframe.value instanceof Dimension)
                dimensions.add(new Keyframe<>((Dimension) keyframe.value, keyframe.startPoint, keyframe.timeInMillis));
            if (keyframe.value instanceof Quaternion)
                quaternions.add(new Keyframe<>((Quaternion) keyframe.value, keyframe.startPoint, keyframe.timeInMillis));
            if (keyframe.value instanceof Transform) {
                vectors.add(new Keyframe<>(((Transform) keyframe.value).position, keyframe.startPoint, keyframe.timeInMillis));
                dimensions.add(new Keyframe<>(((Transform) keyframe.value).scale, keyframe.startPoint, keyframe.timeInMillis));
                quaternions.add(new Keyframe<>(((Transform) keyframe.value).rotation, keyframe.startPoint, keyframe.timeInMillis));
            }
        }
        posKeyframes = vectors.toArray(new Keyframe[0]);
        scaleKeyframes = dimensions.toArray(new Keyframe[0]);
        rotKeyframes = quaternions.toArray(new Keyframe[0]);
    }

    @Override
    public void initialise(UIElement element) {
        super.initialise(element);
        rot_Original = element.transform.rotation.copy();
        pos_Original = element.transform.position.copy();
        scale_Original = element.transform.scale.copy();

        if (rotKeyframes.length == 0)
            rot_running = false;
        if (posKeyframes.length == 0)
            pos_running = false;
        if (scaleKeyframes.length == 0)
            scale_running = false;
    }

    @Override
    public void fixedUpdate() {
        applyRotation();
        applyPosition();
        applyScale();
    }

    private boolean rot_running = true;
    private int rot_currentKeyframe = 0;
    private int rot_passedTPS = 0;
    private Quaternion rot_Original;
    private void applyRotation() {
        if (!rot_running) return;
        Keyframe<Quaternion> frame = rotKeyframes[rot_currentKeyframe];
        if (rot_passedTPS == frame.startPoint + frame.timeInMillis) {
            element.transform.rotation = frame.value.copy();
            rot_currentKeyframe++;
            rot_Original = element.transform.rotation.copy();
            if (rot_currentKeyframe >= rotKeyframes.length)
                rot_running = false;
            return;
        }
        rot_passedTPS++;
        if (rot_passedTPS < frame.startPoint) return;

        double difference = frame.value.getAngle() - rot_Original.getAngle();
        difference /= frame.timeInMillis;
        element.transform.rotate(Math.toDegrees(difference));
    }

    private boolean pos_running = true;
    private int pos_currentKeyframe = 0;
    private int pos_passedTPS = 0;
    private Vector2 pos_Original;
    private void applyPosition() {
        if (!pos_running) return;
        Keyframe<Vector2> frame = posKeyframes[pos_currentKeyframe];
        if (pos_passedTPS == frame.startPoint + frame.timeInMillis) {
            element.transform.position = frame.value.copy();
            pos_currentKeyframe++;
            pos_Original = element.transform.position.copy();
            if (pos_currentKeyframe >= posKeyframes.length)
                pos_running = false;
            return;
        }
        pos_passedTPS++;
        if (pos_passedTPS < frame.startPoint) return;

        Vector2 motion = new Vector2(
                frame.value.x - pos_Original.x,
                frame.value.y - pos_Original.y
        );
        motion.div(frame.timeInMillis);
        element.transform.position.add(motion);
    }

    private boolean scale_running = true;
    private int scale_currentKeyframe = 0;
    private int scale_passedTPS = 0;
    private Dimension scale_Original;
    private void applyScale() {
        if (!scale_running) return;
        Keyframe<Dimension> frame = scaleKeyframes[scale_currentKeyframe];
        if (scale_passedTPS == frame.startPoint + frame.timeInMillis) {
            element.transform.scale = frame.value.copy();
            scale_currentKeyframe++;
            scale_Original = element.transform.scale.copy();
            if (scale_currentKeyframe >= scaleKeyframes.length)
                scale_running = false;
            return;
        }
        scale_passedTPS++;
        if (scale_passedTPS < frame.startPoint) return;

        Dimension scale = new Dimension(
                frame.value.width - scale_Original.width,
                frame.value.height - scale_Original.height
        );
        scale.width /= frame.timeInMillis;
        scale.height /= frame.timeInMillis;
        element.transform.scale.width += scale.width;
        element.transform.scale.height += scale.height;
    }
}
