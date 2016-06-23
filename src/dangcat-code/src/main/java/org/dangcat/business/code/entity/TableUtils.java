package org.dangcat.business.code.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

class TableUtils {
    protected static Class<?> getFieldType(ResultSet resultSet) throws SQLException {
        Class<?> fieldClass = null;
        int sqlType = resultSet.getInt("DATA_TYPE");
        switch (sqlType) {
            case Types.NCLOB:
            case Types.CLOB:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                fieldClass = String.class;
                break;
            case Types.TINYINT:
                fieldClass = Byte.class;
                break;
            case Types.BIGINT:
                fieldClass = Long.class;
                break;
            case Types.SMALLINT:
                fieldClass = Short.class;
                break;
            case Types.INTEGER:
                fieldClass = Integer.class;
                break;
            case Types.NUMERIC:
                Integer scale = resultSet.getInt("DECIMAL_DIGITS");
                if (scale == null || scale == 0)
                    fieldClass = Integer.class;
                else
                    fieldClass = Double.class;
                break;
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.DECIMAL:
                fieldClass = Double.class;
                break;
            case Types.BOOLEAN:
                fieldClass = Boolean.class;
                break;
            case Types.TIMESTAMP:
            case Types.DATE:
                fieldClass = Date.class;
                break;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
                fieldClass = byte[].class;
                break;
            case Types.BIT:
                fieldClass = Boolean.class;
                break;
        }
        return fieldClass;
    }

    protected static void sortColumns(Table table) {
        int index = 0;
        for (Column column : table.getColumns()) {
            if (column.isPrimaryKey())
                column.setIndex(index++);
        }
        for (Column column : table.getColumns()) {
            if (!column.isPrimaryKey())
                column.setIndex(index++);
        }

        Collections.sort(table.getColumns(), new Comparator<Column>() {
            public int compare(Column srcColumn, Column dstColumn) {
                if (srcColumn.getIndex() == -1 || dstColumn.getIndex() == -1)
                    return dstColumn.getIndex() - srcColumn.getIndex();
                return srcColumn.getIndex() - dstColumn.getIndex();
            }
        });
    }

    protected static String toPropertyName(String name) {
        name = name.replace("_", "");
        boolean isAllUpperCase = true;
        for (int i = 0; i < name.length(); i++) {
            char charValue = name.charAt(i);
            if (Character.isLetter(charValue) && Character.isLowerCase(charValue)) {
                isAllUpperCase = false;
                break;
            }
        }

        String subString = name.substring(1);
        if (isAllUpperCase)
            subString = subString.toLowerCase();
        name = name.substring(0, 1).toUpperCase() + subString;
        return name;
    }
}
