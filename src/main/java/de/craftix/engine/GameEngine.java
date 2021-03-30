package de.craftix.engine;

import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Camera;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.var.Input;
import de.craftix.engine.var.Scene;
import de.craftix.engine.var.Updater;

import javax.swing.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.Timer;

public class GameEngine {
    private static GameEngine instance;
    private static Screen screen;
    private static Scene activeScene;
    private static Logger logger;
    private static Logger globalLogger;
    private static int TPS;

    private static final HashMap<String, Float> layers = new HashMap<>();
    private static final ArrayList<Updater> updater = new ArrayList<>();

    protected static void setup(int width, int height, String title, GameEngine instance, int tps, boolean startGame) {
        TPS = tps;
        Logger.globalInfo("Initialising requirements...");
        logger = new Logger("GameEngine");
        globalLogger = new Logger(title);
        logger.info("Logger initialised");
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

        screen = new Screen(width, height, title, (1000f / TPS) / 1000f, startGame);
        logger.info("Graphics initialised");

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
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
        for (ScreenObject object : activeScene.getGameObjects())
            object.start();
        logger.info("Start Method for each GameObject executed");
        logger.info("Game started successfully");
    }

    private static void handleDeltaUpdates() {
        Screen.updateFixedDeltaTime();
        instance.fixedUpdate();
        for (ScreenObject object : activeScene.getGameObjects())
            object.fixedUpdate();
        for (Updater u : updater)
            u.fixedUpdate();
    }

    public static void startGame() { Screen.getDisplay().setVisible(true); }
    public static void shutdown() {
        logger.info("Stopping Game...");
        try { Thread.sleep(200); }
        catch (Exception e) { e.printStackTrace(); }
        GameEngine.instance.stop();
        for (ScreenObject object : GameEngine.getActiveScene().getGameObjects())
            object.stop();
        logger.info("Stop Methods Executed");
        logger.info("Sending Stop command");
        System.exit(0);
    }

    public static void instantiate(GameObject object) {
        getActiveScene().addObject(object);
        object.start();
    }
    public static void destroy(GameObject object) {
        getActiveScene().removeObject(object);
        object.stop();
    }

    public static URI loadFile(String path) {
        try {
            URL url = GameEngine.class.getResource(path);
            return new URI(url.toString().replace(" ","%20"));
        }catch (Exception e) { e.printStackTrace(); }
        return null;
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
    public static Camera getCamera() { return getActiveScene().getCamera(); }
    public static Scene getActiveScene() { return activeScene; }
    public static Float getLayer(String name) { return layers.get(name); }
    public static HashMap<String, Float> getLayers() { return layers; }
    public static Screen getScreenInstance() { return screen; }
    public static Logger getLogger() { return globalLogger; }
    public static int getTPS() { return TPS; }
    public static Updater[] getUpdaters() { return updater.toArray(new Updater[0]); }

    public static void setActiveScene(Scene scene) { activeScene = scene; }
    public static void addLayer(String name, float layer) { if (!layers.containsValue(layer) && !layers.containsKey(name)) layers.put(name, layer); }
    public static void setIcon(Sprite sprite) {
        ImageIcon icon = new ImageIcon(sprite.texture);
        Screen.getDisplay().setIconImage(icon.getImage());
    }
    public static void addUpdater(Updater updater) { GameEngine.updater.add(updater); }

}
