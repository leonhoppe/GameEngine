package de.craftix.test;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.objects.components.AnimationComponent;
import de.craftix.engine.objects.components.Collider;
import de.craftix.engine.objects.components.PhysicsComponent;
import de.craftix.engine.render.*;
import de.craftix.engine.render.Shape;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.components.UIAnimationComponent;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main extends GameEngine {
    private static final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);

    public static void main(String[] args) {
        Screen.antialiasing(true);
        Screen.showFrames(true);
        Screen.setResizeable(true);
        Screen.setFramesPerSecond(120);
        Screen.setAntialiasingEffectTextures(false);
        InputManager.setFullscreenKey(KeyEvent.VK_F11);
        InputManager.setClosingKey(KeyEvent.VK_ESCAPE);
        setup(800, 600, "GameEngine 3.0", new Main(), 1);
    }

    private final PhysicsComponent playerPhysics = new PhysicsComponent();

    @Override
    public void initialise() {
        getActiveScene().setBackground(blocks.getSprite(2).resize(50, 50, Resizer.AVERAGE), false);

        GameObject ground = new GameObject(blocks.getSprite(1), new Transform(new Vector2(0, -5), new Dimension(20, 2), Quaternion.IDENTITY()));
        GameObject player = new GameObject(new Mesh(Color.RED, Shape.CIRCLE), new Transform(new Vector2(0, 5), new Dimension(1), Quaternion.IDENTITY()));

        player.addComponent(playerPhysics);
        player.addComponent(new Collider(player.getMesh(), false));
        ground.addComponent(new Collider(ground.getMesh(), false));

        instantiate(ground);
        instantiate(player);
    }

    @Override
    public void update() {
        if (InputManager.isKeyPressed(KeyEvent.VK_SPACE) && playerPhysics.onGround())
            playerPhysics.setVelocity(new Vector2(0, 7));

        if (InputManager.isKeyPressed(KeyEvent.VK_D))
            playerPhysics.gameObject().transform.translate(Vector2.right().mul(Screen.getDeltaTime() * 5f));
        if (InputManager.isKeyPressed(KeyEvent.VK_A))
            playerPhysics.gameObject().transform.translate(Vector2.left().mul(Screen.getDeltaTime() * 5f));
    }
}
