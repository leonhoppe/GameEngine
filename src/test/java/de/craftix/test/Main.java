package de.craftix.test;

import de.craftix.engine.GameEngine;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.render.SpriteMap;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Input;
import de.craftix.engine.var.Vector2;

import java.awt.event.KeyEvent;

public class Main extends GameEngine {
    private final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);
    private final GameObject test = new GameObject(blocks.getSprite(3), new Vector2(0, 0), new Dimension(1, 1));

    public static void main(String[] args) {
        Screen.antialiasing(true);
        Screen.showFrames(true);
        Screen.showGrid(false);
        Screen.setResizeable(true);
        Screen.limitFPS(true);
        setup(800, 600, "GameEngine", new Main(), 120);
        addInputs(new Inputs());
    }

    @Override
    public void initialise() {
        getActiveScene().setBackground(blocks.getSprite(1));

        instantiate(test);
        instantiate(new GameObject(blocks.getSprite(2), new Vector2(0, 1.5f), new Dimension(1, 1)));
    }

    @Override
    public void fixedUpdate() {
        test.transform.rotation.rotate(0.2f);
    }

    private static class Inputs extends Input {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                Screen.setFullscreen(!Screen.isFullscreen());
            }
        }
    }
}
