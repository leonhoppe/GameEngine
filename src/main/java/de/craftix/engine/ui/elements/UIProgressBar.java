package de.craftix.engine.ui.elements;

import de.craftix.engine.render.Mesh;
import de.craftix.engine.render.MShape;
import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.var.Mathf;
import de.craftix.engine.var.Transform;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIProgressBar extends UIElement {

    private Color borderColor = Color.BLACK;
    private Color barColor = Color.GREEN;

    private float min = 0.0f;
    private float max = 1.0f;
    private float value = 0.0f;

    public UIProgressBar(Transform transform, UIAlignment alignment) {
        this.transform = transform;
        this.alignment = alignment;
        this.mesh = new Mesh(MShape.RECTANGLE, barColor);
    }

    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; }
    public void setBarColor(Color barColor) { this.barColor = barColor; }
    public void setRange(float min, float max) { this.min = min; this.max = max; }
    public void setFinishedAnimationListener(ActionListener finishedAnimationListener) { this.finishedAnimationListener = finishedAnimationListener; }
    public void setValue(float value) { this.value = value; }
    public float getValue() { return value; }

    private float animateTo;
    private float startValue;
    private long milliseconds;
    private boolean startAnimation;
    private long startTime;
    private long lastRender;
    private ActionListener finishedAnimationListener;
    public void animateValueTo(float value, long timeInMilliseconds) {
        animateTo = value;
        milliseconds = timeInMilliseconds;
        startTime = System.currentTimeMillis();
        lastRender = startTime;
        startValue = this.value;
        startAnimation = true;
    }

    @Override
    public void render(Graphics2D g) {
        //Animate the Value
        if (startAnimation) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= milliseconds) {
                startAnimation = false;
                value = animateTo;
                if (finishedAnimationListener != null)
                    finishedAnimationListener.actionPerformed(new ActionEvent(this, 0, "animationFinished"));
            }else {
                float difference = ((animateTo - startValue) / milliseconds) * (currentTime - lastRender);
                value += difference;
                lastRender = currentTime;
            }
        }

        value = Math.max(min, value);
        value = Math.min(max, value);

        applyTransform(g);

        //Render ProgressBar
        float mappedValue = Mathf.map(value, min, max, 0, transform.scale.width);
        Transform barTransform = transform.copy();
        float mappedX = barTransform.position.x - (transform.scale.width / 2f + mappedValue / 2f) + mappedValue;
        barTransform.scale.width = mappedValue;
        g.setColor(barColor);
        g.translate(mappedX, 0);
        g.fill(mesh.getMesh(false, barTransform));
        g.translate(-mappedX, 0);

        g.setColor(borderColor);
        g.draw(mesh.getMesh(false, transform));
    }
}
