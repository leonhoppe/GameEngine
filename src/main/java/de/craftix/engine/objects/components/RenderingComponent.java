package de.craftix.engine.objects.components;

import java.awt.*;
import java.io.Serializable;

public abstract class RenderingComponent extends Component implements Serializable {
    public abstract void render(Graphics2D g);
}
