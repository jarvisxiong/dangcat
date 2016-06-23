package org.dangcat.persistence.model;

import org.apache.log4j.Logger;
import org.dangcat.commons.formator.DateFormatProvider;
import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.io.ResourceLoader;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.MathUtils;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.orderby.OrderByType;
import org.dangcat.persistence.orderby.OrderByUnit;
import org.dangcat.persistence.orm.Session;
import org.dangcat.persistence.orm.TableSqlBuilder;
import org.dangcat.persistence.tablename.DateTimeTableName;
import org.dangcat.persistence.tablename.DynamicTable;
import org.dangcat.persistence.tablename.TableName;
import org.dangcat.persistence.xml.TableXmlResolver;
import org.dom4j.DocumentException;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Date;

/**
 * 构建Table对象。
 * @author dangcat
 * 
 */
public class TableUtils
{
    protected static final Logger logger = Logger.getLogger(TableUtils.class);

    /**
     * 指定资源配置文件名称构建表对象。
     * @param sourceClassType 来源对象。
     * @param name 资源配置文件名称。
     * @return 构建的表对象。
     */
    public static Table build(Class<?> sourceClassType, String name)
    {
        Class<?> classType = sourceClassType == null ? TableUtils.class : sourceClassType;
        Table table = null;
        InputStream inputStream = ResourceLoader.load(classType, name);
        if (inputStream != null)
        {
            try
            {
                TableXmlResolver tableXmlResolver = new TableXmlResolver();
                tableXmlResolver.open(inputStream);
                tableXmlResolver.resolve();
                table = tableXmlResolver.getTable();
                buildDateTime(table);
            }
            catch (DocumentException e)
            {
            }
            finally
            {
                FileUtils.close(inputStream);
            }
        }
        return table;
    }

    private static void buildDateTime(Table table)
    {
        for (Column column : table.getColumns())
        {
            if (Date.class.isAssignableFrom(column.getFieldClass()))
            {
                if (column.getDateType() == null)
                    column.setDateType(DateType.Full);
                if (column.getFormatProvider() == null)
                    column.setFormatProvider(new DateFormatProvider(column.getDateType()));
                column.setDisplaySize(column.getFormatProvider().getFormat().length());
            }
        }
    }

    private static int calculateMinRowNum(Collection<Row> rows)
    {
        int minValue = 1;
        Integer zero = 0;
        for (Row row : rows)
        {
            if (row.getNum() == null || zero.equals(row.getNum()))
            {
                minValue = 1;
                break;
            }
            minValue = Math.min(minValue, row.getNum().intValue());
        }
        return minValue;
    }

    public static void calculateRowNum(Collection<Row> rows, Integer startRow)
    {
        if (rows != null && !rows.isEmpty())
        {
            int index = startRow == null ? 1 : startRow;
            if (startRow == null)
                index = calculateMinRowNum(rows);

            for (Row row : rows)
                row.setNum(index++);
        }
    }

    /**
     * 计算数据行加总。
     */
    public static void calculateTotal(Table table)
    {
        boolean existsNumberField = false;
        Row total = table.getRows().createNewRow();
        for (Row row : table.getRows())
        {
            for (Column column : table.getColumns())
            {
                if (!column.isPrimaryKey() && Number.class.isAssignableFrom(column.getFieldClass()))
                {
                    Field totalField = total.getField(column.getName());
                    Field currentField = row.getField(column.getName());
                    totalField.setObject(MathUtils.plus(totalField.getNumber(), currentField.getNumber()));
                    existsNumberField = true;
                }
            }
        }
        table.setTotal(existsNumberField ? total : null);
    }

    /**
     * 比较两个表的内容是否相同。
     * @param srcTable 来源表。
     * @param dstTable 目标表。
     * @return 比较结果。
     */
    public static boolean equalsContent(Table srcTable, Table dstTable)
    {
        if (srcTable.getRows().size() != dstTable.getRows().size())
            return false;

        for (int i = 0; i < srcTable.getRows().size(); i++)
        {
            Row srcRow = srcTable.getRows().get(i);
            Row dstRow = dstTable.getRows().get(i);
            for (Column column : srcTable.getColumns())
            {
                Field srcField = srcRow.getField(column.getName());
                Field dstField = dstRow.getField(column.getName());
                if (srcField.compareTo(dstField) != 0)
                {
                    if (logger.isDebugEnabled())
                        logger.warn(column.getName() + " : srcField= " + srcField.getString() + ", dstField= " + dstField.getString());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断表是否存在。
     * @param table 表对象。
     * @return 是否存在。
     */
    public static boolean exists(Table table, Session session)
    {
        boolean result = true;
        String sql = null;
        try
        {
            long beginTime = DateUtils.currentTimeMillis();
            if (logger.isDebugEnabled())
                logger.debug("Begin check exists table: " + table.getTableName().getName());

            TableSqlBuilder tableSqlBuilder = new TableSqlBuilder(table, session.getName());
            sql = tableSqlBuilder.buildExistsStatement();

            // 获取会话对象。
            ResultSet resultSet = session.executeQuery(sql);
            if (!resultSet.next())
                result = false;

            if (logger.isDebugEnabled())
                logger.debug("End check exists, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        catch (Exception e)
        {
            result = false;
        }
        return result;
    }

    public static DateTimeTableName getDateTimeTableName(Class<?> classType, int field, Integer value)
    {
        DateTimeTableName dateTimeTableName = (DateTimeTableName) getTableName(classType);
        if (dateTimeTableName != null)
        {
            Date dateTime = DateUtils.now();
            if (value != null && value != 0)
                dateTime = DateUtils.add(field, dateTime, value * -1);
            dateTimeTableName.setDateTime(dateTime);
        }
        return dateTimeTableName;
    }

    /**
     * 取得排序对象。
     * @return 排序对象
     */
    public static OrderBy getOrderBy(Table table)
    {
        OrderBy orderBy = null;
        Columns columns = table.getColumns();
        if (columns != null)
        {
            Column[] primaryKeys = columns.getPrimaryKeys();
            if (primaryKeys != null && primaryKeys.length > 0)
            {
                orderBy = new OrderBy();
                for (Column column : primaryKeys)
                    orderBy.add(new OrderByUnit(column.getFieldName(), OrderByType.Asc));
            }
        }
        return orderBy;
    }

    public static Table getTable(Object value)
    {
        Table table = null;
        if (value instanceof Row)
        {
            Row row = (Row) value;
            if (row != null)
                table = row.getParent();
        }
        else if (value instanceof Class<?>)
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData((Class<?>) value);
            if (entityMetaData != null)
                table = entityMetaData.getTable();
        }
        else if (value != null)
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(value.getClass());
            if (entityMetaData != null)
                table = entityMetaData.getTable();
        }
        return table;
    }

    public static TableName getTableName(Object value)
    {
        TableName tableName = null;
        Table table = getTable(value);
        if (table != null)
        {
            tableName = table.getTableName();
            if (tableName instanceof DynamicTable)
                tableName = tableName.copy();
        }
        return tableName;
    }
}
