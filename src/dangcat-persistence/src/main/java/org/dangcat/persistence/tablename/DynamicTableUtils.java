package org.dangcat.persistence.tablename;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableUtils;
import org.dangcat.persistence.orm.SqlBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DynamicTableUtils {
    private static final String DYNAMIC_TABLE_NAME = "<%tableName%>";

    public static <T> Collection<DynamicTableData<T>> createDynamicTableDataCollection(Collection<T> valueCollection, TableName tableName) {
        Collection<DynamicTableData<T>> dynamicTableDataCollection = null;
        if (valueCollection != null) {
            DateTimeTableName dateTimeTableName = null;
            if (tableName instanceof DateTimeTableName)
                dateTimeTableName = (DateTimeTableName) tableName;
            Table table = null;
            DynamicTable dynamicTable = null;
            Map<String, DynamicTableData<T>> dynamicTableDataMap = null;
            for (T value : valueCollection) {
                if (table == null) {
                    table = TableUtils.getTable(value);
                    if (table == null)
                        break;
                    dynamicTable = getDynamicTable(table.getTableName());
                    if (dynamicTable == null)
                        break;
                }

                String name = getTableName(dateTimeTableName == null ? table.getTableName() : dateTimeTableName, value);
                if (!ValueUtils.isEmpty(name)) {
                    if (dynamicTableDataMap == null)
                        dynamicTableDataMap = new HashMap<String, DynamicTableData<T>>();
                    DynamicTableData<T> dynamicTableData = dynamicTableDataMap.get(name);
                    if (dynamicTableData == null) {
                        dynamicTableData = new DynamicTableData<T>(name, (Table) table.clone());
                        dynamicTableDataMap.put(name, dynamicTableData);
                    }
                    dynamicTableData.getDataCollection().add(value);
                }
            }
            if (dynamicTableDataMap != null)
                dynamicTableDataCollection = dynamicTableDataMap.values();
        }
        return dynamicTableDataCollection;
    }

    public static String getActualTableName(TableName tableName) {
        String name = null;
        if (tableName != null) {
            if (tableName instanceof DateTimeTableName) {
                DateTimeTableName dateTimeTableName = (DateTimeTableName) tableName;
                if (dateTimeTableName.getDateTime() == null)
                    name = dateTimeTableName.getName(DateUtils.now());
                else
                    name = dateTimeTableName.getName();
            } else
                name = tableName.getName();
        }
        return name;
    }

    private static DynamicTable getDynamicTable(TableName tableName) {
        DynamicTable dynamicTable = null;
        if (tableName != null) {
            if (tableName instanceof DynamicTable)
                dynamicTable = (DynamicTable) tableName;
        }
        return dynamicTable;
    }

    public static String getTableName(TableName tableName) {
        String name = null;
        DynamicTable dynamicTable = getDynamicTable(tableName);
        if (dynamicTable != null)
            name = DYNAMIC_TABLE_NAME;
        else if (tableName != null && !ValueUtils.isEmpty(tableName.getName()))
            name = tableName.getName();
        return name;
    }

    public static String getTableName(TableName tableName, Object value) {
        String name = getTableName(tableName);
        DynamicTable dynamicTable = getDynamicTable(tableName);
        if (dynamicTable != null) {
            String dynamicTableName = dynamicTable.getName(value);
            if (!ValueUtils.isEmpty(dynamicTableName))
                name = dynamicTableName;
        }
        return name;
    }

    public static SqlBuilder replaceTableName(SqlBuilder sqlBuilder, TableName tableName) {
        if (tableName != null) {
            String name = getActualTableName(tableName);
            if (!ValueUtils.isEmpty(name))
                sqlBuilder = new SqlBuilder(replaceTableName(sqlBuilder.toString(), name));
        }
        return sqlBuilder;
    }

    public static String replaceTableName(String sql, String tableName) {
        return sql.replace(DYNAMIC_TABLE_NAME, tableName);
    }
}
