package org.dangcat.persistence.entity;

import org.dangcat.persistence.model.Table;

import java.util.ArrayList;
import java.util.List;

class EntityRelationUtils {
    private static Table createBillDetail() {
        Table table = new Table("BillDetail");
        table.getColumns().add("Id", Integer.class, true);
        table.getColumns().add("Name", String.class, 40);
        table.getColumns().add("BillId", Integer.class);
        table.getColumns().add("Amount", Double.class);
        return table;
    }

    private static Table createBillInfo() {
        Table table = new Table("BillInfo");
        table.getColumns().add("Id", Integer.class, true);
        table.getColumns().add("Name", String.class, 40);
        table.getColumns().add("Amount", Double.class);
        return table;
    }

    static List<Table> getTable() {
        List<Table> tableList = new ArrayList<Table>();
        Table masterTable = createBillInfo();
        tableList.add(masterTable);

        Table detail1Table = createBillDetail();
        tableList.add(detail1Table);
        return tableList;
    }
}
