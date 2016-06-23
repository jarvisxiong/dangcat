package org.dangcat.document;

import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.simulate.TableSimulator;
import org.dangcat.persistence.simulate.data.NumberSimulator;
import org.dangcat.persistence.simulate.data.StringSimulator;

public class TableDataUtils {
    private static TableSimulator tableSimulator = null;

    public static void createTableData(Table table, int size) throws TableException {
        getTableSimulator().create(table, size);
    }

    public static void createTableSimulator() {
        TableSimulator tableSimulator = new TableSimulator(getTable());
        tableSimulator.initialize();
        // FieldA
        StringSimulator filedASimulator = tableSimulator.findValueSimulator(EntityData.FieldA);
        filedASimulator.setPrefix("A-");
        // FieldC
        NumberSimulator filedCSimulator = tableSimulator.findValueSimulator(EntityData.FieldC);
        filedCSimulator.setStep(3.14);
        // FieldD
        NumberSimulator filedDSimulator = tableSimulator.findValueSimulator(EntityData.FieldD);
        filedDSimulator.setStep(100000000l);

        TableDataUtils.tableSimulator = tableSimulator;
    }

    public static Table getTable() {
        return EntityDataUtils.getTable();
    }

    private static TableSimulator getTableSimulator() {
        if (tableSimulator == null)
            createTableSimulator();
        return tableSimulator;
    }

    public static void modifyTableData(Table table) throws TableException {
        getTableSimulator().modify(table);
    }
}
