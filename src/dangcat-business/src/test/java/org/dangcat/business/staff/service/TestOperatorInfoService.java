package org.dangcat.business.staff.service;

import junit.framework.Assert;
import org.dangcat.boot.security.SecurityUtils;
import org.dangcat.business.service.QueryResult;
import org.dangcat.business.staff.config.StaffConfig;
import org.dangcat.business.staff.domain.OperatorGroup;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.staff.domain.OperatorInfoCreate;
import org.dangcat.business.staff.domain.simulate.OperatorGroupSimulator;
import org.dangcat.business.staff.domain.simulate.OperatorInfoSimulator;
import org.dangcat.business.staff.domain.simulate.RoleInfoSimulator;
import org.dangcat.business.staff.domain.simulate.RolePermissionSimulator;
import org.dangcat.business.staff.exceptions.OperatorInfoException;
import org.dangcat.business.staff.filter.OperatorInfoFilter;
import org.dangcat.business.staff.service.impl.*;
import org.dangcat.business.test.BusinessServiceTestBase;
import org.dangcat.business.test.QueryAssert;
import org.dangcat.business.test.TestServiceQuery;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.simulate.DatabaseSimulator;
import org.dangcat.persistence.simulate.EntitySimulator;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.dangcat.persistence.validator.exception.DataValidateException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The service test for Operator
 *
 * @author dangcat
 */
public class TestOperatorInfoService extends BusinessServiceTestBase<OperatorInfoService, OperatorInfo, OperatorInfo, OperatorInfoFilter> {
    private static final int TEST_COUNT = 100;

    private void changePassword(String orgPassword, String newPassword, String fieldName, Integer messageId) throws ServiceException {
        try {
            String no = this.getServicePrincipal().getNo();
            String password1 = orgPassword == null ? null : SecurityUtils.encryptPassword(no, orgPassword);
            String password2 = newPassword == null ? null : SecurityUtils.encryptPassword(no, newPassword);
            this.getService().changePassword(password1, password2);
            if (messageId != null)
                throw new ServiceException("changePassword execute error.");
        } catch (OperatorInfoException e) {
            Assert.assertEquals(fieldName, e.getFieldName());
            Assert.assertEquals(messageId, e.getMessageId());
        }
    }

    @Override
    protected void initDatabaseSimulator(DatabaseSimulator databaseSimulator) {
        databaseSimulator.add(new RolePermissionSimulator(), 0);
        databaseSimulator.add(new RoleInfoSimulator(), 10);
        databaseSimulator.add(new OperatorGroupSimulator(), 10);
        databaseSimulator.add(new OperatorInfoSimulator(), TEST_COUNT);
    }

    @Before
    @Override
    public void initialize() {
        // 添加要测试的服务。
        this.addService(OperatorGroupService.class, OperatorGroupServiceImpl.class);
        this.addService(RoleInfoService.class, RoleInfoServiceImpl.class);
        this.addService(OperatorInfoService.class, OperatorInfoServiceImpl.class);

        super.initialize();
    }

    private void resetPassword(String no, String password, String fieldName, Integer messageId) throws ServiceException {
        try {
            String operatorNo = no == null ? null : SecurityUtils.encryptContent(no);
            String password1 = password == null ? null : SecurityUtils.encryptPassword(no, password);
            this.getService().resetPassword(operatorNo, password1);
            if (messageId != null)
                throw new ServiceException("changePassword execute error.");
        } catch (OperatorInfoException e) {
            Assert.assertEquals(fieldName, e.getFieldName());
            Assert.assertEquals(messageId, e.getMessageId());
        }
    }

    /**
     * 测试特殊修改权限。
     */
    @Test
    public void testAdvancedModify() throws ServiceException {
        OperatorInfo operatorInfo = this.getEntityManager().load(OperatorInfo.class, 0);
        this.login(operatorInfo.getNo(), 0);

        OperatorInfo operatorInfo1 = this.getEntityManager().load(OperatorInfo.class, 1);
        operatorInfo1.setGroupId(operatorInfo1.getGroupId() + 1);
        this.assertSave(operatorInfo1, OperatorInfoException.ADVANCED_MODIFY);

        OperatorInfo operatorInfo2 = this.getEntityManager().load(OperatorInfo.class, 2);
        operatorInfo2.setRoleId(operatorInfo2.getRoleId() + 1);
        this.assertSave(operatorInfo2, OperatorInfoException.ADVANCED_MODIFY);

        OperatorInfo operatorInfo3 = this.getEntityManager().load(OperatorInfo.class, 3);
        operatorInfo3.setUseAble(operatorInfo3.getUseAble() ? Boolean.FALSE : Boolean.TRUE);
        this.assertSave(operatorInfo3, OperatorInfoException.ADVANCED_MODIFY);

        OperatorInfo operatorInfo4 = this.getEntityManager().load(OperatorInfo.class, 4);
        operatorInfo4.setExpiryTime(DateUtils.now());
        this.assertSave(operatorInfo4, OperatorInfoException.ADVANCED_MODIFY);

        this.login(operatorInfo.getNo(), OperatorInfoPermissionProvider.ADVANCEDMODIFY);
        operatorInfo.setGroupId(operatorInfo.getGroupId() + 1);
        operatorInfo.setRoleId(operatorInfo.getRoleId() + 1);
        operatorInfo.setUseAble(operatorInfo.getUseAble() ? Boolean.FALSE : Boolean.TRUE);
        operatorInfo.setExpiryTime(DateUtils.now());
        this.assertSave(operatorInfo, null);
    }

    @Test
    public void testChangePassword() throws ServiceException {
        String oldPassword = "dangcat2014";
        OperatorInfoCreate operatorInfo = this.getEntityManager().load(OperatorInfoCreate.class, 0);
        operatorInfo.setPassword(SecurityUtils.storePassword(operatorInfo.getNo(), oldPassword));
        this.getEntityManager().save(operatorInfo);

        this.login(operatorInfo.getNo());
        this.changePassword(null, null, OperatorInfoCreate.Password, OperatorInfoException.INVALIDATE_NOTNULL);

        String newPassword = "dangcat2014";
        this.changePassword(oldPassword, null, OperatorInfoCreate.Password1, OperatorInfoException.INVALIDATE_NOTNULL);
        this.changePassword(oldPassword, newPassword, null, null);

        operatorInfo = this.getEntityManager().load(OperatorInfoCreate.class, 0);
        String password = SecurityUtils.storePassword(operatorInfo.getNo(), newPassword);
        Assert.assertTrue(SecurityUtils.isMatch(password, operatorInfo.getPassword()));
    }

    @Test
    public void testDefaultNew() throws ServiceException {
        OperatorInfo operatorInfo = new OperatorInfo();
        StaffConfig operatorInfoConfig = StaffConfig.getInstance();
        operatorInfo.setUseAble(operatorInfoConfig.getDefaultUseAble());
        if (operatorInfoConfig.getValidDays() != null && operatorInfoConfig.getValidDays() > 0) {
            Date expiryTime = DateUtils.add(DateUtils.DAY, DateUtils.now(), operatorInfoConfig.getValidDays());
            operatorInfo.setExpiryTime(expiryTime);
        }
        this.testDefaultNew(OperatorInfo.class, operatorInfo);
    }

    @Test
    public void testDelete() throws ServiceException {
        this.testDelete(OperatorInfo.class, TEST_COUNT);
    }

    /**
     * 只能删除本组和子组成员账号。
     */
    @Test
    public void testDeleteOtherGroup() throws ServiceException {
        OperatorInfo operatorInfo1 = this.getEntityManager().load(OperatorInfo.class, 1);
        OperatorInfo operatorInfo2 = this.getEntityManager().load(OperatorInfo.class, 2);
        Assert.assertNotNull(operatorInfo1);
        Assert.assertNotNull(operatorInfo2);
        this.login(operatorInfo1.getNo());
        this.assertDelete(operatorInfo2.getId(), OperatorInfoException.KILL_VALIDATEGROUP);
    }

    /**
     * 操作员不能删除自己的账号。
     */
    @Test
    public void testDeleteSelf() throws ServiceException {
        OperatorInfo operatorInfo = this.getEntityManager().load(OperatorInfo.class, 0);
        Assert.assertNotNull(operatorInfo);
        this.login(operatorInfo.getNo());
        this.assertDelete(operatorInfo.getId(), OperatorInfoException.KILL_HIMSELFUL);
    }

    @Test
    public void testFilter() throws ServiceException {
        TestServiceQuery<OperatorInfo, OperatorInfo, OperatorInfoFilter> testServiceQuery = new TestServiceQuery<OperatorInfo, OperatorInfo, OperatorInfoFilter>(this.getBusinessService());
        QueryAssert<OperatorInfoFilter> queryAssert = new QueryAssert<OperatorInfoFilter>(OperatorInfo.class);
        OperatorInfoFilter operatorInfoFilter = new OperatorInfoFilter();
        queryAssert.setDataFilter(operatorInfoFilter);

        OperatorGroupLoader operatorGroupLoader = new OperatorGroupLoader(this.getEntityManager());
        Integer[] groupIds = operatorGroupLoader.loadMemberIds();
        if (groupIds != null)
            operatorInfoFilter.setGroupIds(groupIds);

        this.testFilter_GroupId(testServiceQuery, queryAssert, operatorInfoFilter);
        this.testFilter_Name(testServiceQuery, queryAssert, operatorInfoFilter);
        this.testFilter_No(testServiceQuery, queryAssert, operatorInfoFilter);
        this.testFilter_UseAble(testServiceQuery, queryAssert, operatorInfoFilter);
    }

    private void testFilter_GroupId(TestServiceQuery<OperatorInfo, OperatorInfo, OperatorInfoFilter> testServiceQuery, QueryAssert<OperatorInfoFilter> queryAssert,
                                    OperatorInfoFilter operatorInfoFilter) throws ServiceException {
        for (Object entity : this.loadSamples(OperatorGroup.class)) {
            OperatorGroup operatorGroup = (OperatorGroup) entity;
            operatorInfoFilter.setGroupId(operatorGroup.getId());
            queryAssert.setExpectFilterExpress(new FilterUnit(OperatorInfo.GroupId, FilterType.eq, operatorGroup.getId()));
            this.testFilter(testServiceQuery, queryAssert);
        }
        operatorInfoFilter.setGroupId(null);
    }

    private void testFilter_Name(TestServiceQuery<OperatorInfo, OperatorInfo, OperatorInfoFilter> testServiceQuery, QueryAssert<OperatorInfoFilter> queryAssert, OperatorInfoFilter operatorInfoFilter)
            throws ServiceException {
        for (Object entity : this.loadSamples(OperatorInfo.class)) {
            OperatorInfo operatorInfo = (OperatorInfo) entity;
            operatorInfoFilter.setName(operatorInfo.getName());
            FilterGroup filterGroup = new FilterGroup();
            filterGroup.add(new FilterUnit(OperatorGroup.Name, FilterType.like, operatorInfo.getName()));
            if (operatorInfoFilter.getGroupIds() != null)
                filterGroup.add(new FilterUnit(OperatorInfo.GroupId, FilterType.eq, (Object[]) operatorInfoFilter.getGroupIds()));
            queryAssert.setExpectFilterExpress(filterGroup);
            this.testFilter(testServiceQuery, queryAssert);
        }
        operatorInfoFilter.setName(null);
    }

    private void testFilter_No(TestServiceQuery<OperatorInfo, OperatorInfo, OperatorInfoFilter> testServiceQuery, QueryAssert<OperatorInfoFilter> queryAssert, OperatorInfoFilter operatorInfoFilter)
            throws ServiceException {
        for (Object entity : this.loadSamples(OperatorInfo.class)) {
            OperatorInfo operatorInfo = (OperatorInfo) entity;
            operatorInfoFilter.setNo(operatorInfo.getNo());
            FilterGroup filterGroup = new FilterGroup();
            filterGroup.add(new FilterUnit(OperatorInfo.No, FilterType.like, operatorInfo.getNo()));
            if (operatorInfoFilter.getGroupIds() != null)
                filterGroup.add(new FilterUnit(OperatorInfo.GroupId, FilterType.eq, (Object[]) operatorInfoFilter.getGroupIds()));
            queryAssert.setExpectFilterExpress(filterGroup);
            this.testFilter(testServiceQuery, queryAssert);
        }
        operatorInfoFilter.setNo(null);
    }

    private void testFilter_UseAble(TestServiceQuery<OperatorInfo, OperatorInfo, OperatorInfoFilter> testServiceQuery, QueryAssert<OperatorInfoFilter> queryAssert,
                                    OperatorInfoFilter operatorInfoFilter) throws ServiceException {
        operatorInfoFilter.setUseAble(Boolean.TRUE);
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit(OperatorInfo.UseAble, FilterType.eq, Boolean.TRUE));
        if (operatorInfoFilter.getGroupIds() != null)
            filterGroup.add(new FilterUnit(OperatorInfo.GroupId, FilterType.eq, (Object[]) operatorInfoFilter.getGroupIds()));
        queryAssert.setExpectFilterExpress(filterGroup);
        this.testFilter(testServiceQuery, queryAssert);

        operatorInfoFilter.setUseAble(Boolean.FALSE);
        filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit(OperatorInfo.UseAble, FilterType.eq, Boolean.FALSE));
        if (operatorInfoFilter.getGroupIds() != null)
            filterGroup.add(new FilterUnit(OperatorInfo.GroupId, FilterType.eq, (Object[]) operatorInfoFilter.getGroupIds()));
        queryAssert.setExpectFilterExpress(filterGroup);
        this.testFilter(testServiceQuery, queryAssert);
        operatorInfoFilter.setUseAble(null);
    }

    /**
     * 只能新增本组和子组成员信息。
     *
     * @throws ServiceException
     */
    @Test
    public void testInsertOperator() throws ServiceException {
        this.truncate(OperatorInfo.class);
        // 建立三个组：1、无关联；2、登陆用户组；3、登陆用户子组。
        EntitySimulator operatorGroupSimulator = this.getEntitySimulator(OperatorGroup.class);
        OperatorGroup operatorGroup1 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 1);
        this.getEntityManager().save(operatorGroup1);
        OperatorGroup operatorGroup2 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 2);
        this.getEntityManager().save(operatorGroup2);
        OperatorGroup operatorGroup3 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 3);
        operatorGroup3.setParentId(operatorGroup2.getId());
        this.getEntityManager().save(operatorGroup3);

        // 建立登陆用户，用第二个组登陆。
        EntitySimulator operatorInfoSimulator = this.getEntitySimulator(OperatorInfoCreate.class);
        OperatorInfoCreate operatorInfo1 = (OperatorInfoCreate) operatorInfoSimulator.create(TEST_COUNT + 1);
        operatorInfo1.setGroupId(operatorGroup2.getId());
        this.getEntityManager().save(operatorInfo1);

        this.login(operatorInfo1.getNo());

        OperatorInfoCreate operatorInfo2 = (OperatorInfoCreate) operatorInfoSimulator.create(TEST_COUNT + 2);
        operatorInfo2.setPassword1(SecurityUtils.encryptContent(operatorInfo2.getPassword()));
        operatorInfo2.setPassword2(SecurityUtils.encryptContent(operatorInfo2.getPassword()));
        // 建立第一个账号，用第一个操作员组：返回错误。
        operatorInfo2.setGroupId(operatorGroup1.getId());
        this.getService().create(operatorInfo2);
        Assert.assertNotNull(operatorInfo2.findServiceException(OperatorInfoException.INSERT_VALIDATEGROUP));
        // 建立第二个账号，用第二个操作员组：可以新增。
        OperatorInfoCreate operatorInfo3 = (OperatorInfoCreate) operatorInfoSimulator.create(TEST_COUNT + 3);
        operatorInfo3.setGroupId(operatorGroup2.getId());
        operatorInfo3.setPassword1(SecurityUtils.encryptContent(operatorInfo3.getPassword()));
        operatorInfo3.setPassword2(SecurityUtils.encryptContent(operatorInfo3.getPassword()));
        this.getService().create(operatorInfo3);
        Assert.assertFalse(operatorInfo3.hasError());
        OperatorInfo saveOperatorInfo3 = this.getService().view(operatorInfo3.getId());
        Assert.assertNotNull(saveOperatorInfo3);
        Assert.assertTrue(SimulateUtils.compareData(saveOperatorInfo3, operatorInfo3));
        // 建立第三个账号，用第三个操作员组：可以新增。
        OperatorInfoCreate operatorInfo4 = (OperatorInfoCreate) operatorInfoSimulator.create(TEST_COUNT + 4);
        operatorInfo4.setGroupId(operatorGroup3.getId());
        operatorInfo4.setPassword1(SecurityUtils.encryptContent(operatorInfo4.getPassword()));
        operatorInfo4.setPassword2(SecurityUtils.encryptContent(operatorInfo4.getPassword()));
        this.getService().create(operatorInfo4);
        Assert.assertFalse(operatorInfo4.hasError());
        OperatorInfo saveOperatorInfo4 = this.getService().view(operatorInfo4.getId());
        Assert.assertNotNull(saveOperatorInfo4);
        Assert.assertTrue(SimulateUtils.compareData(saveOperatorInfo4, operatorInfo4));
    }

    /**
     * 只能修改本组和子组成员信息。
     */
    @Test
    public void testModifyOperator() throws ServiceException {
        this.truncate(OperatorInfo.class, OperatorGroup.class);
        // 建立三个组：1、无关联；2、登陆用户组；3、登陆用户子组。
        EntitySimulator operatorGroupSimulator = this.getEntitySimulator(OperatorGroup.class);
        OperatorGroup operatorGroup1 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 1);
        operatorGroup1.setParentId(null);
        this.getEntityManager().save(operatorGroup1);
        OperatorGroup operatorGroup2 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 2);
        operatorGroup2.setParentId(null);
        this.getEntityManager().save(operatorGroup2);
        OperatorGroup operatorGroup3 = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 3);
        operatorGroup3.setParentId(operatorGroup2.getId());
        this.getEntityManager().save(operatorGroup3);

        // 建立三个登陆账号。
        EntitySimulator operatorInfoSimulator = this.getEntitySimulator(OperatorInfo.class);
        // 第一个账号绑定第一个独立组。
        OperatorInfo operatorInfo1 = (OperatorInfo) operatorInfoSimulator.create(TEST_COUNT + 1);
        operatorInfo1.setGroupId(operatorGroup1.getId());
        this.getEntityManager().save(operatorInfo1);
        // 第二个账号绑定第二个组
        OperatorInfo operatorInfo2 = (OperatorInfo) operatorInfoSimulator.create(TEST_COUNT + 2);
        operatorInfo2.setGroupId(operatorGroup2.getId());
        this.getEntityManager().save(operatorInfo2);
        // 第三个账号绑定第三个组
        OperatorInfo operatorInfo3 = (OperatorInfo) operatorInfoSimulator.create(TEST_COUNT + 3);
        operatorInfo3.setGroupId(operatorGroup3.getId());
        this.getEntityManager().save(operatorInfo3);

        // 用第一个账号登录修改第三个账号：不是同组不能修改。
        this.login(operatorInfo1.getNo());
        this.getService().save(operatorInfo3);
        Assert.assertNotNull(operatorInfo3.findServiceException(OperatorInfoException.MODIFY_VALIDATEGROUP));

        // 用第三个账号登录修改第二个账号：不是同组可以修改。
        this.login(operatorInfo3.getNo());
        this.getService().save(operatorInfo2);
        Assert.assertNotNull(operatorInfo2.findServiceException(OperatorInfoException.MODIFY_VALIDATEGROUP));

        // 用第二个账号登录修改第三个账号：是同组可以修改子组账号。
        this.login(operatorInfo2.getNo());
        String saveNo = operatorInfo3.getNo();
        operatorInfoSimulator.modify(operatorInfo3, 4);
        operatorInfo3.setNo(saveNo);
        operatorInfo3.setGroupId(operatorGroup3.getId());
        this.getService().save(operatorInfo3);
        Assert.assertFalse(operatorInfo3.hasError());
        OperatorInfo saveOperatorInfo4 = this.getService().view(operatorInfo3.getId());
        Assert.assertNotNull(saveOperatorInfo4);
        Assert.assertTrue(SimulateUtils.compareData(saveOperatorInfo4, operatorInfo3));
    }

    /**
     * 测试修改密码。
     */
    @Test
    public void testModifyPassword() throws ServiceException {
        this.truncate(OperatorInfo.class);
        EntitySimulator operatorInfoSimulator = this.getEntitySimulator(OperatorInfoCreate.class);
        OperatorInfoCreate operatorInfo = (OperatorInfoCreate) operatorInfoSimulator.create(TEST_COUNT + 1);
        operatorInfo.setPassword1(null);
        operatorInfo.setPassword2("");
        this.getService().create(operatorInfo);
        DataValidateException dataValidateException = (DataValidateException) operatorInfo.findServiceException(OperatorInfoException.INVALIDATE_NOTNULL);
        Assert.assertNotNull(dataValidateException);
        Assert.assertEquals(OperatorInfoCreate.Password1, dataValidateException.getFieldName());

        operatorInfo.setPassword1(SecurityUtils.encryptContent("111111"));
        operatorInfo.setPassword2(SecurityUtils.encryptContent("111112"));
        this.getService().create(operatorInfo);
        OperatorInfoException operatorInfoException = (OperatorInfoException) operatorInfo.findServiceException(OperatorInfoException.PASSWORD_NOTEQUALS);
        Assert.assertNotNull(operatorInfoException);
        Assert.assertEquals(OperatorInfoCreate.Password2, operatorInfoException.getFieldName());
    }

    /**
     * 操作员的账号不能重复。
     */
    @Test
    public void testOperatorNoRepeat() throws ServiceException {
        OperatorInfo operatorInfo0 = this.getEntityManager().load(OperatorInfo.class, 0);
        Assert.assertNotNull(operatorInfo0);

        EntitySimulator operatorInfoSimulator = this.getEntitySimulator(OperatorInfo.class);
        OperatorInfoCreate operatorInfo1 = (OperatorInfoCreate) operatorInfoSimulator.create(TEST_COUNT + 1);
        operatorInfo1.setNo(operatorInfo0.getNo());
        this.getService().create(operatorInfo1);
        Assert.assertNotNull(operatorInfo1.findServiceException(OperatorInfoException.DATA_REPEAT));

        OperatorInfo operatorInfo2 = this.getEntityManager().load(OperatorInfo.class, 2);
        Assert.assertNotNull(operatorInfo2);
        operatorInfo2.setNo(operatorInfo0.getNo());
        this.getService().save(operatorInfo2);
        Assert.assertNotNull(operatorInfo2.findServiceException(OperatorInfoException.MODIFY_NO_DENY));
    }

    /**
     * 操作员组和角色不存在。
     */
    @Test
    public void testOperatorNotExist() throws ServiceException {
        this.truncate(OperatorInfo.class);
        EntitySimulator operatorInfoSimulator = this.getEntitySimulator(OperatorInfo.class);
        OperatorInfoCreate operatorInfo1 = (OperatorInfoCreate) operatorInfoSimulator.create(TEST_COUNT + 1);
        operatorInfo1.setGroupId(1000);
        this.getService().create(operatorInfo1);
        OperatorInfoException operatorInfoException = (OperatorInfoException) operatorInfo1.findServiceException(OperatorInfoException.FIELD_NOTEXISTS);
        Assert.assertNotNull(operatorInfoException);
        Assert.assertEquals(OperatorInfo.GroupId, operatorInfoException.getFieldName());

        operatorInfo1.setGroupId(0);
        operatorInfo1.setRoleId(1000);
        this.getService().create(operatorInfo1);
        operatorInfoException = (OperatorInfoException) operatorInfo1.findServiceException(OperatorInfoException.FIELD_NOTEXISTS);
        Assert.assertNotNull(operatorInfoException);
        Assert.assertEquals(OperatorInfo.RoleId, operatorInfoException.getFieldName());
    }

    @Test
    public void testQuery() throws ServiceException {
        TestServiceQuery<OperatorInfo, OperatorInfo, OperatorInfoFilter> testServiceQuery = new TestServiceQuery<OperatorInfo, OperatorInfo, OperatorInfoFilter>(this.getBusinessService());
        QueryAssert<OperatorInfoFilter> queryAssert = new QueryAssert<OperatorInfoFilter>(OperatorInfo.class);
        queryAssert.setDataFilter(new OperatorInfoFilter());
        queryAssert.setExpectTotaleSize(TEST_COUNT);
        this.testQuery(testServiceQuery, queryAssert);
    }

    /**
     * 只能查询本组和子组成员账号。
     */
    @Test
    public void testQuerySameGroup() throws ServiceException {
        this.truncate(OperatorInfo.class);

        int count = 4;
        List<OperatorGroup> operatorGroupList = new ArrayList<OperatorGroup>();
        List<OperatorInfo> operatorInfoList = new ArrayList<OperatorInfo>();
        EntitySimulator operatorGroupSimulator = this.getEntitySimulator(OperatorGroup.class);
        EntitySimulator operatorInfoSimulator = this.getEntitySimulator(OperatorInfo.class);
        for (int i = 0; i < count; i++) {
            OperatorGroup operatorGroup = (OperatorGroup) operatorGroupSimulator.create(TEST_COUNT + 1 + i);
            if (i == 0 || i == count - 1)
                operatorGroup.setParentId(null);
            else
                operatorGroup.setParentId(operatorGroupList.get(i - 1).getId());
            this.getEntityManager().save(operatorGroup);
            operatorGroupList.add(operatorGroup);

            OperatorInfo operatorInfo = (OperatorInfo) operatorInfoSimulator.create(TEST_COUNT + 1 + i);
            operatorInfo.setGroupId(operatorGroup.getId());
            this.getEntityManager().save(operatorInfo);
            operatorInfoList.add(operatorInfo);
        }

        for (int i = 0; i < count; i++) {
            OperatorInfo operatorInfo = operatorInfoList.get(i);
            this.login(operatorInfo.getNo());
            QueryResult<OperatorInfo> queryResult = this.getService().query(new OperatorInfoFilter());
            Assert.assertNotNull(queryResult);
            Assert.assertNotNull(queryResult.getData());
            if (i == count - 1)
                Assert.assertEquals(1, queryResult.getData().size());
            else
                Assert.assertEquals(count - i - 1, queryResult.getData().size());
        }
    }

    @Test
    public void testResetPassword() throws ServiceException {
        this.resetPassword(null, null, OperatorInfoCreate.No, OperatorInfoException.INVALIDATE_NOTNULL);

        String oldPassword = "dangcat2014";
        OperatorInfoCreate operatorInfo = this.getEntityManager().load(OperatorInfoCreate.class, 0);
        operatorInfo.setPassword(SecurityUtils.storePassword(operatorInfo.getNo(), oldPassword));
        this.getEntityManager().save(operatorInfo);

        String newPassword = "dangcat2014";
        this.resetPassword(operatorInfo.getNo(), null, OperatorInfoCreate.Password1, OperatorInfoException.INVALIDATE_NOTNULL);
        this.resetPassword(operatorInfo.getNo(), newPassword, null, null);

        operatorInfo = this.getEntityManager().load(OperatorInfoCreate.class, 0);
        String password = SecurityUtils.storePassword(operatorInfo.getNo(), newPassword);
        Assert.assertTrue(SecurityUtils.isMatch(password, operatorInfo.getPassword()));
    }

    @Test
    public void testSave() throws ServiceException {
        this.testSave(OperatorInfo.class, TEST_COUNT);
    }

    @Test
    public void testView() throws ServiceException {
        this.testView(OperatorInfo.class, TEST_COUNT);
    }
}
