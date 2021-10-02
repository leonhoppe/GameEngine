package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;

public class Mesh implements Serializable {
    public Vector2[] points;
    public MShape mShape;
    public Vector2[] UVs;
    public transient Sprite texture;
    public Color[] colors;

    public Mesh(Vector2[] points, Color[] meshColors) {
        if (points.length % 3 != 0)
            throw new IllegalArgumentException("points not convertible to Triangles");
        this.points = points;
        this.mShape = null;
        this.UVs = null;
        this.texture = null;
        this.colors = meshColors;
    }
    public Mesh(Vector2[] points, Color meshColor) {
        if (points.length % 3 != 0)
            throw new IllegalArgumentException("points not convertible to Triangles");
        this.points = points;
        this.mShape = null;
        this.UVs = null;
        this.texture = null;
        this.colors = new Color[points.length / 3];
        Arrays.fill(colors, meshColor);
    }

    public Mesh(Vector2[] points, Vector2[] UVs, Sprite texture) {
        if (points.length % 3 != 0)
            throw new IllegalArgumentException("points not convertible to Triangles");
        this.points = points;
        this.mShape = null;
        this.UVs = UVs;
        this.texture = texture;
        this.colors = null;
    }

    public Mesh(MShape mShape, Color color) {
        this.points = null;
        this.mShape = mShape;
        this.UVs = null;
        this.texture = null;
        this.colors = new Color[] { color };
    }

    public void render(Graphics2D g, boolean useCamScale, Transform transform) {
        if (mShape != null) {
            g.setColor(colors[0]);
            g.fill(mShape.getRender(transform, useCamScale));
            g.setColor(Color.BLACK);
            return;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Polygon2D[] tris = getTriangleShapes(useCamScale);
        if (UVs == null && colors != null) {
            for (int i = 0; i < tris.length; i++) {
                g.setColor(colors[i]);
                g.fill(new Area(tris[i]));
            }
        }else {
            Vector2[][] trisPoints = getTriangles(useCamScale);
            Vector2[][] uvTris = getUVTriangles();
            for (int i = 0; i < tris.length; i++) {
                Vector2 aUV = uvTris[i][0];
                Vector2 bUV = uvTris[i][1];
                Vector2 cUV = uvTris[i][2];
                Vector2 a = trisPoints[i][0];
                Vector2 b = trisPoints[i][1];
                Vector2 c = trisPoints[i][2];
                Rectangle area = new Area(tris[i]).getBounds();
                Rectangle2D uvArea = new Area(new Polygon2D(new float[] {aUV.x, bUV.x, cUV.x}, new float[] {aUV.y, bUV.y, cUV.y}, 3)).getBounds2D();
                Point bounds = getTextureBounds(area, uvArea);
                BufferedImage out = new BufferedImage(bounds.x, bounds.y, BufferedImage.TYPE_INT_ARGB);
                BufferedImage currTexture = texture.resize(bounds.x, bounds.y, Resizer.AVERAGE).texture;
                int w = currTexture.getWidth();
                int h = currTexture.getHeight();

                for (int x = area.x; x < area.x + area.width; x++) {
                    for (int y = area.y; y < area.y + area.height; y++) {
                        if (!new Area(tris[i]).contains(new Point(x, y))) continue;
                        Vector2 p = new Vector2(x, y);
                        Vector2 barryA = new Vector2(((b.y - c.y) * (p.x - c.x) + (c.x - b.x) * (p.y - c.y)) / ((b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y)));
                        Vector2 barryB = new Vector2(((c.y - a.y) * (p.x - c.x) + (a.x - c.x) * (p.y - c.y)) / ((b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y)));
                        Vector2 barryC = new Vector2(1 - barryA.x - barryB.x, 1 - barryA.y - barryB.y);

                        Point uv = new Vector2(
                                barryA.x * aUV.x + barryB.x * bUV.x + barryC.x * cUV.x,
                                barryA.y * aUV.y + barryB.y * bUV.y + barryC.y * cUV.y
                        ).mul(new Vector2(w, h)).toPoint();

                        int rgb = currTexture.getRGB(uv.x, uv.y);
                        out.setRGB(uv.x, uv.y, rgb);
                    }
                }
                g.drawImage(out, area.x, area.y, null);
            }
        }

        if (Screen.antialiasing())
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.BLACK);
    }

    private Point getTextureBounds(Rectangle area, Rectangle2D uvArea) {
        float overhangX = (float) (1 - uvArea.getWidth());
        float overhangY = (float) (1 - uvArea.getHeight());

        int x = Math.round((area.width * 2) * overhangX) + area.width;
        int y = Math.round((area.height * 2) * overhangY) + area.height;

        return new Point(x, y);
    }

    public Area getMesh(boolean useCamScale, Transform transform) {
        if (mShape != null)
            return mShape.getRender(transform, useCamScale);

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
            Vector2 p1 = Vector2.map(UVs[i], 0, 1, -1, 1);
            Vector2 p2 = Vector2.map(UVs[i + 1], 0, 1, -1, 1);
            Vector2 p3 = Vector2.map(UVs[i + 2], 0, 1, -1, 1);

            triangles[i / 3][0] = Vector2.normalize(new Vector2(p1.x, -p1.y), -1, 1);
            triangles[i / 3][1] = Vector2.normalize(new Vector2(p2.x, -p2.y), -1, 1);
            triangles[i / 3][2] = Vector2.normalize(new Vector2(p3.x, -p3.y), -1, 1);
        }
        return triangles;
    }

    public void setColors(Color... colors) { this.colors = colors; }
}
