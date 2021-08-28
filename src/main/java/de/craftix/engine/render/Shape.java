package de.craftix.engine.render;

import de.craftix.engine.GameEngine;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public enum Shape implements Serializable {
    TRIANGLE{
        @Override
        public Area getRender(Transform transform, boolean useCamScale) {
            if (!useCamScale) {
                Vector2 top = new Vector2(0, -transform.scale.height / 2f);
                Vector2 right = new Vector2(-transform.scale.width / 2f, transform.scale.height / 2f);
                Vector2 left = new Vector2(transform.scale.width / 2f, transform.scale.height / 2);
                return new Area(new Polygon2D(new float[]{top.x, right.x, left.x},
                        new float[]{top.y, right.y, left.y},
                        3)
                );
            }else {
                float camScale = GameEngine.getCamera().getScale();
                Vector2 top = new Vector2(0, -transform.scale.height * camScale / 2f);
                Vector2 right = new Vector2(-transform.scale.width * camScale / 2f, transform.scale.height * camScale / 2f);
                Vector2 left = new Vector2(transform.scale.width * camScale / 2f, transform.scale.height * camScale / 2);
                return new Area(new Polygon2D(new float[]{top.x, right.x, left.x},
                        new float[]{top.y, right.y, left.y},
                        3)
                );
            }
        }
    },
    RECTANGLE{
        @Override
        public Area getRender(Transform transform, boolean useCamScale) {
            if (!useCamScale) {
                return new Area(new Rectangle2D.Float(-transform.scale.width / 2f, -transform.scale.height / 2f,
                        transform.scale.width, transform.scale.height));
            }else {
                float camScale = GameEngine.getCamera().getScale();
                return new Area(new Rectangle2D.Float(
                        -transform.scale.width * camScale / 2f,
                        -transform.scale.height * camScale / 2f,
                        transform.scale.width * camScale,
                        transform.scale.height * camScale)
                );
            }
        }
    },
    CIRCLE{
        @Override
        public Area getRender(Transform transform, boolean useCamScale) {
            if (!useCamScale) {
                return new Area(new Ellipse2D.Float(-transform.scale.width / 2f, -transform.scale.height / 2f,
                        transform.scale.width, transform.scale.height));
            }else {
                float camScale = GameEngine.getCamera().getScale();
                return new Area(new Ellipse2D.Float(
                        -transform.scale.width * camScale / 2f,
                        -transform.scale.height * camScale / 2f,
                        transform.scale.width * camScale,
                        transform.scale.height * camScale)
                );
            }
        }
    };

    Shape() {}
    public abstract Area getRender(Transform transform, boolean useCamScale);
}
