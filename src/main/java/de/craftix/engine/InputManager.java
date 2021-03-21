package de.craftix.engine;

import de.craftix.engine.render.Screen;
import de.craftix.engine.var.Input;
import de.craftix.engine.var.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class InputManager extends Input {
    private static final boolean[] keys = new boolean[256];
    private static final boolean[] mouseButtons = new boolean[MouseInfo.getNumberOfButtons()];

    private static int fullscreenKey;
    private static boolean useFullscreenKey = false;

    @Override
    public void mousePressed(MouseEvent e) { mouseButtons[e.getButton() - 1] = true; }
    @Override
    public void mouseReleased(MouseEvent e) { mouseButtons[e.getButton() - 1] = false; }
    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        if (useFullscreenKey && e.getKeyCode() == fullscreenKey)
            Screen.setFullscreen(!Screen.isFullscreen());
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

    public static void setFullscreenKey(int key) {
        fullscreenKey = key;
        useFullscreenKey = true;
    }
}
