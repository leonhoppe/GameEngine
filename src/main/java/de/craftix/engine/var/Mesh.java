package de.craftix.engine.var;

import de.craftix.engine.GameEngine;
import de.craftix.engine.render.Shape;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Mesh {
    public final Vector2[] points;
    public final Shape shape;
    public final Transform transform;

    public Mesh(Vector2[] points) {
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

    public Area getMesh() {
        if (shape != null) {
            java.awt.Shape dimensions = null;
            switch (shape) {
                case CIRCLE:
                    dimensions = new Ellipse2D.Float(-(transform.scale.width * GameEngine.getCamera().getScale()) / 2f, -(transform.scale.height * GameEngine.getCamera().getScale()) / 2f,
                            transform.scale.width * GameEngine.getCamera().getScale(), transform.scale.height * GameEngine.getCamera().getScale());
                    break;
                case RECTANGLE:
                    dimensions = new Rectangle((int) (-(transform.scale.width * GameEngine.getCamera().getScale()) / 2f), (int) (-(transform.scale.height * GameEngine.getCamera().getScale()) / 2f),
                            (int) (transform.scale.width * GameEngine.getCamera().getScale()), (int) (transform.scale.height * GameEngine.getCamera().getScale()));
                    break;
                case TRIANGLE:
                    Point top = new Point(0, (int) (-(transform.scale.height * GameEngine.getCamera().getScale()) / 2f));
                    Point right = new Point((int) (-(transform.scale.width * GameEngine.getCamera().getScale()) / 2f),
                            (int) ((transform.scale.height * GameEngine.getCamera().getScale()) / 2f));
                    Point left = new Point((int) ((transform.scale.width * GameEngine.getCamera().getScale()) / 2f),
                            (int) ((transform.scale.height * GameEngine.getCamera().getScale()) / 2));
                    dimensions = new Polygon(new int[]{top.x, right.x, left.x},
                            new int[]{top.y, right.y, left.y},
                            3);
                    break;
            }
            return new Area(dimensions);
        }

        Area mesh = new Area();
        for (int i = 0; i < points.length; i += 3) {
            Point p1 = new Point((int) (points[i].x * GameEngine.getCamera().getScale()),
                                 (int) (-points[i].y * GameEngine.getCamera().getScale()));
            Point p2 = new Point((int) (points[i + 1].x * GameEngine.getCamera().getScale()),
                                 (int) (-points[i + 1].y * GameEngine.getCamera().getScale()));
            Point p3 = new Point((int) (points[i + 2].x * GameEngine.getCamera().getScale()),
                                 (int) (-points[i + 2].y * GameEngine.getCamera().getScale()));
            Polygon polygon = new Polygon(new int[] {p1.x, p2.x, p3.x},
                                          new int[] {p1.y, p2.y, p3.y},
                                          3);
            mesh.add(new Area(polygon));
        }
        return mesh;
    }
}
