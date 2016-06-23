package org.dangcat.persistence.model;

import org.dangcat.commons.formator.DateFormator;
import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.orderby.OrderBy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 数据表辅助工具。
 * @author dangcat
 * 
 */
public class TableStatementHelper
{
    private static final String MACROS_DATE_BEGIN = "{ts '";
    private static final String MACROS_DATE_END = "'}";
    private static final String MACROS_TEXT = "'";
    private static final String MACROS_TEXT_REPLACE = "''";
    private static final String NULL = "NULL";

    /**
     * 产生汇聚语句。
     * @param table 数据表对象。
     * @param filter 过滤条件。
     * @param tableName 临时表表名
     * @return 汇聚语句。
     */
    public static String createAggregationSql(Table table, FilterExpress filter, String tableName)
    {
        return createAggregationSql(table, filter, tableName, false, false);
    }

    /**
     * 产生汇聚语句。
     * @param table 数据表对象。
     * @param filter 过滤条件。
     * @param tableName 临时表表名
     * @param includeOrderby 临时表表名
     * @param includeTopN 临时表表名
     * @return 汇聚语句。
     */
    public static String createAggregationSql(Table table, FilterExpress filter, String tableName, boolean includeOrderby, boolean includeTopN)
    {
        StringBuilder sql = new StringBuilder();
        Column[] primaryKeys = table.getColumns().getPrimaryKeys();

        // 主键
        for (Column column : primaryKeys)
        {
            if (sql.length() == 0)
                sql.append("SELECT ");
            else
                sql.append(", ");
            sql.append(column.getFieldName());
            if (column.getName().equalsIgnoreCase(column.getFieldName()))
            {
                sql.append(" ");
                sql.append(column.getName());
            }
        }

        // 计算栏位
        for (Column column : table.getColumns())
        {
            if (!column.isPrimaryKey())
            {
                sql.append(", ");
                String fieldName = column.getFieldName();
                // 统计字段
                if (fieldName.substring(0, 3).equalsIgnoreCase("max"))
                    sql.append("MAX(" + fieldName + ")");
                else if (fieldName.substring(0, 3).equalsIgnoreCase("min"))
                    sql.append("MIN(" + fieldName + ")");
                else
                    sql.append("SUM(" + fieldName + ")");
                sql.append(" AS " + fieldName);
            }
        }

        if (sql.length() > 0)
        {
            sql.append(Environment.LINE_SEPARATOR + "FROM " + tableName);
            if (filter != null)
            {
                String sqlFilter = filter.toString();
                if (sqlFilter != null && !sqlFilter.equals(""))
                    sql.append(Environment.LINE_SEPARATOR + "WHERE 1=1 AND " + sqlFilter);
            }

            boolean hasCreateGroupBy = false;
            for (Column column : primaryKeys)
            {
                if (!hasCreateGroupBy)
                    sql.append(Environment.LINE_SEPARATOR + "GROUP BY ");
                else
                    sql.append(", ");
                sql.append(column.getFieldName());
                hasCreateGroupBy = true;
            }

            // 排序子句
            if (includeOrderby)
            {
                OrderBy orderBy = table.getOrderBy();
                sql.append(Environment.LINE_SEPARATOR + orderBy.toString());
            }

            // 查询主表，需要恢复TOPN
            if (includeTopN)
            {
                Range range = table.getRange();
                if (range != null)
                {
                    if (range.getFrom() == 0)
                        sql.append(Environment.LINE_SEPARATOR + "LIMIT " + range.getTo());
                    else
                        sql.append(Environment.LINE_SEPARATOR + "LIMIT " + (range.getFrom() - 1) + "," + range.getPageSize());
                }
            }
        }
        return sql.toString();
    }

    /**
     * 根据主表数据行，找到对应的明细数据行。
     * @param masterRow 主表数据行。
     * @param detailTable 明细表对象。
     * @return 找到的数据行集合。
     */
    public static Collection<Row> find(Row masterRow, Table detailTable)
    {
        Table masterTable = masterRow.getParent();
        if (masterTable == null)
            return null;
        // 当前表的栏位集合。
        Columns columns = detailTable.getColumns();
        // 构建主键名称。
        List<String> primaryKeys = new ArrayList<String>();
        // 主键值。
        List<Object> params = new ArrayList<Object>();
        for (Column column : masterTable.getColumns().getPrimaryKeys())
        {
            // 如果当前表不包含此列名则跳过。
            if (columns.find(column.getName()) == null)
                continue;
            // 添加主键和对应值。
            Field field = masterRow.getField(column.getName());
            primaryKeys.add(column.getName());
            params.add(field.getObject());
        }
        return detailTable.getRows().find(primaryKeys.toArray(new String[0]), params.toArray());
    }

    /**
     * 对字符类型取最大长度。
     * @param value 转换值。
     * @param maxLength 最大长度。
     * @return 表达式。
     */
    public static Object subString(Object value, int maxLength)
    {
        if (value != null && maxLength > 0)
        {
            Class<?> classType = value.getClass();
            if (classType.equals(String.class) || classType.equals(Character.class) || classType.equals(byte.class) || classType.equals(byte[].class))
            {
                String strValue = value.toString();
                // 取最大长度。
                if (strValue.length() > maxLength)
                    value = strValue.substring(0, maxLength - 1);
            }
        }
        return value;
    }

    /**
     * 把当前值转成SQL表达式。
     * @param value 转换值。
     * @return 表达式。
     */
    public static String toSqlString(Object value)
    {
        if (value != null)
        {
            Class<?> fieldClass = value.getClass();
            if (ValueUtils.isText(fieldClass))
            {
                String sqlValue = value.toString();
                if (sqlValue.contains(MACROS_TEXT))
                    sqlValue = sqlValue.replace(MACROS_TEXT, MACROS_TEXT_REPLACE);
                return MACROS_TEXT + sqlValue + MACROS_TEXT;
            }
            else if (Date.class.isAssignableFrom(fieldClass))
            {
                SimpleDateFormat simpleDateFormat = DateFormator.getDateFormat(DateType.Full);
                return MACROS_DATE_BEGIN + simpleDateFormat.format(value) + MACROS_DATE_END;
            }

            return value.toString();
        }
        return NULL;
    }
}
