package de.craftix.engine.ui.components;

import de.craftix.engine.objects.GameObject;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class UIAnimationComponent extends UIComponent {
    @Override
    public void render(Graphics2D g) {

    }

    private final HashMap<String, Animation> animations = new HashMap<>();
    private boolean initialised = false;

    public UIAnimationComponent(Animation... animations) {
        for (Animation a : animations) {
            this.animations.put(a.getName(), a);
        }
    }

    @Override
    public void initialise(UIElement object) {
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
            animation.initialise(element);
        animations.put(animation.getName(), animation);
    }
    public Animation[] getAnimations() { return animations.values().toArray(new Animation[0]); }
}
