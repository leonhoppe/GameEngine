package de.craftix.engine.ui;

import de.craftix.engine.GameEngine;
import de.craftix.engine.render.Screen;
import de.craftix.engine.var.Scene;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class UIManager implements Serializable {

    private String html;
    private String css;
    private BufferedImage htmlRender;
    private ArrayList<UIElement> elements = new ArrayList<>();
    private ArrayList<Float> layers = new ArrayList<>();
    private Scene scene;

    public UIManager(Scene scene) { layers.add(-10f); layers.add(0f); layers.add(10f); this.scene = scene; }

    public void addElement(UIElement component) { component.initialise(scene); elements.add(component); }
    public void removeElement(UIElement component) { elements.remove(component); }
    public boolean containsElement(UIElement component) { return elements.contains(component); }
    public void removeElements() { elements.clear(); }
    public ArrayList<UIElement> getElements() { return elements; }
    public UIElement[] getElementTypes(Class<? extends UIElement> elementType) {
        ArrayList<UIElement> typeElements = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            UIElement element = elements.get(i);
            if (element.getClass() == elementType)
                typeElements.add(element);
        }
        return typeElements.toArray(new UIElement[0]);
    }

    public void addLayer(float layer) { layers.add(layer); }
    public void removeLayer(float layer) { layers.remove(layer); }
    public ArrayList<Float> getLayers() { return layers; }
    public void loadHTML(InputStream html, InputStream css) {
        this.html = readStream(html);
        this.css = readStream(css);
        renderHTML();
    }
    private String readStream(InputStream stream) {
        if (stream == null) return "";
        try {
            int bufferSize = 1024;
            char[] buffer = new char[bufferSize];
            StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
            for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
                out.append(buffer, 0, numRead);
            }
            return out.toString();
        }catch (Exception e) { GameEngine.throwError(e); }
        return "";
    }

    private void renderHTML() {
        htmlRender = new BufferedImage(Screen.width() + 10, Screen.height() + 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = htmlRender.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, Screen.antialiasing() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, Screen.antialiasing() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        JEditorPane jep = new JEditorPane("text/html",  "<style>"+css+"</style>" + html);
        jep.setSize(htmlRender.getWidth(), htmlRender.getHeight());

        jep.print(g);
    }

    public void renderComponents(Graphics2D g) {
        if (htmlRender != null) {
            if (Screen.width() + 10 != htmlRender.getWidth() || Screen.height() + 10 != htmlRender.getHeight()) renderHTML();
            g.drawImage(htmlRender, -5, -5, null);
        }
        if (elements.size() == 0) return;
        List<Float> sortedLayers = (List<Float>) layers.clone();
        Collections.sort(sortedLayers);

        Area screen = new Area(new Rectangle(0, 0, Screen.width(), Screen.height()));
        for (Float layer : sortedLayers) {
            for (int i = 0; i < elements.size(); i++) {
                UIElement element = elements.get(i);
                if (!element.isVisible()) continue;
                if (!layer.equals(element.getLayer())) continue;
                Area area = element.getShape();
                area.intersect(screen);
                if (area.isEmpty()) continue;
                AffineTransform orig = g.getTransform();
                element.render(g);
                g.setTransform(orig);
            }
        }
    }

}
