package de.craftix.engine.ui;

import de.craftix.engine.render.Screen;
import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

public enum UIAlignment {
    TOP {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width() / 2f, 0);
            result.addSelf(UIAlignment.calculateOffset(trans));
            return result;
        }
    },
    BOTTOM {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width() / 2f, Screen.height());
            result.addSelf(UIAlignment.calculateOffset(trans));
            return result;
        }
    },
    CENTER {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width() / 2f, Screen.height() / 2f);
            result.addSelf(UIAlignment.calculateOffset(trans));
            return result;
        }
    },
    LEFT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(0, Screen.height() / 2f);
            result.addSelf(UIAlignment.calculateOffset(trans));
            return result;
        }
    },
    RIGHT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width(), Screen.height() / 2f);
            result.addSelf(UIAlignment.calculateOffset(trans));
            return result;
        }
    },

    TOP_LEFT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(0);
            result.addSelf(UIAlignment.calculateOffset(trans));
            return result;
        }
    },
    TOP_RIGHT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width(), 0);
            result.addSelf(UIAlignment.calculateOffset(trans));
            return result;
        }
    },
    BOTTOM_LEFT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(0, Screen.height());
            result.addSelf(UIAlignment.calculateOffset(trans));
            return result;
        }
    },
    BOTTOM_RIGHT {
        @Override
        public Vector2 getScreenPosition(Transform trans) {
            Vector2 result = new Vector2(Screen.width(), Screen.height());
            result.addSelf(UIAlignment.calculateOffset(trans));
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
        result.addSelf(new Vector2(transform.position.x, -transform.position.y));
        return result;
    }
}
