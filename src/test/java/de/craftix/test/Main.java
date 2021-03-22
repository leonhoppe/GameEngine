package de.craftix.test;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.render.SpriteMap;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Vector2;

import java.awt.event.KeyEvent;

public class Main extends GameEngine {
    private final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);
    private final GameObject test = new GameObject(blocks.getSprite(3), new Vector2(0, 0), new Dimension(2, 2));
    private final GameObject test2 = new GameObject(blocks.getSprite(2), new Vector2(0, 2.5f), new Dimension(1.5f, 1.5f));

    public static void main(String[] args) {
        Screen.antialiasing(true);
        Screen.showFrames(true);
        Screen.setResizeable(false);
        Screen.limitFPS(true);
        Screen.setAntialiasingEffectTextures(false);
        InputManager.setFullscreenKey(KeyEvent.VK_F11);
        setup(800, 600, "GameEngine", new Main(), 120);
    }

    @Override
    public void initialise() {
        setIcon(blocks.getSprite(4).texture);
        getActiveScene().setBackground(blocks.getSprite(1));

        instantiate(test);
        instantiate(test2);
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
    }
}
