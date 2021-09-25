package de.craftix.engine;

import de.craftix.engine.render.Resizer;
import de.craftix.engine.render.Screen;
import de.craftix.engine.render.Sprite;

public class EngineSettings {
    private EngineSettings() {}

    public static void setWindowIcon(Sprite sprite) { GameEngine.setIcon(sprite); }
    public static void printSystemLog(boolean printSystemLog) { Logger.printSystemLog(printSystemLog); }
    public static void setResizingMethod(Resizer resizingMethod) { Sprite.setResizingMethod(resizingMethod); }

    public static void setFullscreenKey(int key) { InputManager.setFullscreenKey(key); }
    public static void setCloseKey(int key) { InputManager.setClosingKey(key); }
    public static void setCursor(int cursor) { InputManager.setCursor(cursor); }
    public static void setCursor(Sprite cursor) { InputManager.setCursor(cursor); }

    //Screen Options
    public static void setGridSize(int gridSize) { Screen.gridSize(gridSize); }
    public static void showGrid(boolean showGrid) { Screen.showGrid(showGrid); }
    public static void showFrames(boolean showFrames) { Screen.showFrames(showFrames); }
    public static void setAntialiasing(boolean antialiasing) { Screen.antialiasing(antialiasing); }
    public static void setResizable(boolean resizable) { Screen.setResizeable(resizable); }
    public static void setFullscreen(boolean fullscreen) { Screen.setFullscreen(fullscreen); }
    public static void setAntialiasingForTextures(boolean antialiasingForTextures) { Screen.setAntialiasingEffectTextures(antialiasingForTextures); }
    public static void setFPS(int fps) { Screen.setFramesPerSecond(fps); }
}
