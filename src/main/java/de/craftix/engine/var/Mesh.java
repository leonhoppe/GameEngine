package de.craftix.engine.var;

import de.craftix.engine.GameEngine;
import de.craftix.engine.render.Shape;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class Mesh implements Serializable {
    public final Vector2[] points;
    public final Shape shape;
    public final Transform transform;

    public Mesh(Vector2... points) {
        if (points.length % 3 != 0)
            throw new IllegalArgumentException("points not convertible to Polygons");
        this.points = points;
        this.shape = null;
        this.transform = null;
    }

    public Mesh(Shape shape, Transform transform) {
        this.points = null;
        this.shape = shape;
        this.transform = transform;
    }

    public Area getMesh(boolean useCamScale) {
        if (shape != null)
            return shape.getRender(transform, useCamScale);

        Area mesh = new Area();
        for (Vector2[] triangle : getTriangles(useCamScale)) {
            Vector2 p1 = triangle[0];
            Vector2 p2 = triangle[1];
            Vector2 p3 = triangle[2];
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
}
