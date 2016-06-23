package org.dangcat.persistence.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.event.TableEventAdapter;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orm.JdbcValueUtils;
import org.dangcat.persistence.orm.Session;
import org.dangcat.persistence.orm.TableGenerator;
import org.dangcat.persistence.orm.TableSqlBuilder;
import org.dangcat.persistence.tablename.DynamicTableData;
import org.dangcat.persistence.tablename.DynamicTableUtils;

/**
 * 表管理器。
 * @author dangcat
 * 
 */
class TableSaveManagerImpl extends TableManagerBase
{
    private String databaseName = null;
    private Queue<Field> insertFieldQueue = new LinkedList<Field>();

    TableSaveManagerImpl(String databaseName)
    {
        this.databaseName = databaseName;
    }

    /**
     * 执行删除命令。
     * @param table 数据表对象。
     * @throws SQLException
     */
    private void executeDelete(Session session, Table table) throws SQLException
    {
        Collection<Row> deletedRows = table.getRows().getDeletedRows();
        if (deletedRows.size() > 0)
        {
            List<String> fieldNameList = new LinkedList<String>();
            TableSqlBuilder tableSqlBuilder = new TableSqlBuilder(table, this.databaseName);
            String sql = tableSqlBuilder.buildDeleteStatement(fieldNameList);
            Collection<DynamicTableData<Row>> dynamicTableDataCollection = DynamicTableUtils.createDynamicTableDataCollection(deletedRows, table.getTableName());
            if (dynamicTableDataCollection == null)
                this.executeDelete(session, table, deletedRows, fieldNameList, sql);
            else
            {
                for (DynamicTableData<Row> dynamicTableData : dynamicTableDataCollection)
                {
                    Table currentTable = dynamicTableData.getTable();
                    if (!currentTable.exists())
                        continue;
                    String currentSql = DynamicTableUtils.replaceTableName(sql, currentTable.getName());
                    Collection<Row> currentCollection = dynamicTableData.getDataCollection();
                    this.executeDelete(session, table, currentCollection, fieldNameList, currentSql);
                }
            }
        }
    }

    private void executeDelete(Session session, Table table, Collection<Row> deletedRows, List<String> fieldNameList, String sql) throws SQLException
    {
        session.prepare(sql);
        int count = 0;
        for (Row row : deletedRows)
        {
            count++;
            boolean submit = count == deletedRows.size();
            this.executeRowUpdate(session, table, row, fieldNameList, false, submit);
        }
    }

    /**
     * 执行插入命令。
     * @param table 数据表对象。
     * @throws SQLException
     */
    private void executeInsert(Session session, Table table) throws SQLException
    {
        Collection<Row> insertRows = table.getRows().getInsertedRows();
        if (insertRows.size() > 0)
        {
            List<String> fieldNameList = new LinkedList<String>();
            // 构建查询语句
            TableSqlBuilder tableSqlBuilder = new TableSqlBuilder(table, this.databaseName);
            String sql = tableSqlBuilder.buildInsertStatement(fieldNameList);
            // 自增主键列表。
            Column[] primaryKeys = table.getColumns().getPrimaryKeys();
            List<String> primaryFieldNameList = new ArrayList<String>();
            for (Column column : primaryKeys)
            {
                if (column.isIndentityGeneration())
                    primaryFieldNameList.add(column.getName());
            }
            Collection<DynamicTableData<Row>> dynamicTableDataCollection = DynamicTableUtils.createDynamicTableDataCollection(insertRows, table.getTableName());
            if (dynamicTableDataCollection == null)
                this.executeInsert(session, table, insertRows, fieldNameList, sql, primaryFieldNameList.toArray(new String[0]));
            else
            {
                for (DynamicTableData<Row> dynamicTableData : dynamicTableDataCollection)
                {
                    Table currentTable = dynamicTableData.getTable();
                    if (!currentTable.exists())
                        currentTable.create();
                    String currentSql = DynamicTableUtils.replaceTableName(sql, currentTable.getName());
                    Collection<Row> currentCollection = dynamicTableData.getDataCollection();
                    this.executeInsert(session, currentTable, currentCollection, fieldNameList, currentSql, primaryFieldNameList.toArray(new String[0]));
                }
            }
        }
    }

    private void executeInsert(Session session, Table table, Collection<Row> insertRows, List<String> fieldNameList, String sql, String[] primaryFieldNames) throws SQLException
    {
        // 准备表达对象。
        session.prepare(sql, primaryFieldNames);
        // 批处理队列。
        Queue<Row> batchUpdateQueue = new LinkedList<Row>();
        int count = 0;
        for (Row row : insertRows)
        {
            count++;
            boolean submit = count == insertRows.size();
            int submitCount = this.executeRowUpdate(session, table, row, fieldNameList, true, submit);
            if (primaryFieldNames != null && primaryFieldNames.length > 0)
            {
                if (submitCount > 0)
                    this.parseData(table, batchUpdateQueue, session.getGeneratedKeys(), primaryFieldNames);
                else
                    batchUpdateQueue.add(row);
            }
        }
    }

    /**
     * 执行更新命令。
     * @param table 数据表对象。
     * @throws SQLException
     */
    private void executeModified(Session session, Table table) throws SQLException
    {
        Collection<Row> modifiedRows = table.getRows().getModifiedRows();
        if (modifiedRows.size() > 0)
        {
            Columns columns = table.getColumns();
            Collection<DynamicTableData<Row>> dynamicTableDataCollection = DynamicTableUtils.createDynamicTableDataCollection(modifiedRows, table.getTableName());
            if (dynamicTableDataCollection == null)
                this.executeModified(session, table, modifiedRows, columns);
            else
            {
                for (DynamicTableData<Row> dynamicTableData : dynamicTableDataCollection)
                {
                    Table currentTable = dynamicTableData.getTable();
                    if (!currentTable.exists())
                        continue;
                    Collection<Row> currentCollection = dynamicTableData.getDataCollection();
                    this.executeModified(session, currentTable, currentCollection, columns);
                }
            }
        }
    }

    private void executeModified(Session session, Table table, Collection<Row> modifiedRows, Columns columns) throws SQLException
    {
        List<String> fieldNameList = new LinkedList<String>();
        List<String> primaryKeyList = new LinkedList<String>();
        TableSqlBuilder tableSqlBuilder = new TableSqlBuilder(table, this.databaseName);
        for (Row row : modifiedRows)
        {
            String sql = tableSqlBuilder.buildUpdateStatement(row, fieldNameList, primaryKeyList);
            sql = DynamicTableUtils.replaceTableName(sql, table.getName());
            session.prepare(sql);
            for (int i = 0; i < fieldNameList.size(); i++)
            {
                String fieldName = fieldNameList.get(i);
                Column column = columns.find(fieldName);
                Field field = row.getField(fieldName);
                session.setParam(i, field.getObject(), column);
            }

            for (int i = 0; i < primaryKeyList.size(); i++)
            {
                String fieldName = primaryKeyList.get(i);
                Column column = columns.find(fieldName);
                Field field = row.getField(fieldName);
                if (field.getDataState() == DataState.Modified)
                    session.setParam(i + fieldNameList.size(), field.getOldValue(), column);
                else
                    session.setParam(i + fieldNameList.size(), field.getObject(), column);
            }

            session.executeUpdate();
        }
    }

    /**
     * 执行行更新。
     * @param session 会话对象。
     * @param row 行对象。
     * @param fieldNameList 字段列表。
     * @throws SQLException 执行异常。
     */
    private int executeRowUpdate(Session session, Table table, Row row, List<String> fieldNameList, boolean isInsert, boolean submit) throws SQLException
    {
        Columns columns = table.getColumns();
        for (int i = 0; i < fieldNameList.size(); i++)
        {
            String name = fieldNameList.get(i);
            Field field = row.getField(name);
            Object value = field.getObject();
            Column column = columns.find(name);
            if (isInsert)
            {
                if (value == null && column.isSequenceGeneration())
                {
                    TableGenerator tableGenerator = (TableGenerator) column.getParams().get(TableGenerator.class.getSimpleName());
                    value = session.nextSequence(table.getTableName().getName(), column.getName(), column.getFieldClass(), tableGenerator);
                    field.setObject(value);
                    this.insertFieldQueue.add(field);
                }
            }
            session.setParam(i, value, column);
        }
        return session.executeBatchUpdate(submit);
    }

    /**
     * 解析数据结果。
     * @param row 数据行对象。
     * @param resultSet 查询结果。
     * @throws SQLException
     */
    private void parseData(Table table, Queue<Row> batchUpdateQueue, ResultSet resultSet, String[] fieldNames) throws SQLException
    {
        // 获取元数据。
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        Columns columns = table.getColumns();
        while (resultSet.next())
        {
            Row row = batchUpdateQueue.poll();
            for (int i = 1; 0 < columnCount; i++)
            {
                String fieldName = resultSetMetaData.getColumnLabel(i + 1);
                Column column = columns.findByFieldName(fieldName);
                if (column == null && fieldNames != null && fieldNames.length > 0)
                    column = columns.findByFieldName(fieldNames[i]);
                if (column != null)
                {
                    Field field = row.getField(column.getName());
                    field.setObject(JdbcValueUtils.read(fieldName, resultSet, column.getFieldClass()));
                }
            }
        }
    }

    /**
     * 存储出错时，回滚自增字段数值。
     */
    private void resetAutoIncrement(Queue<Field> fieldQueue)
    {
        try
        {
            while (fieldQueue.size() > 0)
            {
                Field field = fieldQueue.poll();
                if (field != null)
                    field.setObject(null);
            }
        }
        catch (Exception e)
        {

        }
    }

    /**
     * 存储指定表的数据。
     * @param table 表对象。
     * @throws TableException 运行异常。
     */
    protected void save(Table table) throws TableException
    {
        Session session = null;
        try
        {
            long beginTime = DateUtils.currentTimeMillis();
            if (logger.isDebugEnabled())
                logger.debug("Begin save the table: " + table.getTableName().getName());

            // 存储前事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
            {
                if (!tableEventAdapter.beforeSave(table))
                    return;
            }

            table.setTableState(TableState.Saving);
            // 获取会话对象。
            session = this.openSession(this.databaseName);

            session.beginTransaction();
            // 存储已经删除的数据行。
            this.executeDelete(session, table);
            // 存储被修改的数据行。
            this.executeModified(session, table);
            // 存储新插入的数据行。
            this.executeInsert(session, table);

            session.commit();
            table.getRows().setDataState(DataState.Browse);

            // 存储后事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.afterSave(table);

            if (logger.isDebugEnabled())
                logger.debug("End save table, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        catch (Exception e)
        {
            this.resetAutoIncrement(this.insertFieldQueue);
            if (session != null)
                session.rollback();

            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.onSaveError(table, e);

            if (logger.isDebugEnabled())
                logger.error(this, e);
            throw new TableException(e);
        }
        finally
        {
            table.setTableState(TableState.Normal);
            if (session != null)
                session.release();
        }
    }
}
