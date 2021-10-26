package de.craftix.engine.ui.containers.grid;

import de.craftix.engine.var.Vector2;

import java.io.Serializable;
import java.util.ArrayList;

public class GridDefinition implements Serializable {
    private final ArrayList<Row> rows = new ArrayList<>();
    private final ArrayList<Collum> columns = new ArrayList<>();

    public void addRow(Row row) { rows.add(row); }
    public void addCollum(Collum collum) { columns.add(collum); }

    public Row[] getRows() { return rows.toArray(new Row[0]); }
    public Collum[] getColumns() { return columns.toArray(new Collum[0]); }

    public Vector2 getOrigin(int rowID, int collumID) {
        float x = 0, y = 0;
        if (rowID != 0) {
            for (int row = 0; row < rowID; row++) y += rows.get(row).height;
        }
        if (collumID != 0) {
            for (int collum = 0; collum < collumID; collum++) x += columns.get(collum).width;
        }
        return new Vector2(x, y);
    }

    protected void updateGrid(UIGrid grid) {
        float width = grid.transform.scale.width, height = grid.transform.scale.height;
        ArrayList<Row> fitRows = new ArrayList<>();
        ArrayList<Collum> fitColumns = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            if (row.fit) fitRows.add(row);
            else height -= row.height;
        }
        for (int i = 0; i < columns.size(); i++) {
            Collum collum = columns.get(i);
            if (collum.fit) fitColumns.add(collum);
            else width -= collum.width;
        }
        height /= fitRows.size();
        width /= fitColumns.size();
        for (Row row : fitRows) row.height = height;
        for (Collum collum : fitColumns) collum.width = width;
    }
}
