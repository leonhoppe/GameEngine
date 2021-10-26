package de.craftix.engine.ui.containers.grid;

import de.craftix.engine.ui.UIAlignment;
import de.craftix.engine.ui.UIContainer;
import de.craftix.engine.ui.UIElement;
import de.craftix.engine.var.Dimension;
import de.craftix.engine.var.Transform;

import java.util.HashMap;

public class UIGrid extends UIContainer {
    public GridDefinition gridDefinition = new GridDefinition();
    protected HashMap<UIElement, String> elements = new HashMap<>();

    public UIGrid(Transform transform, UIAlignment alignment) {
        super(transform, alignment);
    }

    @Override
    public Transform getTransform(UIElement element) {
        if (!elements.containsKey(element)) {
            Transform trans = transform.copy();
            trans.position = alignment.getScreenPosition(transform, getContainer());
            return trans;
        }
        gridDefinition.updateGrid(this);
        String sq = elements.get(element);
        int rowID = Integer.parseInt(sq.split(":")[0]);
        int collumID = Integer.parseInt(sq.split(":")[1]);
        Row row = gridDefinition.getRows()[rowID];
        Collum collum = gridDefinition.getColumns()[collumID];
        Transform trans = new Transform(transform.rotation);
        trans.position = gridDefinition.getOrigin(rowID, collumID).add(transform.position);
        trans.scale = new Dimension(collum.width, row.height);
        return trans;
    }

    public void setElementDefinition(UIElement element, int row, int collum) { elements.remove(element); elements.put(element, row + ":" + collum); }
}
