package de.craftix.engine.var;

import de.craftix.engine.render.Sprite;
import de.craftix.engine.render.SpriteMap;

public class Animation {

    private int state;
    private int frame = 0;
    private int frames;
    private final long delay;
    private long startTime;
    private final SpriteMap sprite;
    private boolean started;

    public Animation(SpriteMap sprite, int state, int frames, long delay){
        this.sprite = sprite;
        this.delay = delay;
        start(state, frames);
    }

    public Animation(SpriteMap sprite, long delay) {
        this.sprite = sprite;
        this.delay = delay;
        started = false;
    }

    public void update() {
        if (started && System.currentTimeMillis() - startTime >= delay) {
            frame++;
            if (frame == frames) frame = 0;
            startTime = System.currentTimeMillis();
        }
    }

    public void start(int state, int frames) {
        this.frames = frames;
        this.state = state;
        startTime = System.currentTimeMillis();
        frame = 0;
        started = true;
    }

    public void stop() { started = false; }

    public Sprite getImage() { return sprite.getSprite(frame, state); }
    public int getState() { return state; }
    public void setImages(int state, int frames) {
        this.state = state;
        this.frames = frames;
        frame = 0;
        startTime = System.currentTimeMillis();
    }

    public boolean isRunning() { return started; }

}
