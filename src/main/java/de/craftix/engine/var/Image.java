package de.craftix.engine.var;

import de.craftix.engine.GameEngine;
import de.craftix.engine.render.Screen;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

public class Image implements Serializable {
    private URL url;
    private JLabel canvas;
    private Transform transform;

    public Image(URI URL) {
        try {
            this.url = URL.toURL();
        } catch (Exception e) {
            GameEngine.throwError(e);
        }
        GameEngine.addUpdater(this);
    }

    public void show(Vector2 pos, Dimension size) {
        if (Arrays.asList(Screen.getDisplay().getComponents()).contains(canvas)) return;
        ImageIcon icon = new ImageIcon(url);
        icon.setImage(icon.getImage().getScaledInstance(size.getWidth(), size.getHeight(), java.awt.Image.SCALE_DEFAULT));
        canvas = new JLabel(icon);
        transform = new Transform(pos, size);
        Point point = Screen.calculateRawScreenPosition(transform);
        canvas.setBounds(point.x, point.y, icon.getIconWidth(), icon.getIconHeight());
        Screen.getDisplay().add(canvas);
    }
    public void hide() { Screen.getDisplay().remove(canvas); }

    @Updater(update = true)
    public void update() {
        if (canvas == null) return;
        Point p = Screen.calculateRawScreenPosition(transform);
        canvas.setBounds(p.x, p.y, transform.scale.getWidth(), transform.scale.getHeight());
    }
}
