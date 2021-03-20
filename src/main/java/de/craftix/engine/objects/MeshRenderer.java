package de.craftix.engine.objects;

import de.craftix.engine.render.Screen;
import de.craftix.engine.var.Mesh;

import java.awt.*;

public class MeshRenderer extends RenderingComponent {
    public Mesh mesh;
    public Color color;

    public MeshRenderer(Mesh mesh, Color color) { this.mesh = mesh; this.color = color; }

    @Override
    public void render(Graphics2D g) {
        g.setColor(color);
        g.fill(Screen.getTransform(object.transform).createTransformedShape(mesh.getMesh()));
    }
}
