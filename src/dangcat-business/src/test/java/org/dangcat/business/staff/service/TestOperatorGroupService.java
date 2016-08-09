package org.dangcat.business.staff.service;

import junit.framework.Assert;
import org.dangcat.business.service.QueryResult;
import org.dangcat.business.staff.domain.OperatorGroup;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.staff.domain.simulate.OperatorGroupSimulator;
import org.dangcat.business.staff.domain.simulate.OperatorInfoSimulator;
import org.dangcat.business.staff.domain.simulate.RoleInfoSimulator;
import org.dangcat.business.staff.domain.simulate.RolePermissionSimulator;
import org.dangcat.business.staff.exceptions.OperatorGroupException;
import org.dangcat.business.staff.filter.OperatorGroupFilter;
import org.dangcat.business.staff.service.impl.OperatorGroupLoader;
import org.dangcat.business.staff.service.impl.OperatorGroupServiceImpl;
import org.dangcat.business.staff.service.impl.OperatorInfoServiceImpl;
import org.dangcat.business.staff.service.impl.RoleInfoServiceImpl;
import org.dangcat.business.test.BusinessServiceTestBase;
import org.dangcat.business.test.QueryAssert;
import org.dangcat.business.test.TestServiceQuery;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.simulate.DatabaseSimulator;
import org.dangcat.persistence.simulate.EntitySimulator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * The service test for OperatorGroup
 *
 * @author dangcat
 */
public class TestOperatorGroupService extends BusinessServiceTestBase<OperatorGroupService, OperatorGroup, OperatorGroup, OperatorGroupFilter> {
    private static final int TEST_COUNT = 100;

    private void createMembers(List<OperatorGroup> operatorGroupList, int count) throws ServiceException {
        // 造出有关联的四个组。
        for (int i = 0; i < count; i++) {
            OperatorGroup operatorGroup = operatorGroupList.get(i);
            if (i == 0)
                operatorGroup.setParentId(null);
            else
                operatorGroup.setParentId(operatorGroupList.get(i - 1).getId());
            this.getService().save(operatorGroup);
            Assert.assertFalse(operatorGroup.hasError());
            operatorGroupList.add(operatorGroup);
        }
    }

    @Override
    protected void initDatabaseSimulator(DatabaseSimulator databaseSimulator) {
        databaseSimulator.add(new RolePermissionSimulator(), 0);
        databaseSimulator.add(new RoleInfoSimulator(), 10);
        databaseSimulator.add(new OperatorGroupSimulator(), TEST_COUNT);
        databaseSimulator.add(new OperatorInfoSimulator(), TEST_COUNT);
    }

    @Before
    @Override
    public void initialize() {
        // 添加要测试的服务。
        this.addService(OperatorGroupService.class, OperatorGroupServiceImpl.class);
        this.addService(OperatorInfoService.class, OperatorInfoServiceImpl.class);
        this.addService(RoleInfoService.class, RoleInfoServiceImpl.class);

        super.initialize();
    }

    @Test
    public void testDefaultNew() throws ServiceException {
        OperatorGroup operatorGroup = new OperatorGroup();
        this.testDefaultNew(OperatorGroup.class, operatorGroup);
    }

    @Test
    public void testDelete() throws ServiceException {
        this.truncate(OperatorInfo.class);
        this.testDelete(OperatorGroup.class, TEST_COUNT);
    }

    /**
     * 不能删除已绑定操作员的操作组。
     */
    @Test
    public void testDeleteGroupWithOperator() throws ServiceException {
        // 测试删除已经绑定操作员的操作组
        EntitySimulator operatorGroupSimulator = this.getEntitySimulator(OperatorGroup.class);
        OperatorGroup operatorGroup = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 1);
        this.getEntityManager().save(operatorGroup);

        EntitySimulator operatorInfoSimulator = this.getEntitySimulator(OperatorInfo.class);
        OperatorInfo operatorInfo = (OperatorInfo) operatorInfoSimulator.create(TEST_COUNT + 1);
        operatorInfo.setGroupId(operatorGroup.getId());
        this.getEntityManager().save(operatorInfo);

        assertDelete(operatorInfo.getGroupId(), OperatorGroupException.CHILE_OPERATOR_EXISTS);
    }

    /**
     * 不能删除登录用户所在的组或者子组以外的操作组。
     */
    @Test
    public void testDeleteMembers() throws ServiceException {
        EntitySimulator operatorGroupSimulator = this.getEntitySimulator(OperatorGroup.class);
        OperatorGroup operatorGroup1 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 1);
        OperatorGroup operatorGroup2 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 2);
        this.getEntityManager().save(operatorGroup1, operatorGroup2);

        EntitySimulator operatorInfoSimulator = this.getEntitySimulator(OperatorInfo.class);
        OperatorInfo operatorInfo = (OperatorInfo) operatorInfoSimulator.create(TEST_COUNT + 1);
        operatorInfo.setGroupId(operatorGroup1.getId());
        this.getEntityManager().save(operatorInfo);

        this.login(operatorInfo.getNo());
        assertDelete(operatorGroup2.getId(), OperatorGroupException.DELETE_DENY_FOR_NOTMEMBER);
    }

    /**
     * 不能删除已绑定为父组的操作组。
     */
    @Test
    public void testDeleteParent() throws ServiceException {
        this.truncate(OperatorInfo.class);
        int count = 4;
        // 造出有关联的四个组。
        List<OperatorGroup> operatorGroupList = this.getEntityManager().load(OperatorGroup.class);
        this.createMembers(operatorGroupList, count);

        for (int i = 0; i < count - 1; i++)
            assertDelete(operatorGroupList.get(0).getId(), OperatorGroupException.DELETE_DENY_FOR_ISPARENT);
    }

    @Test
    public void testFilter() throws ServiceException {
        TestServiceQuery<OperatorGroup, OperatorGroup, OperatorGroupFilter> testServiceQuery = new TestServiceQuery<OperatorGroup, OperatorGroup, OperatorGroupFilter>(this.getBusinessService());
        QueryAssert<OperatorGroupFilter> queryAssert = new QueryAssert<OperatorGroupFilter>(OperatorGroup.class);
        OperatorGroupFilter operatorGroupFilter = new OperatorGroupFilter();
        queryAssert.setDataFilter(operatorGroupFilter);

        OperatorGroupLoader operatorGroupLoader = new OperatorGroupLoader(this.getEntityManager());
        Integer[] groupIds = operatorGroupLoader.loadMemberIds();

        for (Object entity : this.loadSamples(OperatorGroup.class)) {
            OperatorGroup operatorGroup = (OperatorGroup) entity;
            operatorGroupFilter.setName(operatorGroup.getName());
            FilterGroup filterGroup = new FilterGroup();
            filterGroup.add(new FilterUnit(OperatorGroup.Name, FilterType.like, operatorGroup.getName()));
            if (groupIds != null)
                filterGroup.add(new FilterUnit(OperatorGroup.Id, FilterType.eq, (Object[]) groupIds));
            queryAssert.setExpectFilterExpress(filterGroup);
            this.testFilter(testServiceQuery, queryAssert);
        }
        operatorGroupFilter.setName(null);
    }

    /**
     * 只能查询登录用户所在的组或者子组的数据。
     */
    @Test
    public void testLoadMembers() throws ServiceException {
        int count = 4;
        // 造出有关联的四个组。
        List<OperatorGroup> operatorGroupList = this.getEntityManager().load(OperatorGroup.class);
        this.createMembers(operatorGroupList, count);
        // 造出四个操作员绑定上面用户
        List<OperatorInfo> operatorInfoList = this.getEntityManager().load(OperatorInfo.class);
        // 造出四个操作员绑定上面用户
        OperatorInfoService operatorInfoService = this.getService(OperatorInfoService.class);
        for (int i = 0; i < count; i++) {
            OperatorInfo operatorInfo = operatorInfoList.get(i);
            operatorInfo.setGroupId(operatorGroupList.get(i).getId());
            operatorInfoService.save(operatorInfo);
            Assert.assertFalse(operatorInfo.hasError());
        }
        // 以第一个账号登录。
        for (int i = 0; i < count; i++) {
            this.login(operatorInfoList.get(i).getNo());
            Map<Integer, String> members = this.getService().loadMembers();
            Assert.assertNotNull(members);
            Assert.assertEquals(count - i, members.size());
            QueryResult<OperatorGroup> queryResult = this.getService().query(new OperatorGroupFilter());
            Assert.assertNotNull(queryResult.getData());
            Assert.assertEquals(count - i, queryResult.getData().size());
        }
    }

    /**
     * 不能修改登录用户所在的组或者子组以外的操作组。
     */
    @Test
    public void testModifyMembers() throws ServiceException {
        this.truncate(OperatorInfo.class);

        EntitySimulator operatorGroupSimulator = this.getEntitySimulator(OperatorGroup.class);
        OperatorGroup operatorGroup1 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 1);
        OperatorGroup operatorGroup2 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 2);
        this.getEntityManager().save(operatorGroup1, operatorGroup2);

        EntitySimulator operatorInfoSimulator = this.getEntitySimulator(OperatorInfo.class);
        OperatorInfo operatorInfo = (OperatorInfo) operatorInfoSimulator.create(TEST_COUNT + 1);
        operatorInfo.setGroupId(operatorGroup1.getId());
        this.getEntityManager().save(operatorInfo);

        this.login(operatorInfo.getNo());
        assertSave(operatorGroup2, OperatorGroupException.MODIFY_DENY_FOR_NOTMEMBER);
    }

    @Test
    public void testQuery() throws ServiceException {
        TestServiceQuery<OperatorGroup, OperatorGroup, OperatorGroupFilter> testServiceQuery = new TestServiceQuery<OperatorGroup, OperatorGroup, OperatorGroupFilter>(this.getBusinessService());
        QueryAssert<OperatorGroupFilter> queryAssert = new QueryAssert<OperatorGroupFilter>(OperatorGroup.class);
        queryAssert.setDataFilter(new OperatorGroupFilter());
        queryAssert.setExpectTotaleSize(TEST_COUNT);
        this.testQuery(testServiceQuery, queryAssert);
    }

    @Test
    public void testSave() throws ServiceException {
        this.testSave(OperatorGroup.class, TEST_COUNT);
    }

    /**
     * 操作组的名称不能重复。
     */
    @Test
    public void testSaveGroupNameRepeat() throws ServiceException {
        // 操作员组的名称不能重复。
        EntitySimulator operatorGroupSimulator = this.getEntitySimulator(OperatorGroup.class);
        OperatorGroup operatorGroup1 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 1);
        this.getEntityManager().save(operatorGroup1);

        // 修改名称重复
        OperatorGroup operatorGroup2 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 2);
        this.getEntityManager().save(operatorGroup2);
        operatorGroup2.setName(operatorGroup1.getName());
        this.getService().save(operatorGroup2);
        Assert.assertNotNull(operatorGroup2.findServiceException(OperatorGroupException.DATA_REPEAT));

        // 新增名称重复
        OperatorGroup operatorGroup3 = new OperatorGroup();
        operatorGroup3.setName(operatorGroup1.getName());
        this.getService().create(operatorGroup3);
        Assert.assertNotNull(operatorGroup3.findServiceException(OperatorGroupException.DATA_REPEAT));
    }

    /**
     * 不能存储不存在的操作组。
     */
    @Test
    public void testSaveGroupNotExists() throws ServiceException {
        // 所属操作员组不存在。
        EntitySimulator operatorGroupSimulator = this.getEntitySimulator(OperatorGroup.class);
        OperatorGroup operatorGroup = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 1);
        operatorGroup.setId(-1);
        this.getService().save(operatorGroup);
        Assert.assertNotNull(operatorGroup.findServiceException(OperatorGroupException.DATA_NOTEXISTS));
    }

    /**
     * 不能循环绑定操作组。
     */
    @Test
    public void testSaveGroupParentCycling() throws ServiceException {
        EntitySimulator operatorGroupSimulator = this.getEntitySimulator(OperatorGroup.class);
        OperatorGroup operatorGroup1 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 1);
        this.getEntityManager().save(operatorGroup1);

        // 测试所属组等于本组，造成绑定循环。
        operatorGroup1.setParentId(operatorGroup1.getId());
        this.getService().save(operatorGroup1);
        Assert.assertNotNull(operatorGroup1.findServiceException(OperatorGroupException.BIND_CYCLING));

        // 测试所属组绑定循环。
        OperatorGroup operatorGroup2 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 2);
        operatorGroup2.setParentId(operatorGroup1.getId());
        this.getEntityManager().save(operatorGroup2);
        operatorGroup1.setParentId(operatorGroup1.getId());
        this.getService().save(operatorGroup1);
        Assert.assertNotNull(operatorGroup1.findServiceException(OperatorGroupException.BIND_CYCLING));
    }

    /**
     * 不能绑定不存在的父操作组。
     */
    @Test
    public void testSaveParentGroupNotExists() throws ServiceException {
        // 所属操作员组不存在。
        EntitySimulator operatorGroupSimulator = this.getEntitySimulator(OperatorGroup.class);
        OperatorGroup operatorGroup = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 1);
        operatorGroup.setParentId(-1);
        this.getService().create(operatorGroup);
        Assert.assertNotNull(operatorGroup.findServiceException(OperatorGroupException.DATA_NOTEXISTS));
    }

    @Test
    public void testView() throws ServiceException {
        this.testView(OperatorGroup.class, TEST_COUNT);
    }
}
