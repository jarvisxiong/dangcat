package org.dangcat.persistence.index;

import java.util.Collection;
import java.util.Date;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Rows;
import org.dangcat.persistence.model.Table;
import org.junit.Test;

public class TestTableIndexManager
{
    protected static final Logger logger = Logger.getLogger(TestTableIndexManager.class);
    private static final int TEST_COUNT = 100000;
    private Table table = null;

    private Table getTable()
    {
        if (this.table == null)
        {
            this.table = UserInfoUtils.getTable();
            UserInfoUtils.createData(this.table, TEST_COUNT);
            this.table.getRows().getIndexManager().appendIndex(ValueUtils.join(UserInfo.Name, UserInfo.Age));
        }
        return this.table;
    }

    @Test
    public void testFilterIndex()
    {
        Rows rows = this.getTable().getRows();
        // 注册时间+年龄定位
        String indexName = ValueUtils.join(UserInfo.RegisterTime, UserInfo.Age);
        rows.getIndexManager().appendIndex(indexName);

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
                indexer = rows.getIndexManager().findIndexer(filterGroup);
                Assert.assertEquals(indexName, indexer.getName());
            }

            long beginTime = DateUtils.currentTimeMillis();
            Collection<Row> rowList = rows.find(filterGroup);
            indexCostTime += DateUtils.currentTimeMillis() - beginTime;
            indexCount++;
            UserInfoUtils.assertRowList(rowList, index);
        }
        logger.info("Table filter " + indexName + " index avage cost time : " + indexCostTime / indexCount + "(ms)");
    }

    @Test
    public void testMultiNameIndex()
    {
        Rows rows = this.getTable().getRows();
        // 地址+名称定位
        String indexName = ValueUtils.join(UserInfo.Address, UserInfo.Name);
        String[] indexNames = { UserInfo.Address, UserInfo.Name };
        rows.getIndexManager().appendIndex(indexName);

        long indexCostTime = 0;
        long indexCount = 0;
        for (int index = 0; index < TEST_COUNT; index += 1000)
        {
            long beginTime = DateUtils.currentTimeMillis();
            Collection<Row> rowList = rows.find(indexNames, UserInfoUtils.getAddress(index), UserInfoUtils.getName(index));
            indexCostTime += DateUtils.currentTimeMillis() - beginTime;
            indexCount++;
            UserInfoUtils.assertRowList(rowList, index);
        }
        logger.info("Table " + indexName + " index avage cost time : " + indexCostTime / indexCount + "(ms)");
    }

    @Test
    public void testNameIndex()
    {
        Rows rows = this.getTable().getRows();
        // 名称定位
        rows.getIndexManager().appendIndex(UserInfo.Name);

        long indexCostTime = 0;
        long indexCount = 0;
        for (int index = 0; index < TEST_COUNT; index += 1000)
        {
            long beginTime = DateUtils.currentTimeMillis();
            Collection<Row> rowList = rows.find(new String[] { UserInfo.Name }, UserInfoUtils.getName(index));
            indexCostTime += DateUtils.currentTimeMillis() - beginTime;
            indexCount++;
            UserInfoUtils.assertRowList(rowList, index);
        }
        logger.info("Table name index avage cost time : " + indexCostTime / indexCount + "(ms)");
    }

    @Test
    public void testPrimaryKeyIndex()
    {
        Rows rows = this.getTable().getRows();
        // 主键定位
        rows.getIndexManager().appendIndex(UserInfo.Id, true);

        long indexCostTime = 0;
        long indexCount = 0;
        for (int index = 0; index < TEST_COUNT; index += 1000)
        {
            long beginTime = DateUtils.currentTimeMillis();
            Row row = rows.find(index);
            indexCostTime += DateUtils.currentTimeMillis() - beginTime;
            indexCount++;
            UserInfoUtils.assertRowData(row, index);
        }
        logger.info("Table id index avage cost time : " + indexCostTime / indexCount + "(ms)");
    }

    @Test
    public void testRangeFilterIndex()
    {
        Rows rows = this.getTable().getRows();
        // 注册时间+年龄定位
        String indexName = ValueUtils.join(UserInfo.RegisterTime, UserInfo.Address, UserInfo.Name);
        rows.getIndexManager().appendIndex(indexName);

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
                indexer = rows.getIndexManager().findIndexer(filterGroup);
                Assert.assertEquals(indexName, indexer.getName());
            }

            long beginTime = DateUtils.currentTimeMillis();
            Collection<Row> rowList = rows.find(filterGroup);
            long indexCostTime = DateUtils.currentTimeMillis() - beginTime;
            indexTotalCostTime += indexCostTime;
            indexTotalCount++;

            beginTime = DateUtils.currentTimeMillis();
            int count = 0;
            for (Row row : this.getTable().getRows())
            {
                if (filterGroup.isValid(row))
                    count++;
            }
            long seqCostTime = DateUtils.currentTimeMillis() - beginTime;
            logger.info("Index cost time: " + indexCostTime + ", Seq find cost time : " + seqCostTime + ", count : " + count + ", 1 : " + seqCostTime / (indexCostTime == 0 ? 1 : indexCostTime));
            Assert.assertEquals(count, rowList.size());
        }
        logger.info("Table range filter " + indexName + " index avage cost time : " + indexTotalCostTime / indexTotalCount + "(ms)");
    }
}
