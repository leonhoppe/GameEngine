package de.craftix.test;

import de.craftix.engine.EngineSettings;
import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.objects.components.Collider;
import de.craftix.engine.objects.components.PhysicsComponent;
import de.craftix.engine.render.*;
import de.craftix.engine.render.MShape;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.elements.UIButton;
import de.craftix.engine.ui.elements.UICheckBox;
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
        EngineSettings.setResizable(true);
        EngineSettings.setFullscreenKey(KeyEvent.VK_F11);
        EngineSettings.setCloseKey(KeyEvent.VK_ESCAPE);
        EngineSettings.printSystemLog(false);
        setup(1280, 720, "GameEngine 3.0", new Main(), 60);
    }

    @Override
    public void initialise() {
        getScene().setBackgroundColor(Color.CYAN);

        GameObject ground = new GameObject("ground", new Mesh(MShape.RECTANGLE, Color.GREEN), new Transform(new Vector2(0, -6.5f), new Dimension(26, 2)));
        GameObject player = new GameObject("player", blocks.getSprite(2), new Transform(new Vector2(0, 0), new Dimension(1)));

        player.addComponent(new PhysicsComponent());
        player.addComponent(new Collider(player.getMesh(), false));
        ground.addComponent(new Collider(ground.getMesh(), false));

        ground.setLayer("Background");
        player.setLayer("Foreground");

        instantiate(ground);
        instantiate(player);

        UIElement sun = new UIElement(new Mesh(MShape.CIRCLE, Color.YELLOW), new Transform(new Vector2(50, -50), new Dimension(75)), UIAlignment.TOP_LEFT);
        instantiateUI(sun);

        UICheckBox checkBox = new UICheckBox(true, "CheckBox", new Transform(new Vector2(), new Dimension(40)), UIAlignment.CENTER);
        checkBox.setFont(new Font("Arial", Font.PLAIN, 40));
        checkBox.setColor(Color.RED);
        instantiateUI(checkBox);

        //setScene(new LoginScene());
    }

    @Override
    public void update() {
        float speed = 10.0f;
        PhysicsComponent physics = getObjectByName("player").getComponent(PhysicsComponent.class);
        physics.setGravity(false);

        float vertical = InputManager.getAxis("Vertical") * speed * Screen.getDeltaTime();
        float horizontal = InputManager.getAxis("Horizontal") * speed * Screen.getDeltaTime();

        physics.addVelocity(new Vector2(horizontal, vertical));
    }
}
