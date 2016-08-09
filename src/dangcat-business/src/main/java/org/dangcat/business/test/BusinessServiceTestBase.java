package org.dangcat.business.test;

import junit.framework.Assert;
import org.dangcat.boot.ConfigureReader;
import org.dangcat.boot.service.impl.ServiceCalculator;
import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.business.service.BusinessService;
import org.dangcat.business.service.DataFilter;
import org.dangcat.business.service.impl.BusinessServiceBase;
import org.dangcat.commons.reflect.GenericUtils;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.*;
import org.dangcat.framework.service.annotation.Service;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.dangcat.persistence.entity.*;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.simulate.DatabaseSimulator;
import org.dangcat.persistence.simulate.EntitySimulator;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.dangcat.persistence.validator.LogicValidatorUtils;
import org.junit.After;
import org.junit.Before;

import java.util.*;

public abstract class BusinessServiceTestBase<S, Q extends EntityBase, T extends EntityBase, K extends DataFilter> extends MainServiceBase {
    private static final int MODULE_ID = 1;
    private static final int SERVICE_ID = 1;
    private static final String SESSIONID = "SESSIONID";
    private DatabaseSimulator databaseSimulator = null;
    private Locale locale = Environment.getDefaultLocale();
    @Service
    private S service = null;
    private ServiceContext serviceContext = null;
    private ServiceInfo serviceInfo = null;
    private ServicePrincipal servicePrincipal = null;

    public BusinessServiceTestBase() {
        super(ServiceFactory.createInstance(null));
    }

    protected void addService(Class<?> accessClassType, Class<?> serviceClassType) {
        if (this.getService(accessClassType) == null) {
            Object service = ReflectUtils.newInstance(serviceClassType, new Class<?>[]{ServiceProvider.class}, new Object[]{this});
            if (service != null) {
                // 服务工厂
                ServiceLocator serviceLocator = this.getService(ServiceLocator.class);
                if (serviceLocator == null) {
                    ServiceFactory serviceFactory = ServiceFactory.getInstance();
                    this.addService(ServiceLocator.class, serviceFactory);
                    this.addService(ServiceFactory.class, serviceFactory);
                }

                this.addService(accessClassType, service);
                if (service instanceof ServiceBase)
                    ((ServiceBase) service).initialize();
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void assertDelete(Integer id, Integer exceptionId) throws ServiceException {
        try {
            BusinessServiceBase<Q, T, K> businessServiceBase = (BusinessServiceBase<Q, T, K>) this.getService();
            businessServiceBase.delete(id);
            if (exceptionId != null)
                throw new ServiceException("delete entity error.");
            else {
                T entity = businessServiceBase.view(id);
                Assert.assertNull(entity);
            }
        } catch (ServiceException e) {
            if (exceptionId != null && !exceptionId.equals(e.getMessageId()))
                this.logger.error(exceptionId, e);
            Assert.assertEquals(exceptionId, e.getMessageId());
        }
    }

    @SuppressWarnings("unchecked")
    protected void assertSave(T entity, Integer exceptionId) throws ServiceException {
        try {
            BusinessServiceBase<Q, T, K> businessServiceBase = (BusinessServiceBase<Q, T, K>) this.getService();
            businessServiceBase.save(entity);
            if (exceptionId != null) {
                if (entity.findServiceException(exceptionId) == null)
                    throw new ServiceException("save entity error.");
            } else
                Assert.assertFalse(entity.hasError());
        } catch (ServiceException e) {
            if (exceptionId != null && !exceptionId.equals(e.getMessageId()))
                this.logger.error(exceptionId, e);
            Assert.assertEquals(exceptionId, e.getMessageId());
        }
    }

    protected void disableExtendEvent() {
        System.setProperty("business.extendevent.enabled", "false");
    }

    protected void enabledExtendEvent() {
        System.setProperty("business.extendevent.enabled", "");
    }

    @SuppressWarnings("unchecked")
    protected BusinessService<Q, T, K> getBusinessService() {
        return (BusinessService<Q, T, K>) this.service;
    }

    public DatabaseSimulator getDatabaseSimulator() {
        return this.databaseSimulator;
    }

    protected EntityManager getEntityManager() {
        return EntityManagerFactory.getInstance().open();
    }

    protected EntitySimulator getEntitySimulator(Class<?> classType) {
        String tableName = null;
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
        if (entityMetaData != null)
            tableName = entityMetaData.getTable().getTableName().getName();
        return (EntitySimulator) this.databaseSimulator.getSimulateData(tableName).getDataSimulator();
    }

    protected int getIndexOffSet() {
        return 0;
    }

    protected Locale getLocale() {
        return this.locale;
    }

    protected void setLocale(Locale locale) {
        this.locale = locale;
    }

    protected S getService() {
        return this.service;
    }

    protected ServiceContext getServiceContext() {
        if (this.serviceContext == null)
            this.serviceContext = new ServiceContext(SESSIONID, this.getServicePrincipal(), this.getLocale());
        return this.serviceContext;
    }

    protected ServiceInfo getServiceInfo() {
        if (this.serviceInfo == null) {
            Map<String, Class<?>> genericClassMap = GenericUtils.getClassGenericInfo(this.getClass());
            this.serviceInfo = ServiceFactory.getInstance().getServiceInfo(genericClassMap.get("S"));
            ServiceCalculator.createPermissionValues(this.serviceInfo, MODULE_ID, SERVICE_ID);
        }
        return this.serviceInfo;
    }

    protected ServicePrincipal getServicePrincipal() {
        if (this.servicePrincipal == null)
            this.servicePrincipal = new ServicePrincipal("admin", "学习邓稼先", "SBEH", "127.0.0.1", null, null);
        return this.servicePrincipal;
    }

    private void initDatabase() {
        if (this.databaseSimulator == null) {
            Environment.setHomePath(this.getClass());
            SimulateUtils.configure();

            DatabaseSimulator databaseSimulator = new DatabaseSimulator();
            this.initDatabaseSimulator(databaseSimulator);
            this.databaseSimulator = databaseSimulator;

            super.initialize();
        }
        this.databaseSimulator.initDatabase();
    }

    protected void initDatabaseSimulator(DatabaseSimulator databaseSimulator) {
    }

    @Override
    public void initialize() {
        LogicValidatorUtils.disable();
        System.setProperty(ConfigureReader.KEY_SYSTEM_ID, "1");
        this.initDatabase();

        ServiceHelper.inject(this, this);
    }

    protected Object[] loadSamples(Class<?> classType) {
        List<?> entityList = this.getEntityManager().load(classType);
        Assert.assertNotNull(entityList);
        return new Object[]{entityList.get(0), entityList.get(entityList.size() / 2), entityList.get(entityList.size() - 1)};
    }

    @Before
    public void login() {
        ServiceContext.set(this.getServiceContext());
    }

    protected void login(String no, Integer... permissionIds) {
        Collection<Integer> permissions = null;
        if (permissionIds != null && permissionIds.length > 0) {
            permissions = new HashSet<Integer>();
            for (Integer permissionId : permissionIds)
                permissions.add(ServiceCalculator.create(MODULE_ID, SERVICE_ID, permissionId));
        }

        this.servicePrincipal = new ServicePrincipal(no, no, "SBEH", "127.0.0.1", null, permissions);
        this.serviceContext = new ServiceContext(SESSIONID, this.getServicePrincipal(), this.getLocale());
        if (this.getServiceInfo() != null)
            this.serviceContext.addParam(ServiceInfo.class, this.getServiceInfo());
        this.login();
    }

    @After
    public void logout() {
        ServiceContext.remove();
        this.servicePrincipal = null;
        this.serviceContext = null;
    }

    protected void testDefaultNew(Class<?> classType, Object expectEntity) throws ServiceException {
        EntityBase entity = this.getBusinessService().view(null);
        Assert.assertNotNull(entity);
        Assert.assertTrue(SimulateUtils.compareData(expectEntity, entity));
    }

    protected void testDelete(Class<?> classType, int size) throws ServiceException {
        int indexOffSet = this.getIndexOffSet();
        this.disableExtendEvent();
        Integer[] ids = new Integer[]{indexOffSet, size / 2 + indexOffSet, size - 1 + indexOffSet};
        for (Integer id : ids) {
            Assert.assertTrue(this.getBusinessService().delete(id));
            Object entity = this.getEntityManager().load(classType, id);
            Assert.assertNull(entity);
        }

        try {
            this.getBusinessService().delete(size);
        } catch (ServiceException e) {
            Assert.assertEquals(BusinessException.DATA_NOTFOUND, e.getMessageId());
        }
        this.enabledExtendEvent();
    }

    protected void testFilter(TestServiceQuery<Q, T, K> testServiceQuery, QueryAssert<K> queryAssert) throws ServiceException {
        this.disableExtendEvent();
        testServiceQuery.query(queryAssert.getDataFilter());
        testServiceQuery.assertFilterResult(queryAssert);
        this.enabledExtendEvent();
    }

    private void testQuery(TestServiceQuery<Q, T, K> testServiceQuery, Integer startRow, Integer pageSize, QueryAssert<K> queryAssert) throws ServiceException {
        this.disableExtendEvent();
        queryAssert.getDataFilter().setStartRow(startRow);
        queryAssert.getDataFilter().setPageSize(pageSize);
        testServiceQuery.query(queryAssert.getDataFilter());
        testServiceQuery.assertQueryResult(queryAssert);
        this.enabledExtendEvent();
    }

    protected void testQuery(TestServiceQuery<Q, T, K> testServiceQuery, QueryAssert<K> queryAssert) throws ServiceException {
        this.testQuery(testServiceQuery, "Id", queryAssert);
    }

    protected void testQuery(TestServiceQuery<Q, T, K> testServiceQuery, String idFieldName, QueryAssert<K> queryAssert) throws ServiceException {
        int indexOffSet = this.getIndexOffSet();
        int pageSize = 10;
        queryAssert.setExpectPageSize(pageSize);
        // 测试首页查询。
        int startRow = 1;
        queryAssert.setExpectStartRow(startRow);
        queryAssert.setExpectFilterExpress(new FilterUnit(idFieldName, FilterType.between, indexOffSet, 9 + indexOffSet));
        this.testQuery(testServiceQuery, startRow, pageSize, queryAssert);
        // 测试第二页查询。
        startRow = 11;
        queryAssert.setExpectStartRow(startRow);
        queryAssert.setExpectFilterExpress(new FilterUnit(idFieldName, FilterType.between, 10 + indexOffSet, 19 + indexOffSet));
        this.testQuery(testServiceQuery, startRow, pageSize, queryAssert);
        // 测试超过总页查询。
        startRow = queryAssert.getExpectTotaleSize() + 1;
        queryAssert.setExpectStartRow(queryAssert.getExpectTotaleSize() - pageSize + 1);
        queryAssert.setExpectFilterExpress(new FilterUnit(idFieldName, FilterType.between, queryAssert.getExpectTotaleSize() - 10 + indexOffSet, queryAssert.getExpectTotaleSize() - 1 + indexOffSet));
        this.testQuery(testServiceQuery, startRow, pageSize, queryAssert);
        // 测试余页查询。
        pageSize = 30;
        startRow = 91;
        queryAssert.setExpectStartRow(startRow);
        queryAssert.setExpectPageSize(10);
        queryAssert.setExpectFilterExpress(new FilterUnit(idFieldName, FilterType.between, queryAssert.getExpectTotaleSize() - 10 + indexOffSet, queryAssert.getExpectTotaleSize() - 1 + indexOffSet));
        this.testQuery(testServiceQuery, startRow, pageSize, queryAssert);
        // 测试超大页查询。
        pageSize = queryAssert.getExpectTotaleSize() * 10;
        startRow = 1;
        queryAssert.setExpectStartRow(startRow);
        queryAssert.setExpectPageSize(queryAssert.getExpectTotaleSize());
        queryAssert.setExpectFilterExpress(new FilterUnit(idFieldName, FilterType.between, indexOffSet, queryAssert.getExpectTotaleSize() - 1 + indexOffSet));
        this.testQuery(testServiceQuery, startRow, pageSize, queryAssert);
    }

    protected void testSave(Class<T> classType, int size) throws ServiceException {
        int indexOffSet = this.getIndexOffSet();
        this.disableExtendEvent();
        EntitySimulator entitySimulator = this.getEntitySimulator(classType);
        Integer[] ids = new Integer[]{indexOffSet, size / 2 + indexOffSet, size - 1 + indexOffSet};
        for (Integer id : ids) {
            T expectEntity = this.getEntityManager().load(classType, id);
            entitySimulator.modify(expectEntity, size - id - 1);
            this.getBusinessService().save(expectEntity);
            Assert.assertFalse(expectEntity.hasError());

            T actualEntity = this.getEntityManager().load(classType, id);
            EntityCalculator.calculate(actualEntity);
            Assert.assertTrue(SimulateUtils.compareData(expectEntity, actualEntity));
        }
        this.enabledExtendEvent();
    }

    protected void testView(Class<?> classType, int size) throws ServiceException {
        int indexOffSet = this.getIndexOffSet();
        Integer[] ids = new Integer[]{indexOffSet, size / 2 + indexOffSet, size - 1 + indexOffSet};
        for (Integer id : ids) {
            Object expectEntity = this.getEntityManager().load(classType, id);
            EntityCalculator.calculate(expectEntity);
            EntityBase actualEntity = this.getBusinessService().view(id);
            Assert.assertNotNull(actualEntity);
            Assert.assertTrue(SimulateUtils.compareData(expectEntity, actualEntity));
        }

        try {
            this.getBusinessService().view(size);
        } catch (ServiceException e) {
            Assert.assertEquals(BusinessException.DATA_NOTFOUND, e.getMessageId());
        }
    }

    protected void truncate(Class<?>... classTypes) {
        if (classTypes != null && classTypes.length > 0) {
            for (Class<?> classType : classTypes) {
                EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
                Table table = entityMetaData.getTable();
                if (table.exists())
                    table.truncate();
            }
        }
    }
}
