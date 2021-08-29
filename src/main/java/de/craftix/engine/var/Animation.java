package de.craftix.engine.var;

import de.craftix.engine.render.ScreenObject;
import de.craftix.engine.render.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {

    private ScreenObject object;
    private final String name;
    private final Keyframe<Vector2>[] posKeyframes;
    private final Keyframe<Dimension>[] scaleKeyframes;
    private final Keyframe<Quaternion>[] rotKeyframes;
    private final Keyframe<Sprite>[] spriteKeyframes;
    private final boolean autoStart;
    private final boolean loop;
    private boolean started = false;
    private long animationStart;

    @SafeVarargs
    public Animation(String name, boolean loop, boolean autoStart, Keyframe<? extends Animatable>... keyframes) {
        ArrayList<Keyframe<Vector2>> vectors = new ArrayList<>();
        ArrayList<Keyframe<Dimension>> dimensions = new ArrayList<>();
        ArrayList<Keyframe<Quaternion>> quaternions = new ArrayList<>();
        ArrayList<Keyframe<Sprite>> sprites = new ArrayList<>();
        for (Keyframe<? extends Animatable> keyframe : keyframes) {
            if (keyframe.value instanceof Vector2)
                vectors.add(new Keyframe<>((Vector2) keyframe.value, keyframe.startPoint, keyframe.timeInMillis));
            if (keyframe.value instanceof Dimension)
                dimensions.add(new Keyframe<>((Dimension) keyframe.value, keyframe.startPoint, keyframe.timeInMillis));
            if (keyframe.value instanceof Quaternion)
                quaternions.add(new Keyframe<>((Quaternion) keyframe.value, keyframe.startPoint, keyframe.timeInMillis));
            if (keyframe.value instanceof Transform) {
                vectors.add(new Keyframe<>(((Transform) keyframe.value).position, keyframe.startPoint, keyframe.timeInMillis));
                dimensions.add(new Keyframe<>(((Transform) keyframe.value).scale, keyframe.startPoint, keyframe.timeInMillis));
                quaternions.add(new Keyframe<>(((Transform) keyframe.value).rotation, keyframe.startPoint, keyframe.timeInMillis));
            }
            if (keyframe.value instanceof Sprite)
                sprites.add(new Keyframe<>((Sprite) keyframe.value, keyframe.startPoint, keyframe.timeInMillis));
        }
        posKeyframes = vectors.toArray(new Keyframe[0]);
        scaleKeyframes = dimensions.toArray(new Keyframe[0]);
        rotKeyframes = quaternions.toArray(new Keyframe[0]);
        spriteKeyframes = sprites.toArray(new Keyframe[0]);

        this.autoStart = autoStart;
        this.loop = loop;
        this.name = name;
    }

    public void initialise(ScreenObject object) {
        this.object = object;
        if (autoStart) start();
    }

    public void start() {
        long currentTime = System.currentTimeMillis();
        currentTimeInAnimation = 0;

        animationStart = currentTime;
        rot_lastUpdate = animationStart;
        pos_lastUpdate = animationStart;
        scale_lastUpdate = animationStart;
        sprite_lastUpdate = animationStart;

        rot_original = object.transform.rotation.copy();
        pos_original = object.transform.position.copy();
        scale_original = object.transform.scale.copy();
        sprite_original = object.getSprite().copy();

        rot_currentFrame = 0;
        pos_currentFrame = 0;
        scale_currentFrame = 0;
        sprite_currentFrame = 0;

        rot_running = true;
        pos_running = true;
        scale_running = true;
        sprite_running = true;

        if (rotKeyframes.length == 0)
            rot_running = false;
        if (posKeyframes.length == 0)
            pos_running = false;
        if (scaleKeyframes.length == 0)
            scale_running = false;
        if (spriteKeyframes.length == 0)
            sprite_running = false;

        started = true;
    }
    public void stop() {
        rot_running = false;
        pos_running = false;
        scale_running = false;
        sprite_running = false;

        started = false;
    }
    public boolean isRunning() { return rot_running || pos_running || scale_running || sprite_running; }

    long currentTimeInAnimation;

    public void update() {
        if (loop && started && !isRunning()) start();
        currentTimeInAnimation = System.currentTimeMillis() - animationStart;
        applyRotation();
        applyPosition();
        applyScale();
        applySprite();
    }

    private boolean rot_running = false;
    private int rot_currentFrame;
    private long rot_lastUpdate;
    private Quaternion rot_original;
    private void applyRotation() {
        if (!rot_running) return;
        Keyframe<Quaternion> frame = rotKeyframes[rot_currentFrame];
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - rot_lastUpdate;
        rot_lastUpdate = currentTime;
        if (currentTimeInAnimation - frame.startPoint >= frame.timeInMillis) {
            object.transform.rotation = frame.value.copy();
            rot_currentFrame++;
            rot_original = object.transform.rotation.copy();
            if (rot_currentFrame >= rotKeyframes.length)
                rot_running = false;
            return;
        }
        if (currentTimeInAnimation < frame.startPoint) return;
        double difference = frame.value.getAngle() - rot_original.getAngle();
        difference /= frame.timeInMillis;
        object.transform.rotate(Math.toDegrees(difference * deltaTime));
    }

    private boolean pos_running = false;
    private int pos_currentFrame;
    private long pos_lastUpdate;
    private Vector2 pos_original;
    private void applyPosition() {
        if (!pos_running) return;
        Keyframe<Vector2> frame = posKeyframes[pos_currentFrame];
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - pos_lastUpdate;
        pos_lastUpdate = currentTime;
        if (currentTimeInAnimation - frame.startPoint >= frame.timeInMillis) {
            object.transform.position = frame.value.copy();
            pos_currentFrame++;
            pos_original = object.transform.position.copy();
            if (pos_currentFrame >= posKeyframes.length)
                pos_running = false;
            return;
        }
        if (currentTimeInAnimation < frame.startPoint) return;
        Vector2 motion = new Vector2(
                frame.value.x - pos_original.x,
                frame.value.y - pos_original.y
        );
        motion.div(frame.timeInMillis);
        object.transform.position.add(motion.mul(deltaTime));
    }

    private boolean scale_running = false;
    private int scale_currentFrame;
    private long scale_lastUpdate;
    private Dimension scale_original;
    private void applyScale() {
        if (!scale_running) return;
        Keyframe<Dimension> frame = scaleKeyframes[scale_currentFrame];
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - scale_lastUpdate;
        scale_lastUpdate = currentTime;
        if (currentTimeInAnimation - frame.startPoint >= frame.timeInMillis) {
            object.transform.scale = frame.value.copy();
            scale_currentFrame++;
            scale_original = object.transform.scale.copy();
            if (scale_currentFrame >= scaleKeyframes.length)
                scale_running = false;
            return;
        }
        if (currentTimeInAnimation < frame.startPoint) return;
        Dimension scale = new Dimension(
                frame.value.width - scale_original.width,
                frame.value.height - scale_original.height
        );
        scale.width /= frame.timeInMillis;
        scale.height /= frame.timeInMillis;
        object.transform.scale.width += scale.width * deltaTime;
        object.transform.scale.height += scale.height * deltaTime;
    }

    private boolean sprite_running = false;
    private int sprite_currentFrame;
    private long sprite_lastUpdate;
    private Sprite sprite_original;
    private void applySprite() {
        if (!sprite_running) return;
        Keyframe<Sprite> frame = spriteKeyframes[sprite_currentFrame];
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - sprite_lastUpdate;
        sprite_lastUpdate = currentTime;
        if (currentTimeInAnimation - frame.startPoint >= frame.timeInMillis) {
            object.getSprite().texture = frame.value.texture;
            sprite_currentFrame++;
            sprite_original = object.getSprite().copy();
            if (sprite_currentFrame >= spriteKeyframes.length)
                sprite_running = false;
            return;
        }
        if (currentTimeInAnimation < frame.startPoint) return;
        BufferedImage result = new BufferedImage(object.getSprite().texture.getWidth(), object.getSprite().texture.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage bottom = new BufferedImage(object.getSprite().texture.getWidth(), object.getSprite().texture.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage top = new BufferedImage(object.getSprite().texture.getWidth(), object.getSprite().texture.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = result.getGraphics();
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                Color pixel = new Color(sprite_original.texture.getRGB(x, y), true);
                int alpha = -Math.round(Mathf.map(currentTimeInAnimation - frame.startPoint, 0, frame.timeInMillis, 0, pixel.getAlpha())) + 255;
                if (pixel.getAlpha() != 0) {
                    top.setRGB(x, y, new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), alpha).getRGB());
                    bottom.setRGB(x, y, frame.value.texture.getRGB(x, y));
                }else {
                    top.setRGB(x, y, pixel.getRGB());
                    pixel = new Color(frame.value.texture.getRGB(x, y), true);
                    alpha = Math.round(Mathf.map(currentTimeInAnimation - frame.startPoint, 0, frame.timeInMillis, 0, pixel.getAlpha()));
                    if (pixel.getAlpha() != 0)
                        bottom.setRGB(x, y, new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), alpha).getRGB());
                    else
                        bottom.setRGB(x, y, pixel.getRGB());
                }
            }
        }
        g.drawImage(bottom, 0, 0, null);
        g.drawImage(top, 0, 0, null);
        g.dispose();
        object.getSprite().texture = result;
    }

    public String getName() { return name; }
}
