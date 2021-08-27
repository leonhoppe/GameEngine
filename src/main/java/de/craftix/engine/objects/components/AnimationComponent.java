package de.craftix.engine.objects.components;

import de.craftix.engine.objects.GameObject;
import de.craftix.engine.var.*;

import java.util.ArrayList;

public class AnimationComponent extends Component {
    private final Keyframe<Vector2>[] posKeyframes;
    private final Keyframe<Dimension>[] scaleKeyframes;
    private final Keyframe<Quaternion>[] rotKeyframes;

    @SafeVarargs
    public AnimationComponent(Keyframe<? extends Transformation>... keyframes) {
        ArrayList<Keyframe<Vector2>> vectors = new ArrayList<>();
        ArrayList<Keyframe<Dimension>> dimensions = new ArrayList<>();
        ArrayList<Keyframe<Quaternion>> quaternions = new ArrayList<>();
        for (Keyframe<? extends Transformation> keyframe : keyframes) {
            if (keyframe.value instanceof Vector2)
                vectors.add(new Keyframe<>((Vector2) keyframe.value, keyframe.startPoint, keyframe.timeInTPS));
            if (keyframe.value instanceof Dimension)
                dimensions.add(new Keyframe<>((Dimension) keyframe.value, keyframe.startPoint, keyframe.timeInTPS));
            if (keyframe.value instanceof Quaternion)
                quaternions.add(new Keyframe<>((Quaternion) keyframe.value, keyframe.startPoint, keyframe.timeInTPS));
            if (keyframe.value instanceof Transform) {
                vectors.add(new Keyframe<>(((Transform) keyframe.value).position, keyframe.startPoint, keyframe.timeInTPS));
                dimensions.add(new Keyframe<>(((Transform) keyframe.value).scale, keyframe.startPoint, keyframe.timeInTPS));
                quaternions.add(new Keyframe<>(((Transform) keyframe.value).rotation, keyframe.startPoint, keyframe.timeInTPS));
            }
        }
        posKeyframes = vectors.toArray(new Keyframe[0]);
        scaleKeyframes = dimensions.toArray(new Keyframe[0]);
        rotKeyframes = quaternions.toArray(new Keyframe[0]);
    }

    @Override
    public void initialise(GameObject object) {
        super.initialise(object);
        rot_Original = object.transform.rotation.copy();
        pos_Original = object.transform.position.copy();
        scale_Original = object.transform.scale.copy();

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
        if (rot_passedTPS == frame.startPoint + frame.timeInTPS) {
            object.transform.rotation = frame.value.copy();
            rot_currentKeyframe++;
            rot_Original = object.transform.rotation.copy();
            if (rot_currentKeyframe >= rotKeyframes.length)
                rot_running = false;
            return;
        }
        rot_passedTPS++;
        if (rot_passedTPS < frame.startPoint) return;

        double difference = frame.value.getAngle() - rot_Original.getAngle();
        difference /= frame.timeInTPS;
        object.transform.rotate(Math.toDegrees(difference));
    }

    private boolean pos_running = true;
    private int pos_currentKeyframe = 0;
    private int pos_passedTPS = 0;
    private Vector2 pos_Original;
    private void applyPosition() {
        if (!pos_running) return;
        Keyframe<Vector2> frame = posKeyframes[pos_currentKeyframe];
        if (pos_passedTPS == frame.startPoint + frame.timeInTPS) {
            object.transform.position = frame.value.copy();
            pos_currentKeyframe++;
            pos_Original = object.transform.position.copy();
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
        motion.div(frame.timeInTPS);
        object.transform.position.add(motion);
    }

    private boolean scale_running = true;
    private int scale_currentKeyframe = 0;
    private int scale_passedTPS = 0;
    private Dimension scale_Original;
    private void applyScale() {
        if (!scale_running) return;
        Keyframe<Dimension> frame = scaleKeyframes[scale_currentKeyframe];
        if (scale_passedTPS == frame.startPoint + frame.timeInTPS) {
            object.transform.scale = frame.value.copy();
            scale_currentKeyframe++;
            scale_Original = object.transform.scale.copy();
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
        scale.width /= frame.timeInTPS;
        scale.height /= frame.timeInTPS;
        object.transform.scale.width += scale.width;
        object.transform.scale.height += scale.height;
    }
}
