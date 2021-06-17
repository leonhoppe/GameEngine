package de.craftix.test;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.objects.components.AnimationComponent;
import de.craftix.engine.render.*;
import de.craftix.engine.render.Shape;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.components.UIAnimationComponent;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Main extends GameEngine implements Screen.RenderingListener {
    private static final SpriteMap blocks = new SpriteMap(5, Sprite.load("terrain.png"), 16, 16);
    private static final GameObject grass = new GameObject(blocks.getSprite(3), new Transform(new Vector2(), new Dimension(2), Quaternion.IDENTITY()));

    public static void main(String[] args) {
        Screen.antialiasing(true);
        Screen.showFrames(true);
        Screen.setResizeable(false);
        Screen.setFramesPerSecond(120);
        Screen.setAntialiasingEffectTextures(false);
        InputManager.setFullscreenKey(KeyEvent.VK_F11);
        InputManager.setClosingKey(KeyEvent.VK_ESCAPE);
        InputManager.removeCursor();
        setup(800, 600, "GameEngine 3.0", new Main(), 120);
    }

    @Override
    public void initialise() {
        Screen.addLateRenderingListener(this);
        setIcon(blocks.getSprite(4));
        getActiveScene().setBackground(blocks.getSprite(1).resize(70, 70, Resizer.AVERAGE), false);
        instantiate(grass);

        Mesh mesh = new Mesh(Color.GREEN, Shape.CIRCLE, new Transform(new Vector2(), new Dimension(1), Quaternion.IDENTITY()));
        GameObject test = new GameObject(mesh, new Transform());
        instantiate(test);
    }

    @Override
    public void fixedUpdate() {
        float speed = 5 * grass.transform.position.dist(InputManager.getMousePos());
        grass.transform.lookAt(InputManager.getMousePos());
        grass.transform.translate(grass.transform.forward().mul(Screen.getFixedDeltaTime() * speed));
    }

    @Override
    public void onRender(Graphics2D g) {
        BufferedImage cursor = blocks.getSprite(2).texture;
        Vector2 mouse = InputManager.getMouseRaw();
        mouse.subSelf(new Vector2(
                cursor.getWidth() / 2f,
                cursor.getHeight() / 2f
        ));
        g.drawImage(cursor, mouse.toPoint().x, mouse.toPoint().y, null);
    }
}
