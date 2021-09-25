package de.craftix.test;

import de.craftix.engine.EngineSettings;
import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.objects.components.Collider;
import de.craftix.engine.objects.components.PhysicsComponent;
import de.craftix.engine.render.*;
import de.craftix.engine.render.Shape;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.elements.UITextBox;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Main extends GameEngine {
    private static final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);

    public static void main(String[] args) {
        EngineSettings.setAntialiasing(true);
        EngineSettings.setAntialiasingForTextures(false);
        EngineSettings.showFrames(true);
        EngineSettings.setResizable(false);
        EngineSettings.setFPS(120);
        EngineSettings.setFullscreenKey(KeyEvent.VK_F11);
        EngineSettings.setCloseKey(KeyEvent.VK_ESCAPE);
        EngineSettings.printSystemLog(false);
        EngineSettings.setFullscreen(true);
        setup(800, 600, "GameEngine 3.0", new Main(), 60);
    }

    @Override
    public void initialise() {
        getScene().setBackgroundColor(Color.CYAN);

        Mesh block = new Mesh(new Vector2[]{
                new Vector2(-0.5f, -0.5f),
                new Vector2( 0.5f, -0.5f),
                new Vector2(-0.5f,  0.5f),
                new Vector2(-0.5f,  0.5f),
                new Vector2( 0.5f, -0.5f),
                new Vector2( 0.5f,  0.5f)
        }, new Vector2[]{
                new Vector2(0, 0),
                new Vector2(1, 0),
                new Vector2(0, 1),
                new Vector2(0, 1),
                new Vector2(1, 0),
                new Vector2(1, 1)
        }, blocks.getSprite(2));
        Mesh rect = new Mesh(Shape.RECTANGLE, Color.GREEN);

        GameObject ground = new GameObject("ground", rect, new Transform(new Vector2(0, -5), new Dimension(20, 2), Quaternion.IDENTITY()));
        GameObject player = new GameObject("player", block, new Transform(new Vector2(0, 0), new Dimension(1), Quaternion.IDENTITY()));

        player.addComponent(new PhysicsComponent());
        player.addComponent(new Collider(player.getMesh(), false));
        ground.addComponent(new Collider(ground.getMesh(), false));

        ground.setLayer("Background");
        player.setLayer("Foreground");

        instantiate(ground);
        instantiate(player);

        UIElement sun = new UIElement(new Mesh(Shape.CIRCLE, Color.YELLOW), new Transform(new Vector2(50, -50), new Dimension(75, 75), Quaternion.IDENTITY()), UIAlignment.TOP_LEFT);
        instantiateUI(sun);

        //setScene(new LoginScene());
    }

    @Override
    public void update() {
        float speed = 5.0f;
        PhysicsComponent physics = getObjectByName("player").getComponent(PhysicsComponent.class);
        physics.setGravity(false);

        float vertical = InputManager.getAxis("Vertical") * speed * Screen.getDeltaTime();
        float horizontal = InputManager.getAxis("Horizontal") * speed * Screen.getDeltaTime();

        physics.addVelocity(new Vector2(horizontal, vertical));

        getCamera().transform.position = physics.gameObject().transform.position;
    }
}
