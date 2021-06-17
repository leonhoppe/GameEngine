package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.Logger;
import de.craftix.engine.objects.components.Component;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.var.Quaternion;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Updater;
import de.craftix.engine.var.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.Shape;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

public class Screen extends Canvas {
    protected static Screen instance;

    private static int bufferedFPS;
    private static Logger logger;
    private static JFrame frame = new JFrame();
    private static boolean showGrid = false;
    private static int gridSize = 50;
    private static boolean showFrames = false;
    private static boolean antialiasing;
    private static int fps;
    private static float deltaTime;
    private static float fixedDeltaTime;
    private static Rectangle bufferedBounds;
    private static boolean antialiasingEffectTextures = true;
    private static int framesPerSecond = 60;
    private static BufferStrategy bs;
    private static boolean render = true;

    private final static List<RenderingListener> earlyRenderingListener = new ArrayList<>();
    private final static List<RenderingListener> lateRenderingListener = new ArrayList<>();

    public Screen(int width, int height, String title, float fixedDeltaTime) {
        logger = new Logger("Graphics");
        instance = this;
        Screen.fixedDeltaTime = fixedDeltaTime;
        logger.info("Attempting to set JFrame settings...");
        frame.setSize(width + 17, height + 40);
        frame.setTitle(title);
        frame.add("Center", this);
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

        logger.info("Starting FPS Management System...");
        createBufferStrategy(3);
        bs = getBufferStrategy();
        GameEngine.getTimer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!render) return;
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                executeUpdates();
                earlyRenderingListener.forEach(listener -> listener.onRender(g));
                render(g);
                lateRenderingListener.forEach(listener -> listener.onRender(g));
                g.dispose();
                bs.show();
            }
        }, 0, 1000 / framesPerSecond);
        logger.info("FPS Management System started");

        InputManager iManager = new InputManager();
        addKeyListener(iManager);
        addMouseListener(iManager);
        logger.info("Input management system initialised");
        requestFocus();
        logger.info("Graphics loaded successfully");
    }

    private static long lastFrame = System.nanoTime();
    protected void executeUpdates() {
        deltaTime = (System.nanoTime() - lastFrame) / 1000000000f;
        lastFrame = System.nanoTime();

        GameEngine.getInstance().update();
        for (ScreenObject object : GameEngine.getActiveScene().getRawObjects()) {
            object.update();
            if (object.animation != null) object.animation.update();
            if (object instanceof GameObject)
                for (Component component : ((GameObject) object).getComponents())
                    component.update();
        }
        for (Updater updater : GameEngine.getUpdaters())
            updater.update();
    }

    protected void render(Graphics2D g) {
        if (antialiasing) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }

        if (GameEngine.getActiveScene().getBackground() != null) {
            if (GameEngine.getActiveScene().getBackgroundColor() != null) {
                g.setColor(GameEngine.getActiveScene().getBackgroundColor());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            if (GameEngine.getActiveScene().getBackground().texture != null) {
                Sprite bg = GameEngine.getActiveScene().getBackground();
                if (GameEngine.getActiveScene().getBGAutoScale())
                    g.drawImage(bg.getTextureRaw(getWidth(), getHeight()), 0, 0, null);
                else {
                    bg.repeat = true;
                    Transform transform = new Transform(new Vector2(), new Dimension(bg.texture.getWidth(), bg.texture.getHeight()), Quaternion.IDENTITY());
                    bg.renderRaw(g, transform);
                }
            }
        }

        //Apply Camera Transform
        AffineTransform orig = g.getTransform();
        g.translate(width() / 2f + GameEngine.getCamera().transform.position.x, height() / 2f + GameEngine.getCamera().transform.position.y);
        g.rotate(GameEngine.getCamera().transform.rotation.getAngle(), 0, 0);
        g.translate(-g.getTransform().getTranslateX(), -g.getTransform().getTranslateY());

        g.setColor(Color.BLACK);
        Shape self = new Rectangle(0, 0, getWidth(), getHeight());
        ArrayList<Float> layers = new ArrayList<>(GameEngine.getLayers().values());
        Collections.sort(layers);
        for (Float layer : layers) {
            for (ScreenObject object : GameEngine.getActiveScene().getRawObjects()) {
                Area shape = object.getScreenShape();
                shape.intersect(new Area(self));
                if (shape.isEmpty()) continue;
                if (object.layer == layer)
                    object.render(g);
                if (object.renderBounds) g.draw(object.getScreenShape());
            }
        }
        g.setTransform(orig);

        GameEngine.getActiveScene().getUIManager().renderComponents(g);

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
    }

    public static void updateFPS() {
        fps = bufferedFPS;
        bufferedFPS = 0;
    }

    private static long lastFixedUpdate = System.currentTimeMillis();
    public static void updateFixedDeltaTime() {
        fixedDeltaTime = (System.currentTimeMillis() - lastFixedUpdate) / 1000f;
        lastFixedUpdate = System.currentTimeMillis();
    }

    public static Point calculateScreenPosition(Transform transform) {
        Vector2 result = new Vector2(instance.getWidth() / 2f, instance.getHeight() / 2f);
        result.x -= GameEngine.getCamera().transform.position.x;
        result.y += GameEngine.getCamera().transform.position.y;
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
        result.x += GameEngine.getCamera().transform.position.x + pos.x;
        result.y += -GameEngine.getCamera().transform.position.y + pos.y;
        result.x /= GameEngine.getCamera().getScale();
        result.y /= GameEngine.getCamera().getScale();
        result.y *= -1;

        return result;
    }

    public static void gridSize(int size) { gridSize = size; }
    public static void showGrid(boolean value) { showGrid = value; }
    public static void showFrames(boolean value) { showFrames = value; }
    public static void antialiasing(boolean value) { antialiasing = value; }
    public static boolean antialiasing() { return antialiasing; }
    public static void setResizeable(boolean value) { frame.setResizable(value); }
    public static void setFullscreen(boolean value) {
        render = false;
        if (value) {
            bufferedBounds = frame.getBounds();
            frame.setVisible(false);
            frame.dispose();
            frame.setUndecorated(true);
            java.awt.Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
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
        instance.addNotify();
        render = true;
    }
    public static void setAntialiasingEffectTextures(boolean value) { antialiasingEffectTextures = value; }
    public static int getFPS() { return fps; }
    public static float getDeltaTime() { return deltaTime; }
    public static float getFixedDeltaTime() { return fixedDeltaTime; }
    public static int getBufferedFPS() { return bufferedFPS; }
    public static boolean isFullscreen() { return frame.isUndecorated(); }
    public static boolean antialiasingEffectTextures() { return antialiasingEffectTextures; }
    public static JFrame getDisplay() { return frame; }
    public static Screen getCanvas() { return instance; }
    public static int width() { return instance.getWidth(); }
    public static int height() { return instance.getHeight(); }
    public static void setFramesPerSecond(int framesPerSecond) { Screen.framesPerSecond = framesPerSecond; }
    public static void addEarlyRenderingListener(RenderingListener listener) { earlyRenderingListener.add(listener); }
    public static void addLateRenderingListener(RenderingListener listener) { lateRenderingListener.add(listener); }

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

    public interface RenderingListener {
        void onRender(Graphics2D g);
    }
}
