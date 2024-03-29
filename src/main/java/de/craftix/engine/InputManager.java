package de.craftix.engine;

import de.craftix.engine.render.Screen;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.var.Input;
import de.craftix.engine.var.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class InputManager extends Input {
    private static boolean activated = true;

    private static final boolean[] keys = new boolean[1024];
    private static final boolean[] mouseButtons = new boolean[MouseInfo.getNumberOfButtons()];

    private static int fullscreenKey;
    private static boolean useFullscreenKey = false;

    private static int closingKey;
    private static boolean useClosingKey = false;

    @Override
    public void mousePressed(MouseEvent e) { if (activated) mouseButtons[e.getButton() - 1] = true; }
    @Override
    public void mouseReleased(MouseEvent e) { mouseButtons[e.getButton() - 1] = false; }
    @Override
    public void keyPressed(KeyEvent e) {
        if (!activated) return;
        keys[e.getKeyCode()] = true;
        if (useFullscreenKey && e.getKeyCode() == fullscreenKey)
            Screen.setFullscreen(!Screen.isFullscreen());
        if (useClosingKey && e.getKeyCode() == closingKey)
            GameEngine.shutdown();
    }
    @Override
    public void keyReleased(KeyEvent e) { keys[e.getKeyCode()] = false; }

    public static Vector2 getMouseRaw() {
        Point mouse = new Point(MouseInfo.getPointerInfo().getLocation());
        SwingUtilities.convertPointFromScreen(mouse, GameEngine.getScreenInstance());
        return new Vector2(mouse);
    }
    public static Vector2 getMousePos() { return Screen.calculateVirtualPosition(getMouseRaw()); }
    public static boolean isKeyPressed(int key) { return keys[key]; }
    public static boolean isMouseClicked(int button) { return mouseButtons[button - 1]; }
    public static Cursor getCursor() { return Screen.getDisplay().getCursor(); }

    public static void setCursor(int cursor) {
        Cursor c = new Cursor(cursor);
        Screen.getDisplay().setCursor(c);
    }
    public static void setCursor(Sprite cursor) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image img = cursor.texture;
        Cursor c = toolkit.createCustomCursor(img, new Point(Screen.getDisplay().getX(), Screen.getDisplay().getY()), "img");
        Screen.getDisplay().setCursor(c);
    }
    public static void setCursor(Cursor cursor) {
        Screen.getDisplay().setCursor(cursor);
    }
    public static void removeCursor() {
        Screen.getDisplay().setCursor(Screen.getDisplay().getToolkit().createCustomCursor(
                new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),
                new Point(),
                null ) );
    }
    public static void setFullscreenKey(int key) {
        fullscreenKey = key;
        useFullscreenKey = true;
    }
    public static void setClosingKey(int key) {
        closingKey = key;
        useClosingKey = true;
    }
    public static void setActivated(boolean activated) { InputManager.activated = activated; }

    public static float getAxis(String name) {
        float value = 0;
        if (name.equalsIgnoreCase("horizontal")) {
            if (isKeyPressed(KeyEvent.VK_A)) value += -1;
            if (isKeyPressed(KeyEvent.VK_D)) value += 1;
        }
        if (name.equalsIgnoreCase("vertical")) {
            if (isKeyPressed(KeyEvent.VK_S)) value += -1;
            if (isKeyPressed(KeyEvent.VK_W)) value += 1;
        }
        return value;
    }
}
