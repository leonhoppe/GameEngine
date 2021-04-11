package de.craftix.engine.render;

import java.awt.*;
import java.io.Serializable;

public class SerialisedSprite extends Sprite implements Serializable {
    public String filePath;
    public boolean isInternal;

    public SerialisedSprite(Color color, Shape shape) { this.color = color; this.shape = shape; }
    public SerialisedSprite(String path, boolean repeat, boolean isInternal) { this.filePath = path; this.isInternal = isInternal; this.repeat = repeat; }
    public SerialisedSprite() {}

    public void createSprite() {
        if (isInternal)
            texture = Sprite.load(filePath).texture;
        else
            texture = Sprite.loadExt(filePath).texture;
    }
}
