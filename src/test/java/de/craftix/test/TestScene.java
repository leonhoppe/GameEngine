package de.craftix.test;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.objects.components.Collider;
import de.craftix.engine.objects.components.PhysicsComponent;
import de.craftix.engine.render.*;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.elements.UICheckBox;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Scene;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

import java.awt.*;

public class TestScene extends Scene {
    private static final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);

    @Override
    public void onStart() {
        setBackgroundColor(Color.CYAN);

        GameObject ground = new GameObject("ground", new Mesh(MShape.RECTANGLE, Color.GREEN), new Transform(new Vector2(0, -6.5f), new de.craftix.engine.var.Dimension(26, 2)));
        GameObject player = new GameObject("player", blocks.getSprite(2), new Transform(new Vector2(0, 0), new de.craftix.engine.var.Dimension(1)));

        player.addComponent(new PhysicsComponent());
        player.addComponent(new Collider(player.getMesh(), false));
        ground.addComponent(new Collider(ground.getMesh(), false));

        ground.setLayer("Background");
        player.setLayer("Foreground");

        addObject(ground);
        addObject(player);

        UIElement sun = new UIElement(new Mesh(MShape.CIRCLE, Color.YELLOW), new Transform(new Vector2(50, -50), new de.craftix.engine.var.Dimension(75)), UIAlignment.TOP_LEFT);
        getUIManager().addElement(sun);

        UICheckBox checkBox = new UICheckBox(true, "CheckBox", new Transform(new Vector2(), new Dimension(40)), UIAlignment.CENTER);
        checkBox.setFont(new Font("Arial", Font.PLAIN, 40));
        checkBox.setColor(Color.RED);
        getUIManager().addElement(checkBox);
    }

    @Override
    public void update() {
        float speed = 10.0f;
        PhysicsComponent physics = GameEngine.getObjectByName("player").getComponent(PhysicsComponent.class);
        physics.setGravity(false);

        float vertical = InputManager.getAxis("Vertical") * speed * Screen.getDeltaTime();
        float horizontal = InputManager.getAxis("Horizontal") * speed * Screen.getDeltaTime();

        physics.addVelocity(new Vector2(horizontal, vertical));
    }
}
