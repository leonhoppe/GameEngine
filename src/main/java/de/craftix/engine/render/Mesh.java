package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.var.Mathf;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;

public class Mesh implements Serializable {
    public Vector2[] points;
    public Shape shape;
    public Vector2[] UVs;
    public Sprite texture;
    public Color[] colors;

    public Mesh(Vector2[] points, Color[] meshColors) {
        if (points.length % 3 != 0)
            throw new IllegalArgumentException("points not convertible to Triangles");
        this.points = points;
        this.shape = null;
        this.UVs = null;
        this.texture = null;
        this.colors = meshColors;
    }
    public Mesh(Vector2[] points, Color meshColor) {
        if (points.length % 3 != 0)
            throw new IllegalArgumentException("points not convertible to Triangles");
        this.points = points;
        this.shape = null;
        this.UVs = null;
        this.texture = null;
        this.colors = new Color[points.length / 3];
        Arrays.fill(colors, meshColor);
    }

    public Mesh(Vector2[] points, Vector2[] UVs, Sprite texture) {
        if (points.length % 3 != 0)
            throw new IllegalArgumentException("points not convertible to Triangles");
        this.points = points;
        this.shape = null;
        this.UVs = UVs;
        this.texture = texture;
        this.colors = null;
    }

    public Mesh(Shape shape, Color color) {
        this.points = null;
        this.shape = shape;
        this.UVs = null;
        this.texture = null;
        this.colors = new Color[] { color };
    }

    public void render(Graphics2D g, boolean useCamScale, Transform transform) {
        if (shape != null) {
            g.setColor(colors[0]);
            g.fill(shape.getRender(transform, useCamScale));
            return;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Polygon2D[] tris = getTriangleShapes(useCamScale);
        if (UVs == null && colors != null) {
            for (int i = 0; i < tris.length; i++) {
                g.setColor(colors[i]);
                g.fill(new Area(tris[i]));
            }
        }
        //TODO: Add UV rendering
        Vector2[][] UVPoints = getUVTriangles();
        Vector2[][] trisPoints = getTriangles(useCamScale);
        int w = texture.texture.getWidth();
        int h = texture.texture.getHeight();
        for (int i = 0; i < tris.length; i++) {
            Vector2 p1 = UVPoints[i][0].mul(new Vector2(w, h));
            Vector2 p2 = UVPoints[i][1].mul(new Vector2(w, h));
            Vector2 p3 = UVPoints[i][2].mul(new Vector2(w, h));
            Polygon2D poly = new Polygon2D(new float[] { p1.x, p2.x, p3.x }, new float[] { p1.y, p2.y, p3.y }, 3);
            g.drawImage(getCutout(poly, trisPoints[i]), 0, 0, null);
        }
        if (Screen.antialiasing())
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public Area getMesh(boolean useCamScale, Transform transform) {
        if (shape != null)
            return shape.getRender(transform, useCamScale);

        Area mesh = new Area();
        for (Vector2[] triangle : getTriangles(useCamScale)) {
            Vector2 p1 = triangle[0].add(transform != null ? transform.position : new Vector2());
            Vector2 p2 = triangle[1].add(transform != null ? transform.position : new Vector2());
            Vector2 p3 = triangle[2].add(transform != null ? transform.position : new Vector2());
            Polygon2D polygon = new Polygon2D(
                    new float[] { p1.x, p2.x, p3.x },
                    new float[] { p1.y, p2.y, p3.y },
                    3
            );
            mesh.add(new Area(polygon));
        }
        return mesh;
    }
    public Vector2[][] getTriangles(boolean useCamScale) {
        assert points != null;
        Vector2[][] triangles = new Vector2[points.length / 3][3];
        for (int i = 0; i < points.length; i += 3) {
            if (useCamScale) {
                float camScale = GameEngine.getCamera().getScale();
                Vector2 p1 = new Vector2(points[i].x * camScale,
                        -points[i].y * camScale);
                Vector2 p2 = new Vector2(points[i + 1].x * camScale,
                        -points[i + 1].y * camScale);
                Vector2 p3 = new Vector2(points[i + 2].x * camScale,
                        -points[i + 2].y * camScale);
                triangles[i / 3][0] = p1;
                triangles[i / 3][1] = p2;
                triangles[i / 3][2] = p3;
            }else {
                Vector2 p1 = new Vector2(points[i].x, -points[i].y);
                Vector2 p2 = new Vector2(points[i + 1].x, -points[i + 1].y);
                Vector2 p3 = new Vector2(points[i + 2].x, -points[i + 2].y);
                triangles[i / 3][0] = p1;
                triangles[i / 3][1] = p2;
                triangles[i / 3][2] = p3;
            }
        }
        return triangles;
    }
    public Polygon2D[] getTriangleShapes(boolean useCamScale) {
        Vector2[][] vectors = getTriangles(useCamScale);
        Polygon2D[] triangles = new Polygon2D[vectors.length];
        int index = 0;
        for (Vector2[] tris : vectors) {
            Vector2 p1 = tris[0];
            Vector2 p2 = tris[1];
            Vector2 p3 = tris[2];
            triangles[index] = new Polygon2D(new float[] { p1.x, p2.x, p3.x }, new float[] { p1.y, p2.y, p3.y }, 3);
            index++;
        }
        return triangles;
    }
    public Vector2[][] getUVTriangles() {
        Vector2[][] triangles = new Vector2[UVs.length / 3][3];
        for (int i = 0; i < UVs.length; i += 3) {
            Vector2 p1 = Mathf.mapVector(UVs[i], 0, 1, -1, 1);
            Vector2 p2 = Mathf.mapVector(UVs[i + 1], 0, 1, -1, 1);
            Vector2 p3 = Mathf.mapVector(UVs[i + 2], 0, 1, -1, 1);

            triangles[i / 3][0] = Mathf.normalizeVector(new Vector2(p1.x, -p1.y), -1, 1);
            triangles[i / 3][1] = Mathf.normalizeVector(new Vector2(p2.x, -p2.y), -1, 1);
            triangles[i / 3][2] = Mathf.normalizeVector(new Vector2(p3.x, -p3.y), -1, 1);
        }
        return triangles;
    }

    private BufferedImage getCutout(Polygon2D poly, Vector2[] tris) {
        //TODO: Scale the Mesh based on the Vertices
        BufferedImage out = new BufferedImage(texture.texture.getWidth(), texture.texture.getHeight(), texture.texture.getType());
        Graphics g = out.getGraphics();
        g.setClip(poly);
        g.drawImage(texture.texture, 0, 0, null);
        g.dispose();
        return out;
    }
}
