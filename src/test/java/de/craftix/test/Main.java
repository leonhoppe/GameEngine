package de.craftix.test;

import de.craftix.engine.EngineSettings;
import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.objects.components.Collider;
import de.craftix.engine.objects.components.PhysicsComponent;
import de.craftix.engine.render.*;
import de.craftix.engine.render.MShape;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.ui.elements.UIButton;
import de.craftix.engine.ui.elements.UICheckBox;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Main extends GameEngine {

    public static void main(String[] args) {
        EngineSettings.setAntialiasing(true);
        EngineSettings.setAntialiasingForTextures(false);
        EngineSettings.showFrames(true);
        EngineSettings.setResizable(true);
        EngineSettings.setFullscreenKey(KeyEvent.VK_F11);
        EngineSettings.setCloseKey(KeyEvent.VK_ESCAPE);
        EngineSettings.printSystemLog(false);
        setup(1280, 720, "GameEngine 3.0", new Main(), 60);
    }

    @Override
    public void initialise() {
        //setScene(new LoginScene());
        getScene().setBackgroundColor(Color.RED);
        getUIManager().loadHTML(loadFile("ui.html"), loadFile("ui.css"));
    }
}
