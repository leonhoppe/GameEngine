package de.craftix.engine.objects.components;

import de.craftix.engine.objects.GameObject;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Keyframe;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

public class AnimationComponent extends Component {
    private final Keyframe<Transform>[] keyframes;
    private int currentKeyframe;
    private boolean running;

    @SafeVarargs
    public AnimationComponent(Keyframe<Transform>... keyframes) {
        this.keyframes = keyframes;
        this.currentKeyframe = 0;
        this.running = true;
    }

    @Override
    public void initialise(GameObject object) {
        super.initialise(object);
        this.original = object.transform.copy();
    }

    private int passedTPS = 0;
    private Transform original;
    public void fixedUpdate() {
        if (!running) return;
        Keyframe<Transform> frame = keyframes[currentKeyframe];
        if (passedTPS >= frame.timeInTPS) {
            object.transform = frame.value.copy();
            currentKeyframe++;
            passedTPS = 0;
            original = object.transform.copy();
            if (currentKeyframe >= keyframes.length)
                running = false;
            return;
        }

        //Rotation
        double difference = Math.toDegrees(frame.value.rotation.getAngle() - original.rotation.getAngle());
        difference /= frame.timeInTPS;
        object.transform.rotate(difference);

        //Position
        Vector2 motion = new Vector2(
                frame.value.position.x - original.position.x,
                frame.value.position.y - original.position.y
        );
        float rotX = (float) Math.sin(object.transform.rotation.getAngle());
        float rotY = (float) Math.cos(object.transform.rotation.getAngle());
        if (rotX != 0)
            motion.x *= rotX;
        if (rotY != 0)
            motion.y *= rotY;
        motion.divSelf(frame.timeInTPS);
        object.transform.position.addSelf(motion);
        passedTPS++;

        //Scale
        Dimension scale = new Dimension(
                frame.value.scale.width - original.scale.width,
                frame.value.scale.height - original.scale.height
        );
        scale.width /= frame.timeInTPS;
        scale.height /= frame.timeInTPS;
        object.transform.scale.width += scale.width;
        object.transform.scale.height += scale.height;
    }
}
