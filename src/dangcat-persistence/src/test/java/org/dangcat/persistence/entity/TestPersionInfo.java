package org.dangcat.persistence.entity;

import org.dangcat.commons.database.DatabaseType;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.PersonInfo;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;

import java.util.LinkedList;
import java.util.List;

public class TestPersionInfo extends TestEntityBase {
    private static final int TEST_COUNT = 10;

    @Override
    protected boolean couldTestDatabase(DatabaseType databaseType, boolean defaultValue) {
        return DatabaseType.MySql.equals(databaseType);
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException {
        long beginTime = DateUtils.currentTimeMillis();
        this.logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(PersonInfo.class);
        Table table = entityMetaData.getTable();

        if (table.exists())
            table.drop();
        // 产生新的数据表
        table.create();

        this.testPersonInfo();

        table.drop();

        this.logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void testPersonInfo() throws EntityException {
        EntityManager entityManager = this.getEntityManager();

        List<PersonInfo> personInfoList1 = entityManager.load(PersonInfo.class);
        Assert.assertNull(personInfoList1);

        personInfoList1 = new LinkedList<PersonInfo>();
        for (int i = 0; i < TEST_COUNT; i++) {
            PersonInfo personInfo = new PersonInfo();
            personInfo.setName("Name " + i);
            personInfoList1.add(personInfo);
        }

        entityManager.save(personInfoList1.toArray());

        List<PersonInfo> personInfoList2 = entityManager.load(PersonInfo.class);
        Assert.assertNotNull(personInfoList2);
        Assert.assertEquals(personInfoList1.size(), personInfoList1.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(personInfoList1, personInfoList2));
    }
}
