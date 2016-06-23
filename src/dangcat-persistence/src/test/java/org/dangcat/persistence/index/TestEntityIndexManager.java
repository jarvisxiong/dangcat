package org.dangcat.persistence.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.junit.Test;

public class TestEntityIndexManager
{
    private static IndexManager<UserInfo> indexManager = null;
    protected static final Logger logger = Logger.getLogger(TestEntityIndexManager.class);
    private static final int TEST_COUNT = 100000;

    private IndexManager<UserInfo> getIndexManager()
    {
        if (indexManager == null)
        {
            List<UserInfo> userInfoList = new ArrayList<UserInfo>();
            UserInfoUtils.createData(userInfoList, TEST_COUNT);
            indexManager = new IndexManager<UserInfo>(userInfoList);
            indexManager.appendIndex(ValueUtils.join(UserInfo.Name, UserInfo.Age));
        }
        return indexManager;
    }

    @Test
    public void testFilterIndex()
    {
        IndexManager<UserInfo> indexManager = this.getIndexManager();
        // 注册时间+年龄定位
        String indexName = ValueUtils.join(UserInfo.RegisterTime, UserInfo.Age);
        indexManager.appendIndex(indexName);

        Indexer indexer = null;
        long indexCostTime = 0;
        long indexCount = 0;
        for (int index = 0; index < TEST_COUNT; index += 1000)
        {
            FilterGroup filterGroup = new FilterGroup();
            filterGroup.add(UserInfo.RegisterTime, FilterType.eq, UserInfoUtils.getRegisterTime(index));
            filterGroup.add(UserInfo.Age, FilterType.eq, UserInfoUtils.getAge(index));
            filterGroup.add(UserInfo.Name, FilterType.eq, UserInfoUtils.getName(index));

            if (indexer == null)
            {
                indexer = indexManager.findIndexer(filterGroup);
                Assert.assertEquals(indexName, indexer.getName());
            }

            long beginTime = DateUtils.currentTimeMillis();
            Collection<UserInfo> entityList = indexManager.find(filterGroup);
            indexCostTime += DateUtils.currentTimeMillis() - beginTime;
            indexCount++;
            UserInfoUtils.assertEntityList(entityList, index);
        }
        logger.info("Entity filter " + indexName + " index avage cost time : " + indexCostTime / indexCount + "(ms)");
    }

    @Test
    public void testMultiNameIndex()
    {
        IndexManager<UserInfo> indexManager = this.getIndexManager();
        // 地址+名称定位
        String indexName = ValueUtils.join(UserInfo.Address, UserInfo.Name);
        indexManager.appendIndex(indexName);
        String[] indexNames = { UserInfo.Address, UserInfo.Name };

        long indexCostTime = 0;
        long indexCount = 0;
        for (int index = 0; index < TEST_COUNT; index += 1000)
        {
            long beginTime = DateUtils.currentTimeMillis();
            Collection<UserInfo> entityList = indexManager.find(indexNames, UserInfoUtils.getAddress(index), UserInfoUtils.getName(index));
            indexCostTime += DateUtils.currentTimeMillis() - beginTime;
            indexCount++;
            UserInfoUtils.assertEntityList(entityList, index);
        }
        logger.info("Entity " + indexName + " index avage cost time : " + indexCostTime / indexCount + "(ms)");
    }

    @Test
    public void testNameIndex()
    {
        IndexManager<UserInfo> indexManager = this.getIndexManager();
        // 名称定位
        indexManager.appendIndex(UserInfo.Name);

        long indexCostTime = 0;
        long indexCount = 0;
        for (int index = 0; index < TEST_COUNT; index += 1000)
        {
            long beginTime = DateUtils.currentTimeMillis();
            Collection<UserInfo> entityList = indexManager.find(new String[] { UserInfo.Name }, UserInfoUtils.getName(index));
            indexCostTime += DateUtils.currentTimeMillis() - beginTime;
            indexCount++;
            UserInfoUtils.assertEntityList(entityList, index);
        }
        logger.info("Entity name index avage cost time : " + indexCostTime / indexCount + "(ms)");
    }

    @Test
    public void testPrimaryKeyIndex()
    {
        IndexManager<UserInfo> indexManager = this.getIndexManager();
        // 主键定位
        indexManager.appendIndex(UserInfo.Id, true);

        long indexCostTime = 0;
        long indexCount = 0;
        for (int index = 0; index < TEST_COUNT; index += 1000)
        {
            long beginTime = DateUtils.currentTimeMillis();
            UserInfo userInfo = indexManager.find(index);
            indexCostTime += DateUtils.currentTimeMillis() - beginTime;
            indexCount++;
            UserInfoUtils.assertEntityData(userInfo, index);
        }
        logger.info("Entity id index avage cost time : " + indexCostTime / indexCount + "(ms)");
    }

    @Test
    public void testRangeFilterIndex()
    {
        IndexManager<UserInfo> indexManager = this.getIndexManager();
        indexManager.clear();
        // 注册时间+年龄定位
        String indexName = ValueUtils.join(UserInfo.RegisterTime, UserInfo.Address, UserInfo.Name);
        indexManager.appendIndex(indexName);

        Indexer indexer = null;
        long indexTotalCostTime = 0;
        long indexTotalCount = 0;
        for (int index = 0; index < TEST_COUNT; index += 10000)
        {
            FilterGroup filterGroup = new FilterGroup();
            Date fromRegisterTime = UserInfoUtils.getRegisterTime(index + 4);
            Date toRegisterTime = UserInfoUtils.getRegisterTime(index + 4);
            filterGroup.add(UserInfo.RegisterTime, FilterType.between, fromRegisterTime, toRegisterTime);
            filterGroup.add(UserInfo.Address, FilterType.ge, UserInfoUtils.getAddress(index + 4));
            filterGroup.add(UserInfo.Address, FilterType.le, UserInfoUtils.getAddress(index + 4));
            filterGroup.add(UserInfo.Name, FilterType.like, UserInfoUtils.getName(4));

            if (indexer == null)
            {
                indexer = indexManager.findIndexer(filterGroup);
                Assert.assertEquals(indexName, indexer.getName());
            }

            long beginTime = DateUtils.currentTimeMillis();
            Collection<UserInfo> entityList = indexManager.find(filterGroup);
            long indexCostTime = DateUtils.currentTimeMillis() - beginTime;
            indexTotalCostTime += indexCostTime;
            indexTotalCount++;

            beginTime = DateUtils.currentTimeMillis();
            int count = 0;
            for (UserInfo userInfo : indexManager.getDataCollection())
            {
                if (filterGroup.isValid(userInfo))
                    count++;
            }
            long seqCostTime = DateUtils.currentTimeMillis() - beginTime;
            logger.info("Index cost time: " + indexCostTime + ", Seq find cost time : " + seqCostTime + ", 1 : " + seqCostTime / (indexCostTime == 0 ? 1 : indexCostTime));
            Assert.assertEquals(count, entityList.size());
        }
        logger.info("Table range filter " + indexName + " index avage cost time : " + indexTotalCostTime / indexTotalCount + "(ms)");
    }
}
