package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.pool.SessionException;
import org.dangcat.persistence.TestDatabase;
import org.dangcat.persistence.entity.domain.MemberInfo;
import org.dangcat.persistence.entity.domain.TeacherInfo;
import org.dangcat.persistence.entity.domain.TeacherInfoUtils;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TestCompositeEntity extends TestDatabase {
    private static final int TEST_COUNT = 100;
    private static TeacherInfoUtils teacherInfoUtils = new TeacherInfoUtils();

    @BeforeClass
    public static void setUpBeforeClass() throws IOException, SessionException {
        SimulateUtils.configure();
        teacherInfoUtils.createSimulator();
    }

    protected EntityManager getEntityManager() {
        return EntityManagerFactory.getInstance().open();
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        teacherInfoUtils.createTable();
        this.testEntityInsert();
        this.testEntityModify();
        this.testEntityDelete();
        teacherInfoUtils.dropTable();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void testEntityDelete() throws EntityException {
        EntityManager entityManager = this.getEntityManager();
        List<TeacherInfo> teacherInfoList = entityManager.load(TeacherInfo.class);
        Assert.assertEquals(TEST_COUNT, teacherInfoList.size());
        this.getEntityManager().delete(teacherInfoList.toArray());

        Assert.assertNull(entityManager.load(TeacherInfo.class));
        Assert.assertNull(entityManager.load(MemberInfo.class));
    }

    private void testEntityInsert() throws EntityException {
        List<TeacherInfo> teacherInfoList = new LinkedList<TeacherInfo>();
        teacherInfoUtils.createList(teacherInfoList, TEST_COUNT);
        Assert.assertEquals(TEST_COUNT, teacherInfoList.size());

        // 存储数据表。
        EntityManager entityManager = this.getEntityManager();
        entityManager.save(teacherInfoList.toArray());
        // 检查数据存储正确否
        List<TeacherInfo> saveTeacherInfoList = entityManager.load(TeacherInfo.class);
        Assert.assertEquals(teacherInfoList.size(), saveTeacherInfoList.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(teacherInfoList, saveTeacherInfoList));
    }

    private void testEntityModify() throws EntityException {
        EntityManager entityManager = this.getEntityManager();
        // 检查数据存储正确否
        List<TeacherInfo> teacherInfoList = entityManager.load(TeacherInfo.class);
        teacherInfoUtils.modifyList(teacherInfoList);
        Assert.assertEquals(TEST_COUNT, teacherInfoList.size());

        // 存储数据表。
        entityManager.save(teacherInfoList.toArray());

        // 检查数据存储正确否
        List<TeacherInfo> saveTeacherInfoList = entityManager.load(TeacherInfo.class);
        Assert.assertEquals(teacherInfoList.size(), saveTeacherInfoList.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(teacherInfoList, saveTeacherInfoList));
    }
}
