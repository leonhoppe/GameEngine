package de.craftix.test;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.objects.components.AnimationComponent;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.Shape;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.render.SpriteMap;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.components.UIAnimationComponent;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Main extends GameEngine {
    private static final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);
    private static final GameObject test = new GameObject(blocks.getSprite(3), new Vector2(0, 0), new Dimension(2, 2));
    private static final GameObject test2 = new GameObject(blocks.getSprite(2), new Vector2(0, 0), new Dimension(1.5f, 1.5f));

    public static void main(String[] args) {
        Screen.antialiasing(true);
        Screen.showFrames(true);
        Screen.setResizeable(false);
        Screen.setFramesPerSecond(60);
        Screen.setAntialiasingEffectTextures(false);
        InputManager.setFullscreenKey(KeyEvent.VK_F11);
        InputManager.setClosingKey(KeyEvent.VK_ESCAPE);
        setup(800, 600, "GameEngine", new Main(), 120, true);
    }

    @Override
    public void initialise() {
        setIcon(blocks.getSprite(4));
        getActiveScene().setBackground(blocks.getSprite(1), true);
        //instantiate(test);
        //instantiate(test2);
        //test2.getSprite().repeat = true;

        UIElement e1 = new UIElement(new Transform(new Vector2(), new Dimension(50), Quaternion.IDENTITY()), blocks.getSprite(2), UIAlignment.CENTER);
        UIElement t1 = new UIElement(new Transform(new Vector2(0, 200), new Dimension(50), Quaternion.IDENTITY()), new Sprite(Color.BLACK, Shape.RECTANGLE), UIAlignment.CENTER);
        UIElement t2 = new UIElement(new Transform(new Vector2(200, 200), new Dimension(50), Quaternion.IDENTITY()), new Sprite(Color.BLACK, Shape.RECTANGLE), UIAlignment.CENTER);

        e1.addComponent(new UIAnimationComponent(
                new Keyframe<>(new Vector2(0, 200), 0, 500),
                new Keyframe<>(new Vector2(200, 200), 600, 1000),
                new Keyframe<>(Quaternion.euler(360), 800, 800),
                new Keyframe<>(new Dimension(150), 300, 1000),
                new Keyframe<>(new Transform(new Vector2(), new Dimension(50), Quaternion.IDENTITY()), 1700, 200)
        ));

        getUIManager().addElement(t1);
        getUIManager().addElement(t2);
        getUIManager().addElement(e1);
    }

    @Override
    public void fixedUpdate() {
        float speed = 5;

        test.transform.lookAt(InputManager.getMousePos());

        if (InputManager.isKeyPressed(KeyEvent.VK_W))
            test.transform.translate(test.transform.forward().mul(Screen.getFixedDeltaTime() * speed));

        if (InputManager.isKeyPressed(KeyEvent.VK_A))
            test.transform.translate(test.transform.left().mul(Screen.getFixedDeltaTime() * speed));

        if (InputManager.isKeyPressed(KeyEvent.VK_S))
            test.transform.translate(test.transform.backward().mul(Screen.getFixedDeltaTime() * speed));

        if (InputManager.isKeyPressed(KeyEvent.VK_D))
            test.transform.translate(test.transform.right().mul(Screen.getFixedDeltaTime() * speed));

        if (InputManager.isKeyPressed(KeyEvent.VK_SPACE))
            getCamera().z += Screen.getFixedDeltaTime();
        if (InputManager.isKeyPressed(KeyEvent.VK_SHIFT))
            getCamera().z -= Screen.getFixedDeltaTime();

        //test2.transform.rotate(1);
    }
}
