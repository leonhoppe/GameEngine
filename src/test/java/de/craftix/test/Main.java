package de.craftix.test;

import de.craftix.engine.GameEngine;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.render.SpriteMap;
import de.craftix.engine.var.Vector2;

import java.awt.*;

public class Main extends GameEngine {
    private final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);
    private final GameObject test = new GameObject(blocks.getSprite(3), new Vector2(0, 0), new Dimension(50, 50));

    public static void main(String[] args) {
        Screen.antialiasing = true;
        Screen.showFrames = true;
        setup(800, 600, "GameEngine", new Main(), 120);
    }

    @Override
    public void initialise() {
        getActiveScene().setBackground(blocks.getSprite(1));

        test.transform.rotation.rotate(45);
        instantiate(test);
        instantiate(new GameObject(blocks.getSprite(2), new Vector2(0, 100), new Dimension(50, 50)));
    }

    @Override
    public void fixedUpdate() {
        test.transform.position.x--;
    }
}
