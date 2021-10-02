package de.craftix.engine.objects.components;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.objects.GameObject;
import de.craftix.engine.var.Input;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;

public class InteractionComponent extends Component {
    private boolean isHovering = false;
    private final ActionListener hover;
    private final ActionListener click;

    public InteractionComponent(ActionListener hover, ActionListener click) {
        this.hover = hover;
        this.click = click;
        GameEngine.addInputs(new Inputs(this));
    }

    @Override
    public void update() {
        if (hover == null) return;
        if (checkIntersection()) {
            if (!isHovering)
                hover.actionPerformed(new ActionEvent(this, 0, "start"));
            hover.actionPerformed(new ActionEvent(this, 0, "hover"));
            isHovering = true;
        }else {
            if (isHovering)
                hover.actionPerformed(new ActionEvent(this, 0, "stop"));
            isHovering = false;
        }
    }

    private class Inputs extends Input {
        private final InteractionComponent c;
        public Inputs(InteractionComponent c) { this.c = c; }

        @Override
        public void mousePressed(MouseEvent e) {
            if (GameEngine.getScene() != object.getScene()) return;
            if (click == null) return;
            if (checkIntersection())
                click.actionPerformed(new ActionEvent(c, 0, "click"));
        }
    }

    private boolean checkIntersection() {
        Point mouse = InputManager.getMouseRaw().toPoint();
        Area area = object.getScreenShape();
        return area.contains(mouse);
    }

    public GameObject getGameObject() { return object; }
}
