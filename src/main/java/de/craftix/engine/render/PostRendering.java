package de.craftix.engine.render;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface PostRendering {
    Color renderPixel(Color pixel, int x, int y, BufferedImage frame);
}
