package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.Logger;
import de.craftix.engine.objects.Component;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Updater;
import de.craftix.engine.var.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.Shape;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;

public class Screen extends JLabel {
    protected static Screen instance;

    private static int bufferedFPS;
    private static long lastFrame;
    private static Logger logger;
    private static JFrame frame = new JFrame();
    private static boolean showGrid = false;
    private static int gridSize = 50;
    private static boolean showFrames = false;
    private static boolean antialiasing;
    private static int fps;
    private static float deltaTime;
    private static float fixedDeltaTime;
    private static boolean limitFPS = false;
    private static Rectangle bufferedBounds;
    private static boolean antialiasingEffectTextures = true;

    public Screen(int width, int height, String title, float fixedDeltaTime, boolean startGame) {
        logger = new Logger("Graphics");
        instance = this;
        Screen.fixedDeltaTime = fixedDeltaTime;
        logger.info("Attempting to set JFrame settings...");
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
        frame.setVisible(startGame);
        logger.info("JFrame settings set");

        InputManager iManager = new InputManager();
        addKeyListener(iManager);
        addMouseListener(iManager);
        logger.info("Input management system initialised");
        requestFocus();
        logger.info("Graphics loaded successfully");
    }

    @Override
    protected void paintComponent(Graphics g) {
        deltaTime = (System.nanoTime() - lastFrame) / 1000000000f;
        lastFrame = System.nanoTime();

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
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            if (GameEngine.getActiveScene().getBackground().texture != null) {
                if (GameEngine.getActiveScene().getBackground().texture.getWidth() != getWidth() ||
                        GameEngine.getActiveScene().getBackground().texture.getHeight() != getHeight()) {
                    if (GameEngine.getActiveScene().getBackground().bufferedTexture == null ||
                            GameEngine.getActiveScene().getBackground().bufferedTexture.getWidth() != getWidth() ||
                            GameEngine.getActiveScene().getBackground().bufferedTexture.getHeight() != getHeight()) {
                        GameEngine.getActiveScene().getBackground().bufferedTexture = Resizer.AVERAGE.resize(GameEngine.getActiveScene().getBackground().texture, getWidth(), getHeight());
                    }
                    g.drawImage(GameEngine.getActiveScene().getBackground().bufferedTexture, 0, 0, null);
                }
            }
        }

        g2.setColor(Color.BLACK);
        Shape self = new Rectangle(0, 0, getWidth(), getHeight());
        ArrayList<Float> layers = new ArrayList<>(GameEngine.getLayers().values());
        Collections.sort(layers);
        for (Float layer : layers) {
            for (ScreenObject object : GameEngine.getActiveScene().getRawObjects()) {
                Area shape = object.getScreenShape();
                shape.intersect(new Area(self));
                if (shape.isEmpty()) continue;
                if (object.renderBounds) g2.draw(object.getScreenShape());
                if (shape.isEmpty()) continue;
                if (object.layer == layer)
                    object.render(g2);
            }
        }

        if (showGrid) {
            g.setColor(Color.WHITE);
            for (int x = 0; x <= getWidth() / gridSize; x++)
                g.drawLine(x * gridSize, 0, x * gridSize, getHeight());
            for (int y = 0; y <= getHeight() / gridSize; y++)
                g.drawLine(0, y * gridSize, getWidth(), y * gridSize);
        }

        if (showFrames) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(2, 2, 55, 9);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Tacoma", Font.BOLD, 10));
            g.drawString("FPS: " + fps, 3, 10);
        }

        bufferedFPS++;
        if (limitFPS && (fps > 100 || bufferedFPS > 100))
            try {
                Thread.sleep(5);
            }catch (Exception e) {
                e.printStackTrace();
            }
        repaint();
    }
    public static void updateFPS(ArrayList<Updater> updaters) {
        fps = bufferedFPS;
        bufferedFPS = 0;
        for (Updater updater : updaters)
            updater.update();
    }

    public static Point calculateScreenPosition(Transform transform) {
        Vector2 result = new Vector2(instance.getWidth() / 2f, instance.getHeight() / 2f);
        result.x -= GameEngine.getCamera().x;
        result.y += GameEngine.getCamera().y;
        result.x -= (transform.scale.width * GameEngine.getCamera().getScale()) / 2f;
        result.y -= (transform.scale.height * GameEngine.getCamera().getScale()) / 2f;
        result.y += (transform.position.x * GameEngine.getCamera().getScale()) * Math.cos(Math.toRadians(90) - transform.rotation.getAngle()) -
                (transform.position.y * GameEngine.getCamera().getScale()) * Math.sin(Math.toRadians(90) - transform.rotation.getAngle());
        result.x += (transform.position.x * GameEngine.getCamera().getScale()) * Math.sin(Math.toRadians(90) - transform.rotation.getAngle()) +
                (transform.position.y * GameEngine.getCamera().getScale()) * Math.cos(Math.toRadians(90) - transform.rotation.getAngle());
        return result.toPoint();
    }
    public static Point calculateRawScreenPosition(Transform transform) {
        Vector2 result = new Vector2(instance.getWidth() / 2f, instance.getHeight() / 2f);
        result.subSelf(new Vector2(
                transform.scale.width / 2f,
                transform.scale.height / 2f
        ));
        result.addSelf(new Vector2(
                (float) (transform.position.x * Math.sin(Math.toRadians(90) - transform.rotation.getAngle()) +
                        transform.position.y * Math.cos(Math.toRadians(90) - transform.rotation.getAngle())),
                (float) (transform.position.x * Math.cos(Math.toRadians(90) - transform.rotation.getAngle()) -
                        transform.position.y * Math.sin(Math.toRadians(90) - transform.rotation.getAngle()))
        ));
        return result.toPoint();
    }
    public static Vector2 calculateVirtualPosition(Vector2 pos) {
        Vector2 result = new Vector2(-(instance.getWidth() / 2f), -(instance.getHeight() / 2f));
        result.x += GameEngine.getCamera().x + pos.x;
        result.y += -GameEngine.getCamera().y + pos.y;
        result.x /= GameEngine.getCamera().getScale();
        result.y /= GameEngine.getCamera().getScale();
        result.y *= -1;
        return result;
    }

    public static void gridSize(int size) { gridSize = size; }
    public static void showGrid(boolean value) { showGrid = value; }
    public static void showFrames(boolean value) { showFrames = value; }
    public static void antialiasing(boolean value) { antialiasing = value; }
    public static void setResizeable(boolean value) { frame.setResizable(value); }
    public static void limitFPS(boolean value) { limitFPS = value; }
    public static void setFullscreen(boolean value) {
        if (value) {
            bufferedBounds = frame.getBounds();
            frame.setVisible(false);
            frame.dispose();
            frame.setUndecorated(true);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setBounds(0, 0, dim.width, dim.height);
            frame.setVisible(true);
            instance.requestFocus();
        }else {
            frame.setVisible(false);
            frame.dispose();
            frame.setUndecorated(false);
            frame.setBounds(bufferedBounds);
            frame.setVisible(true);
            instance.requestFocus();
        }
    }
    public static void setAntialiasingEffectTextures(boolean value) { antialiasingEffectTextures = value; }
    public static int getFPS() { return fps; }
    public static float getDeltaTime() { return deltaTime; }
    public static float getFixedDeltaTime() { return fixedDeltaTime; }
    public static int getBufferedFPS() { return bufferedFPS; }
    public static boolean isFullscreen() { return frame.isUndecorated(); }
    public static boolean antialiasingEffectTextures() { return antialiasingEffectTextures; }
    public static JFrame getDisplay() { return frame; }
    public static int width() { return instance.getWidth(); }
    public static int height() { return instance.getHeight(); }

    public static AffineTransform getTransform(Transform transform) {
        Point pos = Screen.calculateScreenPosition(transform);
        AffineTransform trans = new AffineTransform();
        trans.translate(pos.x + ((transform.scale.width * (GameEngine.getCamera().getScale())) / 2f), pos.y + (transform.scale.height * (GameEngine.getCamera().getScale())) / 2f);
        trans.rotate(transform.rotation.getAngle(), transform.position.x * (GameEngine.getCamera().getScale()), -transform.position.y * (GameEngine.getCamera().getScale()));
        return trans;
    }
    public static AffineTransform getRawTransform(Transform transform) {
        AffineTransform trans = new AffineTransform();
        trans.translate(transform.position.x + (transform.scale.width / 2f), transform.position.y + (transform.scale.height / 2f));
        trans.rotate(transform.rotation.getAngle(), 0, 0);
        return trans;
    }
}
