package de.craftix.test;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.objects.components.AnimationComponent;
import de.craftix.engine.render.*;
import de.craftix.engine.render.Shape;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.components.UIAnimationComponent;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Main extends GameEngine {
    private static final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);
    private static final GameObject grass = new GameObject(blocks.getSprite(3), new Vector2(), new Dimension(2));

    public static void main(String[] args) {
        Screen.antialiasing(true);
        Screen.showFrames(true);
        Screen.setResizeable(false);
        Screen.setFramesPerSecond(60);
        Screen.setAntialiasingEffectTextures(false);
        InputManager.setFullscreenKey(KeyEvent.VK_F11);
        InputManager.setClosingKey(KeyEvent.VK_ESCAPE);
        setup(800, 600, "GameEngine 3.0", new Main(), 120);
    }

    @Override
    public void initialise() {
        setIcon(blocks.getSprite(4));
        getActiveScene().setBackground(blocks.getSprite(1).resize(70, 70, Resizer.AVERAGE), false);
        instantiate(grass);
    }

    @Override
    public void fixedUpdate() {
        float speed = 5 * grass.transform.position.dist(InputManager.getMousePos());
        grass.transform.lookAt(InputManager.getMousePos());
        grass.transform.translate(grass.transform.forward().mul(Screen.getFixedDeltaTime() * speed));
    }
}
