package de.craftix.engine.ui.elements;

import de.craftix.engine.GameEngine;
import de.craftix.engine.InputManager;
import de.craftix.engine.render.MShape;
import de.craftix.engine.render.Mesh;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.var.*;
import de.craftix.engine.var.Dimension;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class UISlider extends UIElement {
    private final Dimension sliderDim;
    private final boolean mouseControl;
    private transient ActionListener valueChanged;

    private float minValue = 0;
    private float maxValue = 100;
    private float value = 0;

    public UISlider(boolean mouseControl, Dimension sliderDim, Transform transform, UIAlignment alignment) {
        this.transform = transform;
        this.alignment = alignment;
        this.sliderDim = sliderDim;
        this.mouseControl = mouseControl;
        this.mesh = new Mesh(MShape.RECTANGLE, Color.BLACK);

        GameEngine.addInputs(new Inputs(this));
    }

    @Override
    public void render(Graphics2D g) {
        value = Mathf.boundInRange(value, minValue, maxValue);

        applyTransform(g);
        mesh.render(g, false, transform);

        //Slider
        g.setColor(Color.DARK_GRAY);
        MShape slider = MShape.RECTANGLE;
        Vector2 pos = new Vector2(-(transform.scale.width / 2f), 0);
        pos.x += Mathf.map(value, minValue, maxValue, 0, transform.scale.width);
        g.translate(pos.x, pos.y);
        g.fill(slider.getRender(new Transform(pos, sliderDim, transform.rotation), false));
    }

    public float getValue() { return Mathf.boundInRange(value, minValue, maxValue); }
    public void setValue(float value) { this.value = value; }
    public void setMin(float min) { this.minValue = min; }
    public void setMax(float max) { this.maxValue = max; }

    public void setValueChangedListener(ActionListener actionListener) { this.valueChanged = actionListener; }

    private class Inputs extends Input {
        private final UISlider object;
        private boolean clickedInBounds = false;

        public Inputs(UISlider object) { this.object = object; }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (GameEngine.getScene() != object.getScene()) return;
            if (!mouseControl) return;
            if (!getScreenShape().contains(e.getPoint()) && !clickedInBounds) return;
            float mouse = e.getX();
            float origin = alignment.getScreenPosition(transform).x;
            mouse -= origin;
            value = Mathf.map(mouse, 0, transform.scale.width, minValue, maxValue);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (GameEngine.getScene() != object.getScene()) return;
            if (!mouseControl || !clickedInBounds) return;
            clickedInBounds = false;
            InputManager.setCursor(Cursor.DEFAULT_CURSOR);
            if (valueChanged != null)
                valueChanged.actionPerformed(new ActionEvent(object, 0, "ValueChanged"));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (GameEngine.getScene() != object.getScene()) return;
            if (!mouseControl) return;
            if (!getScreenShape().contains(e.getPoint()) && !clickedInBounds) return;
            if (!clickedInBounds) clickedInBounds = true;
            InputManager.setCursor(Cursor.E_RESIZE_CURSOR);
            float mouse = e.getX();
            float origin = alignment.getScreenPosition(transform).x;
            mouse -= origin;
            value = Mathf.map(mouse, 0, transform.scale.width, minValue, maxValue);
        }
    }
}
