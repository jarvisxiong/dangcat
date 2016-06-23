package org.dangcat.business.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import junit.framework.Assert;

import org.dangcat.business.service.BusinessService;
import org.dangcat.business.service.DataFilter;
import org.dangcat.business.service.QueryResult;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.entity.EntityCalculator;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.EntityManagerFactory;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.simulate.SimulateUtils;

public class TestServiceQuery<Q extends EntityBase, T extends EntityBase, K extends DataFilter>
{
    private BusinessService<Q, T, K> businessService = null;
    private DataFilter dataFilter = null;
    private QueryResult<Q> queryResult = null;
    private Map<Number, String> valueMap = null;

    public TestServiceQuery(BusinessService<Q, T, K> businessService)
    {
        this.businessService = businessService;
    }

    public void assertFilterResult(QueryAssert<K> queryAssert)
    {
        Assert.assertNotNull(this.queryResult);
        Collection<Q> actualCollection = this.queryResult == null ? null : this.queryResult.getData();
        Collection<Object> expectCollection = null;
        FilterExpress filterExpress = queryAssert.getExceptFilterExpress();
        if (filterExpress != null)
        {
            EntityManager entityManager = EntityManagerFactory.getInstance().open();
            Collection<?> entityCollection = entityManager.load(queryAssert.getClassType());
            if (entityCollection != null)
            {
                for (Object entity : entityCollection)
                {
                    if (filterExpress.isValid(entity))
                    {
                        if (expectCollection == null)
                            expectCollection = new HashSet<Object>();
                        expectCollection.add(entity);
                    }
                }
                if (expectCollection != null)
                    EntityCalculator.calculate(expectCollection);
            }
        }
        if (expectCollection == null)
        {
            if (actualCollection != null)
                Assert.assertEquals(0, actualCollection.size());
        }
        else
        {
            Assert.assertNotNull(actualCollection);
            Assert.assertTrue(SimulateUtils.compareDataCollection(expectCollection, actualCollection));
        }
    }

    public void assertQueryResult(QueryAssert<K> queryAssert)
    {
        Assert.assertNotNull(this.queryResult);
        Assert.assertEquals(queryAssert.getExpectStartRow(), this.queryResult.getStartRow());
        Assert.assertEquals(queryAssert.getExpectTotaleSize(), this.queryResult.getTotalSize());

        Collection<?> actualCollection = this.getResult().getData();
        Assert.assertNotNull(actualCollection);
        Assert.assertEquals(queryAssert.getExpectPageSize(), new Integer(actualCollection.size()));
        if (this.getValueMap() != null)
            Assert.assertEquals(queryAssert.getExpectPageSize(), new Integer(this.getValueMap().size()));

        if (queryAssert.getExceptFilterExpress() != null)
        {
            EntityManager entityManager = EntityManagerFactory.getInstance().open();
            Collection<?> expectCollection = entityManager.load(queryAssert.getClassType(), queryAssert.getExceptFilterExpress());
            EntityCalculator.calculate(expectCollection);
            Assert.assertTrue(SimulateUtils.compareDataCollection(expectCollection, actualCollection));
        }
    }

    public DataFilter getDataFilter()
    {
        return this.dataFilter;
    }

    public QueryResult<?> getResult()
    {
        return this.queryResult;
    }

    public Map<Number, String> getValueMap()
    {
        return this.valueMap;
    }

    public void query(K dataFilter) throws ServiceException
    {
        this.queryResult = this.businessService.query(dataFilter);
        this.valueMap = this.businessService.selectMap(dataFilter);
        this.dataFilter = dataFilter;
    }
}
