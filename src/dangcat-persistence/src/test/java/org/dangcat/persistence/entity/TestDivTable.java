package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.pool.SessionException;
import org.dangcat.persistence.entity.domain.OperatorLog;
import org.dangcat.persistence.entity.domain.TableSimulatorUtils;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Field;
import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.dangcat.persistence.tablename.DateTimeTableName;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class TestDivTable extends TestEntityBase
{
    private static final int TEST_COUNT = 100;
    private static TableSimulatorUtils operatorLogUtils = new TableSimulatorUtils();

    @BeforeClass
    public static void createSimulator() throws IOException, SessionException
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(OperatorLog.class);
        operatorLogUtils.createSimulator(entityMetaData.getTable());
    }

    private Map<String, Collection<Row>> createOperatorLogMap(Collection<Row> rowCollection, int dateType)
    {
        DateTimeTableName dateTimeTableName = new DateTimeTableName(OperatorLog.class.getSimpleName());
        dateTimeTableName.setDateType(dateType);
        Map<String, Collection<Row>> operatorLogMap = new HashMap<String, Collection<Row>>();
        for (Row row : rowCollection)
        {
            dateTimeTableName.setDateTime(row.getField(OperatorLog.DateTime).getDate());
            Collection<Row> dataCollection = operatorLogMap.get(dateTimeTableName.getName());
            if (dataCollection == null)
            {
                dataCollection = new HashSet<Row>();
                operatorLogMap.put(dateTimeTableName.getName(), dataCollection);
            }
            dataCollection.add(row);
        }
        return operatorLogMap;
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        this.testDivTable(DateUtils.YEAR);
        this.testDivTable(DateUtils.MONTH);

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void testDelete(Table table, Map<String, Collection<Row>> operatorLogMap) throws EntityException
    {
        // 存储数据表。
        table.getRows().removeAll();
        table.save();

        DateTimeTableName dateTimeTableName = (DateTimeTableName) table.getTableName();
        // 检查数据存储正确否
        for (Entry<String, Collection<Row>> entry : operatorLogMap.entrySet())
        {
            dateTimeTableName.setDateTime(dateTimeTableName.parse(entry.getKey()));
            Table operatorLogTable = operatorLogUtils.getTable();
            operatorLogTable.setName(dateTimeTableName.getName());
            operatorLogTable.load();
            Assert.assertTrue(operatorLogTable.getRows().isEmpty());
        }
    }

    private void testDivTable(int dateType) throws EntityException
    {
        Table table = operatorLogUtils.getTable();
        operatorLogUtils.create(table, TEST_COUNT);
        Assert.assertEquals(TEST_COUNT, table.getRows().size());

        Map<String, Collection<Row>> operatorLogMap = createOperatorLogMap(table.getRows(), dateType);
        operatorLogUtils.dropTables(operatorLogMap.keySet());

        DateTimeTableName dateTimeTableName = new DateTimeTableName(OperatorLog.class.getSimpleName());
        dateTimeTableName.setDateType(dateType);
        table.setTableName(dateTimeTableName);

        this.testInsert(table, operatorLogMap);
        this.testModify(table, operatorLogMap);
        this.testDelete(table, operatorLogMap);

        operatorLogUtils.dropTables(operatorLogMap.keySet());
    }

    private void testInsert(Table table, Map<String, Collection<Row>> operatorLogMap) throws EntityException
    {
        // 存储数据表。
        table.save();

        DateTimeTableName dateTimeTableName = (DateTimeTableName) table.getTableName();
        // 检查数据存储正确否
        for (Entry<String, Collection<Row>> entry : operatorLogMap.entrySet())
        {
            dateTimeTableName.setDateTime(dateTimeTableName.parse(entry.getKey()));
            Table operatorLogTable = operatorLogUtils.getTable();
            operatorLogTable.setName(dateTimeTableName.getName());
            operatorLogTable.load();
            Assert.assertFalse(operatorLogTable.getRows().isEmpty());
            Assert.assertTrue(SimulateUtils.compareDataCollection(entry.getValue(), operatorLogTable.getRows()));
        }
        dateTimeTableName.setDateTime(null);
    }

    private void testModify(Table table, Map<String, Collection<Row>> operatorLogMap) throws EntityException
    {
        for (Row row : table.getRows())
        {
            Field operatorId = row.getField(OperatorLog.OperatorId);
            operatorId.setInteger(TEST_COUNT - operatorId.getInteger());
            Field permissionId = row.getField(OperatorLog.PermissionId);
            permissionId.setInteger(TEST_COUNT - permissionId.getInteger());
            Field remark = row.getField(OperatorLog.Remark);
            remark.setString("Remark " + operatorId.getInteger());
            Field result = row.getField(OperatorLog.Result);
            result.setInteger(TEST_COUNT - result.getInteger());
        }
        this.testInsert(table, operatorLogMap);
    }
}
