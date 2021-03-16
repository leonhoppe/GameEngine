package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.Logger;
import de.craftix.engine.objects.Component;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;

public class Screen extends JLabel {
    public final JFrame frame;
    public static int width;
    public static int height;
    public static boolean antialiasing;
    public static int fps;
    public static int deltaTime;
    public static boolean showFrames;

    private static int bufferedFPS;
    private static long lastFrame;
    private static Logger logger;

    public Screen(int width, int height, String title) {
        logger = new Logger("Graphics");
        Screen.width = width;
        Screen.height = height;
        logger.info("Attempting to set JFrame settings...");
        frame = new JFrame();
        frame.setSize(width + 17, height + 40);
        frame.setTitle(title);
        frame.setLayout(null);
        frame.setContentPane(this);
        frame.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                GameEngine.shutdown();
            }
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        frame.setVisible(true);
        logger.info("JFrame settings set");

        new Timer(1000, (e) -> { fps = bufferedFPS; bufferedFPS = 0; }).start();
        logger.info("FPS Updater initialised");
        InputManager iManager = new InputManager();
        addKeyListener(iManager);
        addMouseListener(iManager);
        logger.info("Input management system initialised");
        requestFocus();
        logger.info("Graphics loaded successfully");
    }

    @Override
    protected void paintComponent(Graphics g) {
        deltaTime = (int) (lastFrame - System.currentTimeMillis());
        lastFrame = System.currentTimeMillis();

        GameEngine.getInstance().update();
        for (ScreenObject object : GameEngine.getActiveScene().getGameObjects()) {
            object.update();
            if (object.animation != null) object.animation.update();
            if (object instanceof GameObject)
                for (Component component : ((GameObject) object).getComponents())
                    component.update();
        };

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (antialiasing)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        else
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        if (GameEngine.getActiveScene().getBackground() != null) {
            if (GameEngine.getActiveScene().getBackground().texture == null && GameEngine.getActiveScene().getBackground().color != null) {
                g.setColor(GameEngine.getActiveScene().getBackground().color);
                g.fillRect(0, 0, width, height);
            }
            if (GameEngine.getActiveScene().getBackground().texture != null) {
                if (GameEngine.getActiveScene().getBackground().texture.getWidth() != width ||
                        GameEngine.getActiveScene().getBackground().texture.getHeight() != height) {
                    if (GameEngine.getActiveScene().getBackground().bufferedTexture == null ||
                            GameEngine.getActiveScene().getBackground().bufferedTexture.getWidth() != width ||
                            GameEngine.getActiveScene().getBackground().bufferedTexture.getHeight() != height) {
                        GameEngine.getActiveScene().getBackground().bufferedTexture = Resizer.AVERAGE.resize(GameEngine.getActiveScene().getBackground().texture, width, height);
                    }
                    g.drawImage(GameEngine.getActiveScene().getBackground().bufferedTexture, 0, 0, null);
                }
            }
        }

        ArrayList<Float> layers = new ArrayList<>(GameEngine.getLayers().values());
        Collections.sort(layers);
        for (Float layer : layers) {
            for (ScreenObject object : GameEngine.getActiveScene().getRawObjects()) {
                if (!object.visible) continue;
                if (object.layer == layer) {
                    object.render(g2);
                }
            }
        }

        g.setColor(Color.WHITE);
        int gridSize = 50;
        for (int x = 0; x <= width / gridSize; x++)
            g.drawLine(x * gridSize, 0, x * gridSize, height);
        for (int y = 0; y <= height / gridSize; y++)
            g.drawLine(0, y * gridSize, width, y * gridSize);

        if (showFrames) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(9, 2, 55, 9);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Tacoma", Font.BOLD, 10));
            g.drawString("FPS: " + fps, 10, 10);
        }

        bufferedFPS++;
        repaint();
    }

    public static Point calculateScreenPosition(Transform transform) {
        Vector2 result = new Vector2(width / 2f, height / 2f);
        result.x -= GameEngine.getCamera().x;
        result.y += GameEngine.getCamera().y;
        result.x -= transform.scale.width / 2f;
        result.y -= transform.scale.height / 2f;
        result.y += transform.position.x * Math.cos(Math.toRadians(90) - transform.rotation.getAngle()) -
                transform.position.y * Math.sin(Math.toRadians(90) - transform.rotation.getAngle());
        result.x += transform.position.x * Math.sin(Math.toRadians(90) - transform.rotation.getAngle()) +
                transform.position.y * Math.cos(Math.toRadians(90) - transform.rotation.getAngle());
        return result.toPoint();
    }

    public static Vector2 calculateVirtualPosition(Vector2 pos) {
        Vector2 result = new Vector2(-((float) width / 2), -((float) height / 2));
        result.x += GameEngine.getCamera().x + pos.x;
        result.y += -GameEngine.getCamera().y + pos.y;
        result.y *= -1;
        return result;
    }
}
