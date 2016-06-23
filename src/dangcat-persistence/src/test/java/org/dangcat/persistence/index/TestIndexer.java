package org.dangcat.persistence.index;

import junit.framework.Assert;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.junit.Test;

import java.util.*;

public class TestIndexer {
    @Test
    public void testIndexDelete() {
        Indexer indexer = new Indexer(ValueUtils.join(UserInfo.Name, UserInfo.Age));
        List<UserInfo> entityList = new ArrayList<UserInfo>();
        UserInfoUtils.createData(entityList, 100);
        for (UserInfo userInfo : entityList)
            indexer.add(userInfo);

        UserInfo userInfo = new UserInfo();
        userInfo.setId(11);
        UserInfoUtils.createData(userInfo, 11);
        indexer.remove(userInfo);

        Map<String, FilterComparable> indexFilterMap = new HashMap<String, FilterComparable>();
        FilterComparable nameComparable = new FilterComparable();
        nameComparable.add(new FilterUnit(UserInfo.Name, FilterType.eq, userInfo.getName()));
        indexFilterMap.put(UserInfo.Name, nameComparable);
        FilterComparable ageComparable = new FilterComparable();
        ageComparable.add(new FilterUnit(UserInfo.Age, FilterType.eq, userInfo.getAge()));
        indexFilterMap.put(UserInfo.Age, ageComparable);

        List<UserInfo> dataList = new LinkedList<UserInfo>();
        indexer.search(dataList, indexFilterMap);

        Assert.assertEquals(0, dataList.size());
    }

    private void testIndexFind(String indexName, int count, FilterComparable filterComparable, int expected, int assertIndex) {
        Indexer indexer = new Indexer(indexName);
        List<UserInfo> entityList = new ArrayList<UserInfo>();
        UserInfoUtils.createData(entityList, count);
        for (UserInfo userInfo : entityList)
            indexer.add(userInfo);

        List<UserInfo> dataList = new LinkedList<UserInfo>();
        Map<String, FilterComparable> indexFilterMap = new HashMap<String, FilterComparable>();
        indexFilterMap.put(indexName, filterComparable);
        indexer.search(dataList, indexFilterMap);
        Assert.assertEquals(expected, dataList.size());
        UserInfoUtils.assertEntityList(dataList, assertIndex);
    }

    @Test
    public void testIndexFindBetween() {
        FilterComparable filterComparable = new FilterComparable();
        filterComparable.add(new FilterUnit(null, FilterType.between, 10, 19));
        this.testIndexFind(UserInfo.Id, 100, filterComparable, 10, 10);
    }

    @Test
    public void testIndexFindEq() {
        FilterComparable filterComparable = new FilterComparable();
        filterComparable.add(new FilterUnit(null, FilterType.eq, 56));
        this.testIndexFind(UserInfo.Id, 100, filterComparable, 1, 56);
    }

    @Test
    public void testIndexFindGt() {
        FilterComparable filterComparable = new FilterComparable();
        filterComparable.add(new FilterUnit(null, FilterType.gt, 80));
        this.testIndexFind(UserInfo.Id, 100, filterComparable, 19, 81);
    }

    @Test
    public void testIndexFindLt() {
        FilterComparable filterComparable = new FilterComparable();
        filterComparable.add(new FilterUnit(null, FilterType.lt, 15));
        this.testIndexFind(UserInfo.Id, 100, filterComparable, 15, 0);
    }

    @Test
    public void testIndexRepeatKey() {
        Indexer indexer = new Indexer(UserInfo.Id);
        List<UserInfo> entityList = new ArrayList<UserInfo>();
        UserInfoUtils.createData(entityList, 100);
        for (UserInfo userInfo : entityList) {
            userInfo.setId(0);
            indexer.add(userInfo);
        }

        FilterComparable filterComparable = new FilterComparable();
        filterComparable.add(new FilterUnit(null, FilterType.ge, 0));

        List<UserInfo> dataList = new LinkedList<UserInfo>();
        Map<String, FilterComparable> indexFilterMap = new HashMap<String, FilterComparable>();
        indexFilterMap.put(UserInfo.Id, filterComparable);
        indexer.search(dataList, indexFilterMap);

        Assert.assertEquals(1, dataList.size());
        UserInfoUtils.assertEntityList(dataList, 0);
    }

    @Test
    public void testIndexUpdate() {
        Indexer indexer = new Indexer(ValueUtils.join(UserInfo.Name, UserInfo.Age));
        List<UserInfo> entityList = new ArrayList<UserInfo>();
        UserInfoUtils.createData(entityList, 100);
        for (UserInfo userInfo : entityList)
            indexer.add(userInfo);

        UserInfo userInfo10 = entityList.get(10);
        UserInfo userInfo20 = entityList.get(20);
        userInfo20.setName(userInfo10.getName());
        userInfo20.setAge(userInfo10.getAge());
        indexer.update(userInfo20, null);

        Map<String, FilterComparable> indexFilterMap = new HashMap<String, FilterComparable>();
        FilterComparable nameComparable = new FilterComparable();
        nameComparable.add(new FilterUnit(UserInfo.Name, FilterType.eq, userInfo10.getName()));
        indexFilterMap.put(UserInfo.Name, nameComparable);
        FilterComparable ageComparable = new FilterComparable();
        ageComparable.add(new FilterUnit(UserInfo.Age, FilterType.eq, userInfo10.getAge()));
        indexFilterMap.put(UserInfo.Age, ageComparable);

        List<UserInfo> dataList = new LinkedList<UserInfo>();
        indexer.search(dataList, indexFilterMap);

        Assert.assertEquals(2, dataList.size());
        for (UserInfo userInfo : dataList) {
            Assert.assertEquals(UserInfoUtils.getName(10), userInfo.getName());
            Assert.assertEquals(UserInfoUtils.getAge(10), userInfo.getAge());
        }
    }
}