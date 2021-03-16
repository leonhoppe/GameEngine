package de.craftix.engine.var;

import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    public final List<ScreenObject> objects = new ArrayList<>();
    public Sprite background;

    public GameObject[] getGameObjects() {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        for (ScreenObject object : objects) {
            if (object instanceof GameObject)
                gameObjects.add((GameObject) object);
        }
        return gameObjects.toArray(new GameObject[0]);
    }

}
