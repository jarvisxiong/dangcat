package org.dangcat.persistence.model;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.event.TableEventAdapter;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orm.JdbcValueUtils;
import org.dangcat.persistence.orm.Session;
import org.dangcat.persistence.orm.SqlBuilder;
import org.dangcat.persistence.orm.TableSqlBuilder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 表管理器。
 * @author dangcat
 * 
 */
class TableLoadManagerImpl extends TableManagerBase
{
    private String databaseName = null;

    TableLoadManagerImpl(String databaseName)
    {
        this.databaseName = databaseName;
    }

    /**
     * 载入指定表的数据。
     * @param table 表对象。
     * @throws TableException 运行异常。
     */
    protected void load(Table table) throws TableException
    {
        SqlBuilder sqlBuilder = null;
        Session session = null;
        try
        {
            long beginTime = DateUtils.currentTimeMillis();
            if (logger.isDebugEnabled())
                logger.debug("Begin load the table: " + table.getTableName().getName());

            // 载入前事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
            {
                if (!tableEventAdapter.beforeLoad(table))
                    return;
            }

            TableSqlBuilder tableSqlBuilder = new TableSqlBuilder(table, this.databaseName);
            // 计算记录总数。
            Range range = table.getRange();
            if (range != null && range.isCalculateTotalSize())
            {
                int totalSize = 0;
                sqlBuilder = tableSqlBuilder.buildTotalSizeStatement();
                if (sqlBuilder.length() > 0)
                {
                    // 获取会话对象。
                    session = this.openSession(this.databaseName);
                    ResultSet resultSet = session.executeQuery(sqlBuilder.toString());
                    while (resultSet.next())
                        totalSize = resultSet.getInt(Range.TOTALSIZE);
                }
                range.setTotalSize(totalSize);
                if (totalSize == 0)
                {
                    table.getRows().clear();
                    return;
                }
            }
            // 载入数据。
            sqlBuilder = tableSqlBuilder.buildLoadStatement();
            if (sqlBuilder.length() > 0)
            {
                // 获取会话对象。
                if (session == null)
                    session = this.openSession(this.databaseName);
                ResultSet resultSet = session.executeQuery(sqlBuilder.toString());

                // 修改载入状态。
                table.setTableState(TableState.Loading);

                // 如果栏位没有构建需要自动生成。
                if (table.getColumns().size() == 0)
                    session.loadMetaData(table, resultSet);

                // 解析数据结果。
                this.parseData(table, resultSet);
            }

            // 载入后事件。
            table.calculate();
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.afterLoad(table);

            if (logger.isDebugEnabled())
                logger.debug("End load table, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        catch (Exception e)
        {
            String message = sqlBuilder == null ? e.toString() : sqlBuilder.toString();
            if (logger.isDebugEnabled())
                logger.error(message, e);

            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.onLoadError(table, e);

            throw new TableException(message, e);
        }
        finally
        {
            table.setTableState(TableState.Normal);
            if (session != null)
                session.release();
        }
    }

    /**
     * 载入元数据内容。
     * @param table 表对象。
     * @throws TableException 运行异常。
     */
    protected void loadMetaData(Table table) throws TableException
    {
        Session session = null;
        try
        {
            long beginTime = DateUtils.currentTimeMillis();
            if (logger.isDebugEnabled())
                logger.debug("Begin loadMetaData: " + table.getTableName().getName());

            // 获取会话对象。
            session = this.openSession(this.databaseName);
            session.loadMetaData(table, null);

            // 载入元数据事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.onLoadMetaData(table);

            if (logger.isDebugEnabled())
                logger.debug("End loadMetaData, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        catch (Exception e)
        {
            throw new TableException(e);
        }
        finally
        {
            if (session != null)
                session.release();
        }
    }

    /**
     * 解析数据结果。
     * @param row 数据行对象。
     * @param resultSet 查询结果。
     * @throws SQLException
     * @throws TableException
     * @throws TableException
     */
    private void parseData(Table table, ResultSet resultSet) throws SQLException, TableException
    {
        Columns columns = table.getColumns();
        Rows rows = table.getRows();
        rows.clear();
        int position = 0;
        Range range = table.getRange();
        // 获取元数据。
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        while (resultSet.next())
        {
            position++;
            if (range != null && range.getMode() != Range.BY_SQLSYNTAX)
            {
                if (position < range.getFrom())
                    continue;
                else if (position > range.getTo())
                    break;
            }

            Row row = rows.createNewRow();
            for (int i = 1; i <= columnCount; i++)
            {
                String fieldName = resultSetMetaData.getColumnLabel(i);
                Column column = columns.findByFieldName(fieldName);
                if (column != null)
                {
                    Field field = row.getField(column.getName());
                    field.setObject(JdbcValueUtils.read(fieldName, resultSet, column.getFieldClass()));
                }
            }
            rows.add(row);
        }
    }
}
