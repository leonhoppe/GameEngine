package de.craftix.engine.var;

import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Camera;
import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.ui.UIManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Scene implements Serializable {
    private final List<ScreenObject> objects = new ArrayList<>();
    private final UIManager uiManager = new UIManager();
    private final Camera camera = new Camera();
    private Sprite background;

    public GameObject[] getGameObjects() {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        for (ScreenObject object : objects) {
            if (object instanceof GameObject)
                gameObjects.add((GameObject) object);
        }
        return gameObjects.toArray(new GameObject[0]);
    }
    public ScreenObject[] getRawObjects() { return objects.toArray(new ScreenObject[0]); }
    public void addObject(ScreenObject object) {
        objects.add(object);
    }
    public void removeObject(ScreenObject object) {
        objects.remove(object);
    }

    public void setBackground(Sprite bg) { background = bg; }
    public Sprite getBackground() { return background; }

    public UIManager getUIManager() { return uiManager; }
    public Camera getCamera() { return camera; }
}
