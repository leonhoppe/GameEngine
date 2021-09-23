package de.craftix.test;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.objects.components.Collider;
import de.craftix.engine.objects.components.PhysicsComponent;
import de.craftix.engine.render.*;
import de.craftix.engine.render.Shape;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.configuration.file.YamlConfiguration;
import de.craftix.engine.var.configuration.utils.Configurable;
import de.craftix.engine.var.configuration.utils.ConfigurationManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

public class Main extends GameEngine {
    private static final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);

    Configurable<String> server = new Configurable<>("mysql.server", "localhost");
    Configurable<Integer> port = new Configurable<>("mysql.port", 3306);
    Configurable<String> database = new Configurable<>("mysql.database", "database");
    Configurable<String> username = new Configurable<>("mysql.username", "user");
    Configurable<String> password = new Configurable<>("mysql.password", "pass");
    Configurable<Transform> trans = new Configurable<>("player", new Transform());
    Configurable<Float> speed = new Configurable<>("speed", 10f);
    ConfigurationManager mysqlConfig = new ConfigurationManager(new File("config.yml"), server, port, database, username, password, trans, speed);

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
