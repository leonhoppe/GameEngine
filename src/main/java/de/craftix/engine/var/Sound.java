package de.craftix.engine.var;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.io.Serializable;

public class Sound implements Serializable {
    private AudioInputStream stream;
    private Clip clip;

    public Sound(File audioFile) {
        try {
            stream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(stream);
        }catch (Exception e) { e.printStackTrace(); }
    }

    public void setProperty(FloatControl.Type property, float value) {
        if (!clip.isControlSupported(property))
            throw new IllegalArgumentException("Property not supported");
        FloatControl control = (FloatControl) clip.getControl(property);
        control.setValue(value);
    }
    public float getProperty(FloatControl.Type property) {
        if (!clip.isControlSupported(property))
            throw new IllegalArgumentException("Property not supported");
        FloatControl control = (FloatControl) clip.getControl(property);
        return control.getValue();
    }

    public void start() { clip.start(); }
    public void stop() { clip.stop(); }

    public Clip getClip() { return clip; }
    public AudioInputStream getStream() { return stream; }
}
