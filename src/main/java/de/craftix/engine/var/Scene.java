package de.craftix.engine.var;

import de.craftix.engine.objects.GameObject;
import de.craftix.engine.render.Camera;
import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Sprite;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.UIManager;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Scene implements Serializable {
    private final List<ScreenObject> objects = new ArrayList<>();
    private final List<Image> images = new ArrayList<>();
    private final UIManager uiManager = new UIManager(this);
    private final Camera camera = new Camera();
    private Sprite background;
    private Color bg_Color;
    private boolean bg_autoscale;
    private boolean active = false;

    public GameObject[] getGameObjects() {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        for (ScreenObject object : objects) {
            if (object instanceof GameObject)
                gameObjects.add((GameObject) object);
        }
        return gameObjects.toArray(new GameObject[0]);
    }
    public ScreenObject[] getRawObjects() { return objects.toArray(new ScreenObject[0]); }
    public void addObject(ScreenObject object) { object.initialise(this); objects.add(object); }
    public void removeObject(ScreenObject object) {
        objects.remove(object);
    }

    public void addImage(Image image) { images.add(image); image.show(); }
    public void removeImage(Image image) { images.remove(image); image.hide(); }
    public Image[] getImages() { return images.toArray(new Image[0]); }

    public void setBackground(Sprite bg, boolean autoscale) { background = bg; bg_autoscale = autoscale; }
    public void setBackgroundColor(Color color) { this.bg_Color = color; }
    public Sprite getBackground() { return background; }
    public Color getBackgroundColor() { return bg_Color; }
    public boolean getBGAutoScale() { return bg_autoscale; }

    public UIManager getUIManager() { return uiManager; }
    public Camera getCamera() { return camera; }

    public void start() {
        active = true;
        onStart();

        for (ScreenObject obj : objects)
            obj.start();
        for (UIElement element : uiManager.getElements())
            element.start();

        for (Image image : images)
            image.show();
    }
    public void stop() {
        active = false;
        for (ScreenObject obj : objects)
            obj.stop();
        for (UIElement element : uiManager.getElements())
            element.stop();

        for (Image image : images)
            image.hide();

        onStop();
    }

    public boolean isActive() { return active; }

    public void onStart() {}
    public void onStop() {}
    public void update() {}
    public void fixedUpdate() {}
}
