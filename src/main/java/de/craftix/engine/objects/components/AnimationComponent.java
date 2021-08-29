package de.craftix.engine.objects.components;

import de.craftix.engine.objects.GameObject;
import de.craftix.engine.var.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AnimationComponent extends Component {
    private final HashMap<String, Animation> animations = new HashMap<>();
    private boolean initialised = false;

    public AnimationComponent(Animation... animations) {
        for (Animation a : animations) {
            this.animations.put(a.getName(), a);
        }
    }

    @Override
    public void initialise(GameObject object) {
        super.initialise(object);
        for (Animation a : animations.values()) {
            a.initialise(object);
        }
        initialised = true;
    }

    @Override
    public void update() {
        for (Animation a : animations.values()) {
            a.update();
        }
    }

    public Animation getAnimation(String name) { return animations.get(name); }
    public void addAnimation(Animation animation) {
        if (initialised)
            animation.initialise(object);
        animations.put(animation.getName(), animation);
    }
    public Animation[] getAnimations() { return animations.values().toArray(new Animation[0]); }
}
