package de.craftix.engine;

import de.craftix.engine.objects.Component;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.var.Input;
import de.craftix.engine.var.Scene;
import de.craftix.engine.var.Vector2;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class GameEngine {
    private static GameEngine instance;
    private static Screen screen;
    private static Vector2 camera;
    private static Scene activeScene;
    private static Logger logger;
    private static Logger globalLogger;

    private static final HashMap<String, Float> layers = new HashMap<>();

    protected static void setup(int width, int height, String title, GameEngine instance, int tps) {
        Logger.globalInfo("Initialising requirements...");
        logger = new Logger("GameEngine");
        globalLogger = new Logger(title);
        logger.info("Logger initialised");
        camera = new Vector2();
        logger.info("Camera initialised");
        activeScene = new Scene();
        logger.info("Scene initialised");
        GameEngine.instance = instance;
        logger.info("instance initialised");

        layers.put("Background", -1f);
        layers.put("Default", 0f);
        layers.put("Foreground", 1f);
        logger.info("Default layers created");

        instance.initialise();
        logger.info("Initialising Method executed");

        screen = new Screen(width, height, title);
        logger.info("Graphics initialised");

        new Timer(1000 / tps, (e) -> handleDeltaUpdates()).start();
        logger.info("FixedUpdate Thread initialised");

        instance.start();
        for (ScreenObject object : activeScene.objects)
            object.start();
        logger.info("Start Method for each GameObject executed");
        logger.info("Game started successfully");
    }

    private static void handleDeltaUpdates() {
        instance.fixedUpdate();
        for (ScreenObject object : activeScene.objects) {
            object.fixedUpdate();
            if (object instanceof GameObject)
                for (Component component : ((GameObject) object).getComponents())
                    component.fixedUpdate();
        }
    }

    public static void shutdown() {
        logger.info("Stopping Game...");
        GameEngine.instance.stop();
        for (ScreenObject object : GameEngine.getActiveScene().objects)
            object.stop();
        logger.info("Stop Methods Executed");
        logger.info("Sending Stop command");
        System.exit(-1);
    }

    public static void instantiate(GameObject object) {
        getActiveScene().objects.add(object);
        object.start();
    }

    public static void destroy(GameObject object) {
        getActiveScene().objects.remove(object);
        object.stop();
    }

    public static File loadFile(String path) {
        try {
            return new File(Objects.requireNonNull(GameEngine.class.getClassLoader().getResource(path)).getPath());
        }catch (Exception e) { e.printStackTrace(); }
        throw new NullPointerException("null");
    }

    public static void addInputs(Input input) {
        screen.addKeyListener(input);
        screen.addMouseListener(input);
        screen.addMouseMotionListener(input);
        screen.addMouseWheelListener(input);
    }

    public void fixedUpdate() {}
    public void update() {}
    public void start() {}
    public void stop() {}
    public void initialise() {}

    public static GameEngine getInstance() { return instance; }
    public static Vector2 getCamera() { return camera; }
    public static Scene getActiveScene() { return activeScene; }
    public static Float getLayer(String name) { return layers.get(name); }
    public static HashMap<String, Float> getLayers() { return layers; }
    public static Screen getScreenInstance() { return screen; }
    public static Logger getLogger() { return globalLogger; }

    public static void setActiveScene(Scene scene) { activeScene = scene; }
    public static void addLayer(String name, float layer) { if (!layers.containsValue(layer) && !layers.containsKey(name)) layers.put(name, layer); }
}
