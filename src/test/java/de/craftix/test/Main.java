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
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Main extends GameEngine {
    private static final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);

    public static void main(String[] args) {
        Screen.antialiasing(true);
        Screen.showFrames(true);
        Screen.setResizeable(false);
        Screen.setFramesPerSecond(120);
        Screen.setAntialiasingEffectTextures(false);
        InputManager.setClosingKey(KeyEvent.VK_ESCAPE);
        InputManager.setFullscreenKey(KeyEvent.VK_F11);
        setup(800, 600, "GameEngine 3.0", new Main(), 60);
    }

    @Override
    public void initialise() {
        getScene().setBackgroundColor(Color.CYAN);

        GameObject ground = new GameObject("ground", new Mesh(Shape.RECTANGLE, Color.GREEN), new Transform(new Vector2(0, -5), new Dimension(20, 2), Quaternion.IDENTITY()));
        GameObject player = new GameObject("player", new Mesh(Shape.CIRCLE, Color.RED), new Transform(new Vector2(0, 0), new Dimension(1), Quaternion.IDENTITY()));

        player.addComponent(new PhysicsComponent());
        player.addComponent(new Collider(player.getMesh(), false));
        ground.addComponent(new Collider(ground.getMesh(), false));

        ground.setLayer("Background");
        player.setLayer("Foreground");

        instantiate(ground);
        instantiate(player);

        UIElement sun = new UIElement(new Mesh(Shape.CIRCLE, Color.YELLOW), new Transform(new Vector2(50, -50), new Dimension(75, 75), Quaternion.IDENTITY()), UIAlignment.TOP_LEFT);
        instantiateUI(sun);
    }

    @Override
    public void update() {
        GameObject player = getObjectByName("player");
        PhysicsComponent playerPhysics = player.getComponent(PhysicsComponent.class);

        if (InputManager.isKeyPressed(KeyEvent.VK_SPACE) && playerPhysics.onGround())
            playerPhysics.setVelocity(new Vector2(0, 10f));

        if (InputManager.isKeyPressed(KeyEvent.VK_D))
            playerPhysics.addVelocity(Vector2.right().mul(Screen.getDeltaTime() * 7f));
        if (InputManager.isKeyPressed(KeyEvent.VK_A))
            playerPhysics.addVelocity(Vector2.left().mul(Screen.getDeltaTime() * 7f));
    }
}
