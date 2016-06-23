package org.dangcat.persistence.entity;

import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Table;

import java.util.ArrayList;
import java.util.List;

class EntityJoinUtils {
    private static Table createAccountInfo() {
        Table table = new Table("AccountInfo");
        table.getColumns().add("Id", Integer.class, true);
        table.getColumns().add("Name", String.class, 40);
        table.getColumns().add("ServiceId", Integer.class);
        table.getColumns().add("GroupId", Integer.class);

        Row row = table.getRows().createNewRow();
        row.getField("Id").setInteger(1);
        row.getField("Name").setString("Accoun Name 1");
        row.getField("ServiceId").setInteger(1);
        row.getField("GroupId").setInteger(2);
        table.getRows().add(row);

        row = table.getRows().createNewRow();
        row.getField("Id").setInteger(2);
        row.getField("Name").setString("Accoun Name 2");
        row.getField("ServiceId").setInteger(2);
        row.getField("GroupId").setInteger(1);
        table.getRows().add(row);

        return table;
    }

    private static Table createGroupInfo() {
        Table table = new Table("GroupInfo");
        table.getColumns().add("Id", Integer.class, true);
        table.getColumns().add("Name", String.class, 40);
        table.getColumns().add("Description", String.class, 40);

        Row row = table.getRows().createNewRow();
        row.getField("Id").setInteger(1);
        row.getField("Name").setString("Group Name 1");
        row.getField("Description").setString("Group Description 1");
        table.getRows().add(row);

        row = table.getRows().createNewRow();
        row.getField("Id").setInteger(2);
        row.getField("Name").setString("Group Name 2");
        row.getField("Description").setString("Group Description 2");
        table.getRows().add(row);
        return table;
    }

    private static Table createServiceInfo() {
        Table table = new Table("ServiceInfo");
        table.getColumns().add("Id", Integer.class, true);
        table.getColumns().add("Name", String.class, 40);
        table.getColumns().add("Description", String.class, 40);

        Row row = table.getRows().createNewRow();
        row.getField("Id").setInteger(1);
        row.getField("Name").setString("Service Name 1");
        row.getField("Description").setString("Service Description 1");
        table.getRows().add(row);

        row = table.getRows().createNewRow();
        row.getField("Id").setInteger(2);
        row.getField("Name").setString("Service Name 2");
        row.getField("Description").setString("Service Description 2");
        table.getRows().add(row);
        return table;
    }

    static List<Table> getTable() {
        List<Table> tableList = new ArrayList<Table>();
        Table masterTable = createAccountInfo();
        tableList.add(masterTable);

        Table detail1Table = createServiceInfo();
        tableList.add(detail1Table);

        Table detail2Table = createGroupInfo();
        tableList.add(detail2Table);
        return tableList;
    }
}
