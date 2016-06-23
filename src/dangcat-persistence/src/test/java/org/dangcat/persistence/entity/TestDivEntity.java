package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.entity.domain.EntitySimulatorUtils;
import org.dangcat.persistence.entity.domain.OperatorLog;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.dangcat.persistence.tablename.DateTimeTableName;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.*;
import java.util.Map.Entry;

public class TestDivEntity extends TestEntityBase
{
    private static final int TEST_COUNT = 100;
    private static EntitySimulatorUtils<OperatorLog> operatorLogUtils = new EntitySimulatorUtils<OperatorLog>(OperatorLog.class);

    @BeforeClass
    public static void createSimulator()
    {
        operatorLogUtils.createSimulator();
    }

    private Map<String, Collection<OperatorLog>> createOperatorLogMap(Collection<OperatorLog> operatorLogCollection, int dateType)
    {
        DateTimeTableName dateTimeTableName = new DateTimeTableName(OperatorLog.class.getSimpleName());
        dateTimeTableName.setDateType(dateType);
        Map<String, Collection<OperatorLog>> operatorLogMap = new HashMap<String, Collection<OperatorLog>>();
        for (OperatorLog operatorLog : operatorLogCollection)
        {
            dateTimeTableName.setDateTime(operatorLog.getDateTime());
            Collection<OperatorLog> dataCollection = operatorLogMap.get(dateTimeTableName.getName());
            if (dataCollection == null)
            {
                dataCollection = new HashSet<OperatorLog>();
                operatorLogMap.put(dateTimeTableName.getName(), dataCollection);
            }
            dataCollection.add(operatorLog);
        }
        return operatorLogMap;
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        this.testDivEntity(DateUtils.YEAR);
        this.testDivEntity(DateUtils.MONTH);

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void testDelete(List<OperatorLog> entityList, Map<String, Collection<OperatorLog>> operatorLogMap, DateTimeTableName dateTimeTableName) throws EntityException
    {
        // 存储数据表。
        EntityManager entityManager = this.getEntityManager();
        entityManager.delete(entityList.toArray());

        // 检查数据存储正确否
        for (Entry<String, Collection<OperatorLog>> entry : operatorLogMap.entrySet())
        {
            dateTimeTableName.setDateTime(dateTimeTableName.parse(entry.getKey()));
            LoadEntityContext loadEntityContext = new LoadEntityContext(OperatorLog.class);
            loadEntityContext.setTableName(dateTimeTableName);
            List<OperatorLog> saveEntityList = entityManager.load(loadEntityContext);
            Assert.assertNull(saveEntityList);
        }
    }

    private void testDivEntity(int dateType) throws EntityException
    {
        List<OperatorLog> entityList = new LinkedList<OperatorLog>();
        operatorLogUtils.createList(entityList, TEST_COUNT);
        Assert.assertEquals(TEST_COUNT, entityList.size());

        Map<String, Collection<OperatorLog>> operatorLogMap = createOperatorLogMap(entityList, dateType);
        operatorLogUtils.dropTables(operatorLogMap.keySet());

        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(OperatorLog.class);
        DateTimeTableName dateTimeTableName = (DateTimeTableName) entityMetaData.getTableName();
        dateTimeTableName.setDateType(dateType);

        this.testInsert(entityList, operatorLogMap, dateTimeTableName);
        this.testModify(entityList, operatorLogMap, dateTimeTableName);
        this.testDelete(entityList, operatorLogMap, dateTimeTableName);

        operatorLogUtils.dropTables(operatorLogMap.keySet());
    }

    private void testInsert(List<OperatorLog> entityList, Map<String, Collection<OperatorLog>> operatorLogMap, DateTimeTableName dateTimeTableName) throws EntityException
    {
        // 存储数据表。
        EntityManager entityManager = this.getEntityManager();
        entityManager.save(entityList.toArray());

        // 检查数据存储正确否
        for (Entry<String, Collection<OperatorLog>> entry : operatorLogMap.entrySet())
        {
            dateTimeTableName.setDateTime(dateTimeTableName.parse(entry.getKey()));
            LoadEntityContext loadEntityContext = new LoadEntityContext(OperatorLog.class);
            loadEntityContext.setTableName(dateTimeTableName);
            List<OperatorLog> saveEntityList = entityManager.load(loadEntityContext);
            Assert.assertTrue(SimulateUtils.compareDataCollection(entry.getValue(), saveEntityList));
        }
        dateTimeTableName.setDateTime(null);
    }

    private void testModify(List<OperatorLog> entityList, Map<String, Collection<OperatorLog>> operatorLogMap, DateTimeTableName dateTimeTableName) throws EntityException
    {
        for (OperatorLog operatorLog : entityList)
        {
            operatorLog.setOperatorId(TEST_COUNT - operatorLog.getOperatorId());
            operatorLog.setPermissionId(TEST_COUNT - operatorLog.getPermissionId());
            operatorLog.setRemark("Remark " + operatorLog.getId());
            operatorLog.setResult(TEST_COUNT - operatorLog.getResult());
        }
        this.testInsert(entityList, operatorLogMap, dateTimeTableName);
    }
}
