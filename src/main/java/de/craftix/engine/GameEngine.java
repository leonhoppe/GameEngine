package de.craftix.engine;

import de.craftix.engine.objects.GameObject;
import de.craftix.engine.objects.components.Component;
import de.craftix.engine.render.Camera;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.UIManager;
import de.craftix.engine.ui.components.UIComponent;
import de.craftix.engine.var.Input;
import de.craftix.engine.var.Scene;
import de.craftix.engine.var.Updater;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.Timer;

public class GameEngine {
    private static GameEngine instance;
    private static Screen screen;
    private static Scene scene;
    private static Logger logger;
    private static Logger globalLogger;
    private static int TPS;
    private static String appName;
    private static Timer screenTimer;
    private static Timer fixedTimer;

    private static final HashMap<String, Float> layers = new HashMap<>();
    private static final ArrayList<Object> updaters = new ArrayList<>();

    protected static void setup(int width, int height, String title, GameEngine instance, int tps) {
        TPS = tps;
        appName = title;
        logger = new Logger("GameEngine", true);
        globalLogger = new Logger(title);
        logger.info("INITIALISING");
        screenTimer = new Timer();
        fixedTimer = new Timer();
        logger.info("Timer initialised");
        GameEngine.instance = instance;
        logger.info("instance initialised");

        layers.put("Background", -1f);
        layers.put("Default", 0f);
        layers.put("Foreground", 1f);
        logger.info("Default layers created");

        screen = new Screen(width, height, title, (1000f / TPS) / 1000f);
        logger.info("Graphics initialised");

        scene = new Scene();
        scene.start();
        logger.info("Scene initialised");

        instance.initialise();
        logger.info("Initialising Method executed");
        Screen.setRender(true);

        fixedTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handleDeltaUpdates();
            }
        }, 0, 1000 / TPS);
        logger.info("FixedUpdate Thread initialised");
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Screen.updateFPS();
            }
        }, 0, 1000);
        logger.info("FPS Updater Thread initialised");

        instance.start();
        for (ScreenObject object : scene.getGameObjects())
            object.start();
        logger.info("Start Method for each GameObject executed");
        logger.info("Game started successfully");
    }

    private static void handleDeltaUpdates() {
        try {
            Screen.updateFixedDeltaTime();
            if (System.currentTimeMillis() - Screen.getProgramStartTime() < 500) return;
            instance.fixedUpdate();
            getScene().fixedUpdate();
            for (ScreenObject object : scene.getRawObjects()) {
                object.fixedUpdate();
                if (object instanceof GameObject)
                    for (Component component : ((GameObject) object).getComponents())
                        component.fixedUpdate();
            }
            for (UIElement element : getUIManager().getElements()) {
                element.fixedUpdate();
                for (UIComponent component : element.getComponents()) {
                    component.fixedUpdate();
                }
            }

            for (Object o : updaters) {
                for (Method m : o.getClass().getMethods()) {
                    if (m.isAnnotationPresent(Updater.class)) {
                        if (m.getDeclaredAnnotation(Updater.class).fixedUpdate()) {
                            try {
                                m.invoke(o);
                            }catch (Exception e) {
                                throwError(e);
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            throwError(e);
        }
    }

    public static void shutdown() {
        logger.info("Stopping Game...");
        screenTimer.cancel();
        fixedTimer.cancel();
        try { Thread.sleep(200); }
        catch (Exception e) { throwError(e); }
        GameEngine.instance.stop();
        for (ScreenObject object : GameEngine.getScene().getGameObjects())
            object.stop();
        logger.info("Stop Methods Executed");
        logger.info("Sending Stop command");
        System.exit(0);
    }

    public static void instantiate(GameObject object) {
        getScene().addObject(object);
        object.start();
    }
    public static void destroy(GameObject object) {
        getScene().removeObject(object);
        object.stop();
    }
    public static GameObject getObjectByName(String name) {
        for (GameObject all : getScene().getGameObjects()) {
            if (all.getName().equals(name))
                return all;
        }
        return null;
    }

    public static void instantiateUI(UIElement element) {
        getUIManager().addElement(element);
        element.start();
    }
    public static void destroyUI(UIElement element) {
        getUIManager().removeElement(element);
        element.stop();
    }

    public static InputStream loadFile(String path) {
        if (path == null) return null;
        try {
            return GameEngine.class.getClassLoader().getResourceAsStream(path);
        }catch (Exception e) { throwError(e); }
        return null;
    }

    public static void addInputs(Input input) {
        screen.addKeyListener(input);
        screen.addMouseListener(input);
        screen.addMouseMotionListener(input);
        screen.addMouseWheelListener(input);
        screen.addFocusListener(input);
    }

    public static void throwError(Exception e) {
        String className = e.getStackTrace()[e.getStackTrace().length - 1].getClassName();
        Logger log = new Logger(appName == null ? "GameEngine" : appName);
        log.warning("Error at " + Logger.ANSI_RED + className);
        log.getStream().print(Logger.ANSI_RED);
        e.printStackTrace(log.getStream());
        log.getStream().print(Logger.ANSI_RESET);
        shutdown();
    }

    public void fixedUpdate() {}
    public void update() {}
    public void start() {}
    public void stop() {}
    public void initialise() {}

    public static GameEngine getInstance() { return instance; }
    public static Camera getCamera() { return getScene().getCamera(); }
    public static Scene getScene() { return scene; }
    public static UIManager getUIManager() { return scene.getUIManager(); }
    public static Float getLayer(String name) { return layers.get(name); }
    public static HashMap<String, Float> getLayers() { return layers; }
    public static Screen getScreenInstance() { return screen; }
    public static Logger getLogger() { return globalLogger; }
    public static int getTPS() { return TPS; }
    public static Timer getScreenTimer() { return screenTimer; }
    public static Object[] getUpdaters() { return updaters.toArray(); }
    public static String awaitConsoleInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static void setScene(Scene scene) {
        GameEngine.scene.stop();
        GameEngine.scene = scene;
        GameEngine.scene.start();
    }
    public static void addLayer(String name, float layer) { if (!layers.containsValue(layer) && !layers.containsKey(name)) layers.put(name, layer); }
    public static void setIcon(Sprite sprite) {
        ImageIcon icon = new ImageIcon(sprite.texture);
        Screen.getDisplay().setIconImage(icon.getImage());
    }
    public static void addUpdater(Object instance) { GameEngine.updaters.add(instance); }
    public static void resetScreenTimer() { screenTimer = new Timer(); }
}
