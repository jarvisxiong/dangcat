package org.dangcat.business.code.entity;

import java.util.LinkedList;
import java.util.List;

public class Table {
    private List<Column> columns = new LinkedList<Column>();
    private String tableName = null;

    public Table(String tableName) {
        this.tableName = tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String getName() {
        return TableUtils.toPropertyName(this.getTableName());
    }

    public String getTableName() {
        return this.tableName;
    }
}
