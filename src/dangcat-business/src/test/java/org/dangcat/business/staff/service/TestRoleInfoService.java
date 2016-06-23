package org.dangcat.business.staff.service;

import junit.framework.Assert;
import org.dangcat.boot.ConfigureReader;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.staff.domain.RoleBasic;
import org.dangcat.business.staff.domain.RoleInfo;
import org.dangcat.business.staff.domain.RolePermission;
import org.dangcat.business.staff.domain.simulate.OperatorInfoSimulator;
import org.dangcat.business.staff.domain.simulate.RoleInfoSimulator;
import org.dangcat.business.staff.domain.simulate.RolePermissionSimulator;
import org.dangcat.business.staff.exceptions.OperatorGroupException;
import org.dangcat.business.staff.exceptions.RoleInfoException;
import org.dangcat.business.staff.filter.RoleInfoFilter;
import org.dangcat.business.staff.service.impl.RoleInfoServiceImpl;
import org.dangcat.business.systeminfo.PermissionInfo;
import org.dangcat.business.test.BusinessServiceTestBase;
import org.dangcat.business.test.QueryAssert;
import org.dangcat.business.test.TestServiceQuery;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.simulate.DatabaseSimulator;
import org.dangcat.persistence.simulate.EntitySimulator;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * The service test for Role
 * @author dangcat
 * 
 */
public class TestRoleInfoService extends BusinessServiceTestBase<RoleInfoService, RoleBasic, RoleInfo, RoleInfoFilter>
{
    private static final int TEST_COUNT = 100;

    @Override
    protected void initDatabaseSimulator(DatabaseSimulator databaseSimulator)
    {
        databaseSimulator.add(new RolePermissionSimulator(), 0);
        databaseSimulator.add(new RoleInfoSimulator(), TEST_COUNT);
        databaseSimulator.add(new OperatorInfoSimulator(), TEST_COUNT);
    }

    @Before
    @Override
    public void initialize()
    {
        System.setProperty(ConfigureReader.KEY_SYSTEM_ID, "10");
        // 添加要测试的服务。
        this.addService(RoleInfoService.class, RoleInfoServiceImpl.class);

        super.initialize();
    }

    @Test
    public void testDefaultNew() throws ServiceException
    {
        RoleInfo roleInfo = new RoleInfo();
        this.testDefaultNew(RoleInfo.class, roleInfo);
    }

    @Test
    public void testDelete() throws ServiceException
    {
        this.testDelete(RoleInfo.class, TEST_COUNT);
    }

    @Test
    public void testDeleteRoleWithOperator() throws ServiceException
    {
        // 测试删除已经绑定操作员的操作组
        EntitySimulator roleInfoSimulator = this.getEntitySimulator(RoleInfo.class);
        RoleInfo roleInfo = (RoleInfo) roleInfoSimulator.create(TEST_COUNT + 1);
        this.getEntityManager().save(roleInfo);

        EntitySimulator operatorInfoSimulator = this.getEntitySimulator(OperatorInfo.class);
        OperatorInfo operatorInfo = (OperatorInfo) operatorInfoSimulator.create(TEST_COUNT + 1);
        operatorInfo.setRoleId(roleInfo.getId());
        this.getEntityManager().save(operatorInfo);
        this.assertDelete(roleInfo.getId(), RoleInfoException.CHILE_OPERATOR_EXISTS);
    }

    @Test
    public void testFilter() throws ServiceException
    {
        TestServiceQuery<RoleBasic, RoleInfo, RoleInfoFilter> testServiceQuery = new TestServiceQuery<RoleBasic, RoleInfo, RoleInfoFilter>(this.getBusinessService());
        QueryAssert<RoleInfoFilter> queryAssert = new QueryAssert<RoleInfoFilter>(RoleBasic.class);
        RoleInfoFilter roleInfoFilter = new RoleInfoFilter();
        queryAssert.setDataFilter(roleInfoFilter);

        for (Object entity : this.loadSamples(RoleInfo.class))
        {
            RoleInfo roleInfo = (RoleInfo) entity;
            roleInfoFilter.setName(roleInfo.getName());
            queryAssert.setExpectFilterExpress(new FilterUnit(RoleInfo.Name, FilterType.like, roleInfo.getName()));
            this.testFilter(testServiceQuery, queryAssert);
        }
        roleInfoFilter.setName(null);
    }

    @Test
    public void testQuery() throws ServiceException
    {
        TestServiceQuery<RoleBasic, RoleInfo, RoleInfoFilter> testServiceQuery = new TestServiceQuery<RoleBasic, RoleInfo, RoleInfoFilter>(this.getBusinessService());
        QueryAssert<RoleInfoFilter> queryAssert = new QueryAssert<RoleInfoFilter>(RoleBasic.class);
        queryAssert.setDataFilter(new RoleInfoFilter());
        queryAssert.setExpectTotaleSize(TEST_COUNT);
        this.testQuery(testServiceQuery, queryAssert);
    }

    @Test
    public void testSave() throws ServiceException
    {
        this.testSave(RoleInfo.class, TEST_COUNT);
    }

    @Test
    public void testSaveRoleNameRepeat() throws ServiceException
    {
        // 操作员组的名称不能重复。
        EntitySimulator roleInfoSimulator = this.getEntitySimulator(RoleInfo.class);
        RoleInfo roleInfo1 = (RoleInfo) roleInfoSimulator.create(TEST_COUNT + 1);
        this.getEntityManager().save(roleInfo1);

        // 修改名称重复
        RoleInfo roleInfo2 = (RoleInfo) roleInfoSimulator.create(TEST_COUNT + 2);
        this.getEntityManager().save(roleInfo2);
        roleInfo2.setName(roleInfo1.getName());
        this.getService().save(roleInfo2);
        Assert.assertNotNull(roleInfo2.findServiceException(RoleInfoException.DATA_REPEAT));

        // 新增名称重复
        RoleInfo roleInfo3 = new RoleInfo();
        roleInfo3.setName(roleInfo1.getName());
        this.getService().create(roleInfo3);
        Assert.assertNotNull(roleInfo3.findServiceException(RoleInfoException.DATA_REPEAT));
    }

    @Test
    public void testSaveRoleNotExists() throws ServiceException
    {
        // 所属操作员组不存在。
        EntitySimulator roleInfoSimulator = this.getEntitySimulator(RoleInfo.class);
        RoleInfo roleInfo = (RoleInfo) roleInfoSimulator.create(TEST_COUNT + 1);
        roleInfo.setId(-1);
        this.getService().save(roleInfo);
        Assert.assertNotNull(roleInfo.findServiceException(OperatorGroupException.DATA_NOTEXISTS));
    }

    @Test
    public void testSaveRolePermissions() throws ServiceException
    {
        this.truncate(RoleInfo.class, RolePermission.class);
        // 所属操作员组不存在。
        EntitySimulator roleInfoSimulator = this.getEntitySimulator(RoleInfo.class);
        RoleInfo roleInfo = (RoleInfo) roleInfoSimulator.create(TEST_COUNT + 1);
        roleInfo.getPermissions().add(new PermissionInfo(1001));
        roleInfo.getPermissions().add(new PermissionInfo(100101));
        roleInfo.getPermissions().add(new PermissionInfo(10020301));
        roleInfo.getPermissions().add(new PermissionInfo(10020302));
        this.getService().create(roleInfo);
        Assert.assertFalse(roleInfo.hasError());

        RoleInfo saveRoleInfo = this.getService().view(roleInfo.getId());
        Assert.assertNotNull(saveRoleInfo);

        List<PermissionInfo> expectedList1 = new ArrayList<PermissionInfo>();
        expectedList1.add(new PermissionInfo(10020301));
        expectedList1.add(new PermissionInfo(10020302));
        Assert.assertEquals(expectedList1.size(), saveRoleInfo.getPermissions().size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(expectedList1, saveRoleInfo.getPermissions()));
        Assert.assertEquals(expectedList1.size(), saveRoleInfo.getRolePermissions().size());

        saveRoleInfo.getPermissions().remove(expectedList1.get(expectedList1.size() - 1));
        saveRoleInfo.getPermissions().remove(expectedList1.get(expectedList1.size() - 2));
        saveRoleInfo.getPermissions().add(new PermissionInfo(10020101));
        saveRoleInfo.getPermissions().add(new PermissionInfo(10020204));
        this.getService().save(saveRoleInfo);
        Assert.assertFalse(saveRoleInfo.hasError());

        List<PermissionInfo> expectedList2 = new ArrayList<PermissionInfo>();
        expectedList2.add(new PermissionInfo(10020101));
        expectedList2.add(new PermissionInfo(10020204));

        RoleInfo saveRoleInfo2 = this.getService().view(roleInfo.getId());
        Assert.assertEquals(expectedList2.size(), saveRoleInfo2.getPermissions().size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(expectedList2, saveRoleInfo2.getPermissions()));
        Assert.assertEquals(expectedList2.size(), saveRoleInfo2.getRolePermissions().size());

        saveRoleInfo2.getPermissions().clear();
        this.getService().save(saveRoleInfo2);
        Assert.assertFalse(saveRoleInfo2.hasError());

        RoleInfo saveRoleInfo3 = this.getService().view(roleInfo.getId());
        Assert.assertEquals(0, saveRoleInfo3.getPermissions().size());
        Assert.assertEquals(0, saveRoleInfo3.getRolePermissions().size());
    }

    @Test
    public void testView() throws ServiceException
    {
        this.testView(RoleInfo.class, TEST_COUNT);
    }
}
