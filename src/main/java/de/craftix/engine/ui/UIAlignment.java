package de.craftix.engine.ui;

import de.craftix.engine.var.Transform;
import de.craftix.engine.var.Vector2;

public enum UIAlignment {
    TOP {
        @Override
        public Vector2 getScreenPosition(Transform trans, Transform container) {
            Vector2 result = new Vector2(container.scale.width / 2f, 0);
            result.add(UIAlignment.calculateOffset(trans, new Vector2(trans.scale.width / 2f, 0)));
            result.add(container.position);
            return result;
        }
    },
    BOTTOM {
        @Override
        public Vector2 getScreenPosition(Transform trans, Transform container) {
            Vector2 result = new Vector2(container.scale.width / 2f, container.scale.height);
            result.add(UIAlignment.calculateOffset(trans, new Vector2(trans.scale.width / 2f, trans.scale.height)));
            result.add(container.position);
            return result;
        }
    },
    CENTER {
        @Override
        public Vector2 getScreenPosition(Transform trans, Transform container) {
            Vector2 result = new Vector2(container.scale.width / 2f, container.scale.height / 2f);
            result.add(UIAlignment.calculateOffset(trans, new Vector2(trans.scale.width / 2f, trans.scale.height / 2f)));
            result.add(container.position);
            return result;
        }
    },
    LEFT {
        @Override
        public Vector2 getScreenPosition(Transform trans, Transform container) {
            Vector2 result = new Vector2(0, container.scale.height / 2f);
            result.add(UIAlignment.calculateOffset(trans, new Vector2(0, trans.scale.height / 2f)));
            result.add(container.position);
            return result;
        }
    },
    RIGHT {
        @Override
        public Vector2 getScreenPosition(Transform trans, Transform container) {
            Vector2 result = new Vector2(container.scale.width, container.scale.height / 2f);
            result.add(UIAlignment.calculateOffset(trans, new Vector2(trans.scale.width, trans.scale.height / 2f)));
            result.add(container.position);
            return result;
        }
    },

    TOP_LEFT {
        @Override
        public Vector2 getScreenPosition(Transform trans, Transform container) {
            Vector2 result = new Vector2(0);
            result.add(UIAlignment.calculateOffset(trans, new Vector2()));
            result.add(container.position);
            return result;
        }
    },
    TOP_RIGHT {
        @Override
        public Vector2 getScreenPosition(Transform trans, Transform container) {
            Vector2 result = new Vector2(container.scale.width, 0);
            result.add(UIAlignment.calculateOffset(trans, new Vector2(trans.scale.width, 0)));
            result.add(container.position);
            return result;
        }
    },
    BOTTOM_LEFT {
        @Override
        public Vector2 getScreenPosition(Transform trans, Transform container) {
            Vector2 result = new Vector2(0, container.scale.height);
            result.add(UIAlignment.calculateOffset(trans, new Vector2(0, trans.scale.height)));
            result.add(container.position);
            return result;
        }
    },
    BOTTOM_RIGHT {
        @Override
        public Vector2 getScreenPosition(Transform trans, Transform container) {
            Vector2 result = new Vector2(container.scale.width, container.scale.height);
            result.add(UIAlignment.calculateOffset(trans, new Vector2(trans.scale.width, trans.scale.height)));
            result.add(container.position);
            return result;
        }
    };

    public abstract Vector2 getScreenPosition(Transform trans, Transform container);
    private static Vector2 calculateOffset(Transform transform, Vector2 origin) {
        Vector2 result = new Vector2();
        result.sub(origin);
        result.add(new Vector2(transform.position.x, -transform.position.y));
        return result;
    }
}
