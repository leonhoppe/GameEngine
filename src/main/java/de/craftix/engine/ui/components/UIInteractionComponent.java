package de.craftix.engine.ui.components;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.var.Input;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;

public class UIInteractionComponent extends UIComponent {

    private boolean hovering = false;
    private ActionListener hover;
    private ActionListener click;

    public UIInteractionComponent(ActionListener hover, ActionListener click) {
        this.hover = hover;
        this.click = click;
        GameEngine.addInputs(new UIInteractionComponent.Inputs(this));
    }

    @Override
    public void render(Graphics2D g) {}

    @Override
    public void update() {
        if (hover == null) return;
        if (checkIntersection()) {
            if (!hovering)
                hover.actionPerformed(new ActionEvent(this, 0, "start"));
            hover.actionPerformed(new ActionEvent(this, 0, "hover"));
            hovering = true;
        }else {
            if (hovering)
                hover.actionPerformed(new ActionEvent(this, 0, "stop"));
            hovering = false;
        }
    }

    private class Inputs extends Input {
        private final UIInteractionComponent c;
        public Inputs(UIInteractionComponent c) { this.c = c; }

        @Override
        public void mousePressed(MouseEvent e) {
            if (GameEngine.getScene() != element.getScene()) return;
            if (click == null) return;
            if (checkIntersection())
                click.actionPerformed(new ActionEvent(c, 0, "click"));
        }
    }

    private boolean checkIntersection() {
        Point mouse = InputManager.getMouseRaw().toPoint();
        Area area = element.getScreenShape();
        return area.contains(mouse);
    }

    public void setHover(ActionListener hover) { this.hover = hover; }
    public void setClick(ActionListener click) { this.click = click; }

    public boolean isHovering() { return hovering; }
}
