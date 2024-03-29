package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.Logger;
import de.craftix.engine.objects.components.Component;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.components.UIComponent;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import javax.swing.*;
import java.awt.*;
import java.awt.Shape;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

public class Screen extends Canvas {
    protected static Screen instance;

    private static int bufferedFPS;
    private static final JFrame frame = new JFrame();
    private static boolean showGrid = false;
    private static int gridSize = 50;
    private static boolean showFrames = false;
    private static boolean antialiasing;
    private static int fps;
    private static float deltaTime;
    private static float fixedDeltaTime;
    private static long updateTimestamp;
    private static long fixedUpdateTimestamp;
    private static Rectangle bufferedBounds;
    private static boolean antialiasingEffectTextures = true;
    private static int framesPerSecond = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDisplayMode().getRefreshRate();
    private static BufferStrategy bs;
    private static boolean render = false;
    private static final long programStart = System.currentTimeMillis();
    private static boolean started = false;
    private static boolean fullscreen = false;
    private static TimerTask updateTask;

    private final static List<RenderingListener> earlyRenderingListener = new ArrayList<>();
    private final static List<RenderingListener> lateRenderingListener = new ArrayList<>();
    private final static List<PostRendering> postRenderer = new ArrayList<>();

    public Screen(int width, int height, String title, float fixedDeltaTime) {
        Logger logger = new Logger("Graphics", true);
        instance = this;
        Screen.fixedDeltaTime = fixedDeltaTime;
        logger.info("Attempting to set JFrame settings...");
        frame.setSize(width + 17, height + 40);
        frame.setTitle(title);
        frame.add("Center", this);
        frame.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
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

        if (fullscreen) {
            frame.setUndecorated(true);
            java.awt.Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setBounds(0, 0, dim.width, dim.height);
        }

        frame.setVisible(true);
        logger.info("JFrame settings set");

        logger.info("Starting FPS Management System...");
        updateTask = new TimerTask() {
            @Override
            public void run() {
                if (!render || !frame.isVisible()) return;
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                updateTimestamp = System.currentTimeMillis();
                executeUpdates();
                BufferedImage frame = exportFrame();
                if (Screen.postRenderer.size() != 0) {
                    for (int x = 0; x < frame.getWidth(); x++)
                        for (int y = 0; y < frame.getHeight(); y++) {
                            Color pixel = new Color(frame.getRGB(x, y));
                            for (PostRendering renderer : Screen.postRenderer) {
                                pixel = renderer.renderPixel(pixel, x, y, frame);
                            }
                            frame.setRGB(x, y, pixel.getRGB());
                        }
                }
                g.drawImage(frame, 0, 0, null);
                g.dispose();
                try { bs.show(); }catch (Exception e) {
                    createBuffers();
                }

                bufferedFPS++;
            }
        };
        createBuffers();
        GameEngine.getScreenTimer().scheduleAtFixedRate(updateTask, 0, 1000 / framesPerSecond);
        logger.info("FPS Management System started");

        InputManager iManager = new InputManager();
        addKeyListener(iManager);
        addMouseListener(iManager);
        logger.info("Input management system initialised");
        requestFocus();
        logger.info("Graphics loaded successfully");
        started = true;
    }

    private void createBuffers() {
        try {
            createBufferStrategy(3);
            bs = getBufferStrategy();
        }catch (Exception ignored) {}
    }

    private static long lastFrame;
    protected void executeUpdates() {
        deltaTime = (System.nanoTime() - lastFrame) / 1000000000f;
        lastFrame = System.nanoTime();
        if (System.currentTimeMillis() - programStart < 500) return;

        try {
            GameEngine.getInstance().update();
            GameEngine.getScene().update();
            for (int i = 0; i < GameEngine.getScene().getRawObjects().length; i++) {
                ScreenObject object = GameEngine.getScene().getRawObjects()[i];
                object.update();
                if (object instanceof GameObject)
                    for (Component component : ((GameObject) object).getComponents())
                        component.update();
            }

            for (int i = 0; i < GameEngine.getUIManager().getElements().size(); i++) {
                UIElement element = GameEngine.getUIManager().getElements().get(i);
                element.update();
                for (UIComponent component : element.getComponents()) {
                    component.update();
                }
            }

            for (Object o : GameEngine.getUpdaters()) {
                for (Method m : o.getClass().getMethods()) {
                    if (m.isAnnotationPresent(Updater.class)) {
                        if (m.getDeclaredAnnotation(Updater.class).update()) {
                            try {
                                m.invoke(o);
                            }catch (Exception e) {
                                GameEngine.throwError(e);
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            GameEngine.throwError(e);
        }
    }

    protected void renderBG(Graphics2D g) {
        if (antialiasing) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }

        if (GameEngine.getScene().getBackgroundColor() != null) {
            g.setColor(GameEngine.getScene().getBackgroundColor());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        if (GameEngine.getScene().getBackground() != null) {
            Sprite bg = GameEngine.getScene().getBackground();
            if (GameEngine.getScene().getBGAutoScale())
                g.drawImage(bg.getTextureRaw(getWidth(), getHeight()), 0, 0, null);
            else {
                bg.repeat = true;
                Transform transform = new Transform(new Vector2(), new Dimension(bg.texture.getWidth(), bg.texture.getHeight()), Quaternion.IDENTITY());
                bg.renderRaw(g, transform);
            }
        }
    }
    protected void render(Graphics2D g) {
        AffineTransform orig = (AffineTransform) g.getTransform().clone();
        g.translate(width() / 2f + GameEngine.getCamera().transform.position.x, height() / 2f + GameEngine.getCamera().transform.position.y);
        g.rotate(GameEngine.getCamera().transform.rotation.getAngle(), 0, 0);
        g.translate(-g.getTransform().getTranslateX(), -g.getTransform().getTranslateY());

        g.setColor(Color.BLACK);
        Shape self = new Rectangle(0, 0, getWidth(), getHeight());
        ArrayList<Float> layers = new ArrayList<>(GameEngine.getLayers().values());
        Collections.sort(layers);
        for (Float layer : layers) {
            for (int i = 0; i < GameEngine.getScene().getRawObjects().length; i++) {
                ScreenObject object = GameEngine.getScene().getRawObjects()[i];
                if (!object.isVisible()) continue;
                Area shape = object.getScreenShape();
                shape.intersect(new Area(self));
                if (shape.isEmpty()) continue;
                if (object.layer == layer) {
                    object.render(g);
                }
            }
        }
        g.setTransform(orig);

        GameEngine.getScene().getUIManager().renderComponents(g);

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
    }

    public static void updateFPS() {
        fps = bufferedFPS;
        bufferedFPS = 0;
    }

    private static long lastFixedUpdate = System.currentTimeMillis();
    public static void updateFixedDeltaTime() {
        fixedDeltaTime = (System.currentTimeMillis() - lastFixedUpdate) / 1000f;
        lastFixedUpdate = System.currentTimeMillis();
        fixedUpdateTimestamp = System.currentTimeMillis();
    }

    public static Point calculateScreenPosition(Transform transform) {
        Vector2 result = new Vector2(instance.getWidth() / 2f, instance.getHeight() / 2f);
        result.x -= GameEngine.getCamera().transform.position.x * GameEngine.getCamera().getScale();
        result.y += GameEngine.getCamera().transform.position.y * GameEngine.getCamera().getScale();
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
        result.sub(new Vector2(
                transform.scale.width / 2f,
                transform.scale.height / 2f
        ));
        result.add(new Vector2(
                (float) (transform.position.x * Math.sin(Math.toRadians(90) - transform.rotation.getAngle()) +
                        transform.position.y * Math.cos(Math.toRadians(90) - transform.rotation.getAngle())),
                (float) (transform.position.x * Math.cos(Math.toRadians(90) - transform.rotation.getAngle()) -
                        transform.position.y * Math.sin(Math.toRadians(90) - transform.rotation.getAngle()))
        ));
        return result.toPoint();
    }
    public static Vector2 calculateVirtualPosition(Vector2 pos) {
        pos = pos.copy();

        Point zero = calculateScreenPosition(new Transform(Vector2.zero(), new Dimension(), Quaternion.IDENTITY()));
        AffineTransform trans = new AffineTransform();
        trans.rotate(-GameEngine.getCamera().transform.rotation.getAngle(), zero.x, zero.y);
        Point2D temp = new Point2D.Float();
        trans.transform(pos.toPoint2D(), temp);
        pos = new Vector2(temp);

        Vector2 result = new Vector2(-(instance.getWidth() / 2f), -(instance.getHeight() / 2f));
        result.x += GameEngine.getCamera().transform.position.x + pos.x;
        result.y += -GameEngine.getCamera().transform.position.y + pos.y;
        result.x /= GameEngine.getCamera().getScale();
        result.y /= GameEngine.getCamera().getScale();
        result.y *= -1;

        return result;
    }

    private static long lastFullscreenChange = System.currentTimeMillis();

    public static void gridSize(int size) { gridSize = size; }
    public static void showGrid(boolean value) { showGrid = value; }
    public static void showFrames(boolean value) { showFrames = value; }
    public static void antialiasing(boolean value) { antialiasing = value; }
    public static boolean antialiasing() { return antialiasing; }
    public static void setResizeable(boolean value) { frame.setResizable(value); }
    public static void setFullscreen(boolean value) {
        if (!started) Screen.fullscreen = value;
        else {
            if (System.currentTimeMillis() - lastFullscreenChange < 200) return;
            render = false;
            bs.dispose();
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
            instance.createBufferStrategy(3);
            bs = instance.getBufferStrategy();
            instance.addNotify();
            render = true;
            lastFullscreenChange = System.currentTimeMillis();
        }
    }
    public static void setAntialiasingEffectTextures(boolean value) { antialiasingEffectTextures = value; }
    public static int getFPS() { return fps; }
    public static float getDeltaTime() { return deltaTime; }
    public static float getFixedDeltaTime() { return fixedDeltaTime; }
    public static long getUpdateTimestamp() { return updateTimestamp; }
    public static long getFixedUpdateTimestamp() { return fixedUpdateTimestamp; }
    public static int getBufferedFPS() { return bufferedFPS; }
    public static boolean isFullscreen() { return frame.isUndecorated(); }
    public static boolean antialiasingEffectTextures() { return antialiasingEffectTextures; }
    public static JFrame getDisplay() { return frame; }
    public static Screen getCanvas() { return instance; }
    public static long getProgramStartTime() { return programStart; }
    public static int width() { return instance.getWidth(); }
    public static int height() { return instance.getHeight(); }
    public static void setRender(boolean render) { Screen.render = render; }
    public static void setFramesPerSecond(int framesPerSecond) { Screen.framesPerSecond = framesPerSecond; }
    public static void addEarlyRenderingListener(RenderingListener listener) { earlyRenderingListener.add(listener); }
    public static void addLateRenderingListener(RenderingListener listener) { lateRenderingListener.add(listener); }
    public static void addPostRenderer(PostRendering renderer) { postRenderer.add(renderer); }
    public static void removePostRenderer(PostRendering renderer) { postRenderer.remove(renderer); }
    public static BufferedImage exportFrame() {
        BufferedImage frame = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) frame.getGraphics();
        instance.renderBG(g);
        earlyRenderingListener.forEach(listener -> listener.onRender(g));
        instance.render(g);
        lateRenderingListener.forEach(listener -> listener.onRender(g));
        g.dispose();
        return frame;
    }
    public static boolean isOnScreen(ScreenObject object) {
        Area screen = new Area(new Rectangle(0, 0, width(), height()));
        Area obj = object.getScreenShape();
        obj.intersect(screen);
        return !obj.isEmpty();
    }

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
