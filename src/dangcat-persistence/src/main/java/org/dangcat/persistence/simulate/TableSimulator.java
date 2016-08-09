package org.dangcat.persistence.simulate;

import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Rows;
import org.dangcat.persistence.model.Table;

/**
 * Table数据模拟器。
 *
 * @author dangcat
 */
public class TableSimulator extends DataSimulator {
    private Table table = null;

    public TableSimulator(Table table) {
        this.table = table;
    }

    public void create(Table table, int size) {
        this.setSize(size);
        Rows rows = table.getRows();
        for (int index = 0; index < size; index++) {
            Row row = rows.createNewRow();
            this.modify(row, index);
            rows.add(row);
        }
    }

    @Override
    public Table getTable() {
        return this.table;
    }

    @Override
    public void initialize() {
        for (Column column : this.table.getColumns())
            this.addValueSimulator(column);
    }

    public void modify(Row row, int index) {
        if (row != null) {
            for (Column column : this.table.getColumns()) {
                if (column.isAutoIncrement())
                    continue;

                Object value = this.getValue(index, column.getName());
                row.setValue(column.getName(), value);
            }
        }
    }

    public void modify(Table table) {
        Rows rows = table.getRows();
        for (int index = 0; index < rows.size(); index++) {
            Row row = rows.get(index);
            this.modify(row, rows.size() - index - 1);
        }
    }
}
