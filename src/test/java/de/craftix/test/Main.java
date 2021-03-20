package de.craftix.test;

import de.craftix.engine.GameEngine;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.render.SpriteMap;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Vector2;

public class Main extends GameEngine {
    private final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);
    private final GameObject test = new GameObject(blocks.getSprite(3), new Vector2(0, 0), new Dimension(1, 1));
    private final GameObject test2 = new GameObject(blocks.getSprite(2), new Vector2(0, 1.5f), new Dimension(0.5f, 0.5f));

    public static void main(String[] args) {
        Screen.antialiasing(true);
        Screen.showFrames(true);
        Screen.showGrid(false);
        Screen.setResizeable(false);
        Screen.limitFPS(true);
        Screen.setAntialiasingEffectTextures(false);
        setup(800, 600, "GameEngine", new Main(), 60);
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
        test.transform.rotation.rotate(1);
    }
}
