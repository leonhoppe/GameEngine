package de.craftix.engine.render;

import java.awt.image.BufferedImage;

public class SpriteMap {

    private final int cols;
    private final int width;
    private final int height;
    private final Sprite sprite;

    public SpriteMap(int cols, Sprite sprite, int width, int height) {
        this.width = width;
        this.height = height;
        this.cols = cols;
        this.sprite = sprite;
    }

    public Sprite getSprite(int id){
        int row = (id / cols);
        int col = (id % cols);
        return getSprite(col, row);
    }

    public Sprite getSprite(int col, int row){
        return new Sprite(sprite.texture.getSubimage(col * width, row * height, width, height));
    }

    public int getCols() { return cols; }

}
