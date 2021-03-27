package de.craftix.engine.ui;

import de.craftix.engine.render.Screen;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

public enum ElementAlignment {
    TOP {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width() / 2f, 0);
            result.addSelf(ElementAlignment.calculateOffset(trans));
            return result;
        }
    },
    BOTTOM {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width() / 2f, Screen.height());
            result.addSelf(ElementAlignment.calculateOffset(trans));
            return result;
        }
    },
    CENTER {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width() / 2f, Screen.height() / 2f);
            result.addSelf(ElementAlignment.calculateOffset(trans));
            return result;
        }
    },
    LEFT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(0, Screen.height() / 2f);
            result.addSelf(ElementAlignment.calculateOffset(trans));
            return result;
        }
    },
    RIGHT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width(), Screen.height() / 2f);
            result.addSelf(ElementAlignment.calculateOffset(trans));
            return result;
        }
    },

    TOP_LEFT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(0);
            result.addSelf(ElementAlignment.calculateOffset(trans));
            return result;
        }
    },
    TOP_RIGHT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width(), 0);
            result.addSelf(ElementAlignment.calculateOffset(trans));
            return result;
        }
    },
    BOTTOM_LEFT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(0, Screen.height());
            result.addSelf(ElementAlignment.calculateOffset(trans));
            return result;
        }
    },
    BOTTOM_RIGHT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width(), Screen.height());
            result.addSelf(ElementAlignment.calculateOffset(trans));
            return result;
        }
    };

    public abstract Vector2 getScreenPosition(Transform trans);

    private static Vector2 calculateOffset(Transform transform) {
        Vector2 result = new Vector2();
        result.subSelf(new Vector2(
                transform.scale.width / 2f,
                transform.scale.height / 2f
        ));
        result.addSelf(new Vector2(
                (float) (transform.position.x * Math.sin(Math.toRadians(90) - transform.rotation.getAngle()) +
                        transform.position.y * Math.cos(Math.toRadians(90) - transform.rotation.getAngle())),
                (float) (transform.position.x * Math.cos(Math.toRadians(90) - transform.rotation.getAngle()) -
                        transform.position.y * Math.sin(Math.toRadians(90) - transform.rotation.getAngle()))
        ));
        return result;
    }
}