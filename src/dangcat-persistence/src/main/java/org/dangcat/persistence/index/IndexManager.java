package org.dangcat.persistence.index;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.filter.*;

import java.util.*;

/**
 * 索引管理器。
 *
 * @param <T>
 * @author dangcat
 */
public class IndexManager<T> {
    private Collection<T> dataCollection = null;
    private Map<String, Indexer> indexerMap = Collections.synchronizedMap(new HashMap<String, Indexer>());
    private boolean isEmbedCache = false;
    private Indexer primaryIndexer = null;

    /**
     * 构建索引管理器。
     *
     * @param dataList 数据集合列表。
     */
    public IndexManager() {
        this.dataCollection = Collections.synchronizedCollection(new HashSet<T>());
        this.isEmbedCache = true;
    }

    /**
     * 构建索引管理器。
     *
     * @param dataList 数据集合列表。
     */
    public IndexManager(Collection<T> dataCollectoin) {
        this.dataCollection = Collections.synchronizedCollection(dataCollectoin);
    }

    /**
     * 添加数据。
     *
     * @param data
     */
    public void add(T data) {
        if (data != null) {
            Map<String, Indexer> indexerMap = this.indexerMap;
            for (Indexer indexer : indexerMap.values())
                indexer.add(data);
            if (this.isEmbedCache)
                this.dataCollection.add(data);
        }
    }

    private void appendFilterExpress(Map<String, FilterComparable> indexFilterMap, FilterUnit filterUnit) {
        FilterComparable filterComparable = indexFilterMap.get(filterUnit.getFieldName());
        if (filterComparable == null) {
            filterComparable = new FilterComparable();
            indexFilterMap.put(filterUnit.getFieldName(), filterComparable);
        }
        filterComparable.add(filterUnit);
    }

    /**
     * 添加索引。
     *
     * @param indexName 索引名。
     */
    public void appendIndex(String indexName) {
        this.appendIndex(indexName, false);
    }

    /**
     * 添加索引。
     *
     * @param indexName    索引名。
     * @param isPrimaryKey 是否是主键索引。
     */
    public void appendIndex(String indexName, boolean isPrimaryKey) {
        Map<String, Indexer> indexerMap = this.indexerMap;
        if (!ValueUtils.isEmpty(indexName) && !indexerMap.containsKey(indexName)) {
            Indexer indexer = new Indexer(indexName);
            indexerMap.put(indexName, indexer);
            if (isPrimaryKey)
                this.primaryIndexer = indexer;
            this.rebuild(indexName);
        }
    }

    /**
     * 清除索引。
     */
    public void clear() {
        this.indexerMap = Collections.synchronizedMap(new HashMap<String, Indexer>());
        if (this.isEmbedCache)
            this.dataCollection = Collections.synchronizedCollection(new HashSet<T>());
    }

    private Map<String, FilterComparable> create(FilterExpress filterExpress) {
        Map<String, FilterComparable> indexFilterMap = new TreeMap<String, FilterComparable>(String.CASE_INSENSITIVE_ORDER);
        this.findIndexFilterMap(indexFilterMap, filterExpress);
        return indexFilterMap;
    }

    /**
     * 将指定的字段名和数值产生过滤条件。
     *
     * @param fieldNames 字段名。
     * @param values     数值。
     * @return 过滤条件。
     */
    private FilterExpress createFilterExpress(String[] fieldNames, Object... values) {
        FilterExpress filterExpress = null;
        if (fieldNames.length == 1)
            filterExpress = new FilterUnit(fieldNames[0], FilterType.eq, values[0]);
        else {
            FilterGroup filterGroup = new FilterGroup();
            for (int i = 0; i < fieldNames.length; i++)
                filterGroup.add(new FilterUnit(fieldNames[i], FilterType.eq, values[i]));
            filterExpress = filterGroup;
        }
        return filterExpress;
    }

    /**
     * 按照指定的条件在索引上查找数据。
     *
     * @param filterExpress 索引条件。
     * @return 数据集合。
     */
    public Collection<T> find(FilterExpress filterExpress) {
        Collection<T> dataCollection = this.getDataCollection();
        if (dataCollection == null || dataCollection.size() == 0)
            return null;

        Map<String, FilterComparable> indexFilterMap = this.create(filterExpress);
        Indexer indexer = this.findIndexer(indexFilterMap);
        Collection<T> sourceCollection = new HashSet<T>();
        if (indexer != null)
            indexer.search(sourceCollection, indexFilterMap);
        else
            sourceCollection.addAll(dataCollection);
        Collection<T> findCollection = null;
        for (T data : sourceCollection) {
            if (filterExpress.isValid(data)) {
                if (findCollection == null)
                    findCollection = new HashSet<T>();
                findCollection.add(data);
            }
        }
        return findCollection;
    }

    /**
     * 根据主键值找到记录行。
     *
     * @param params 主键参数值。
     * @return 找到的数据行。
     */
    public T find(Object... params) {
        T data = null;
        if (this.primaryIndexer != null) {
            Collection<T> dataCollection = this.find(this.primaryIndexer.getIndexFieldNames(), params);
            if (dataCollection != null && dataCollection.size() > 0)
                data = dataCollection.iterator().next();
        }
        return data;
    }

    /**
     * 按照指定的字段值查找数据。
     *
     * @param fieldNames 字段名，多字段以分号间隔。
     * @param values     字段数值，必须与字段对应。
     * @return 找到的记录行。
     */
    public Collection<T> find(String[] fieldNames, Object... values) {
        Collection<T> findCollection = null;
        if (fieldNames.length > 0 && fieldNames.length == values.length) {
            FilterExpress filterExpress = this.createFilterExpress(fieldNames, values);
            if (filterExpress != null)
                findCollection = this.find(filterExpress);
        }
        return findCollection;
    }

    /**
     * 测试过滤选择索引用。
     */
    protected Indexer findIndexer(FilterExpress filterExpress) {
        Map<String, FilterComparable> indexFilterMap = this.create(filterExpress);
        return this.findIndexer(indexFilterMap);
    }

    private Indexer findIndexer(Map<String, FilterComparable> indexFilterMap) {
        Indexer findIndexer = null;
        int findhitRate = 0;
        Map<String, Indexer> indexerMap = this.indexerMap;
        for (Indexer indexer : indexerMap.values()) {
            int hitRate = indexer.checkHitRate(indexFilterMap.keySet());
            if (hitRate > findhitRate) {
                findIndexer = indexer;
                findhitRate = hitRate;
            }
        }
        return findIndexer;
    }

    private void findIndexFilterMap(Map<String, FilterComparable> indexFilterMap, FilterExpress filterExpress) {
        if (filterExpress instanceof FilterUnit)
            this.appendFilterExpress(indexFilterMap, (FilterUnit) filterExpress);
        else {
            FilterGroup filterGroup = (FilterGroup) filterExpress;
            if (FilterGroupType.and.equals(filterGroup.getGroupType()) || filterGroup.getFilterExpressList().size() == 1) {
                for (FilterExpress childFilterExpress : filterGroup.getFilterExpressList())
                    this.findIndexFilterMap(indexFilterMap, childFilterExpress);
            }
        }
    }

    public Collection<T> getDataCollection() {
        return this.dataCollection;
    }

    public Set<String> getIndexNameSet() {
        return this.indexerMap.keySet();
    }

    public String getPrimaryKeyIndex() {
        return this.primaryIndexer != null ? this.primaryIndexer.getName() : null;
    }

    /**
     * 重建索引。
     */
    public void rebuild() {
        Map<String, Indexer> indexerMap = this.indexerMap;
        for (String indexName : indexerMap.keySet())
            this.rebuild(indexName);
    }

    /**
     * 重建指定的索引。
     */
    public void rebuild(String indexName) {
        Map<String, Indexer> indexerMap = this.indexerMap;
        Indexer indexer = indexerMap.get(indexName);
        if (indexer != null) {
            indexer.reset();
            Collection<T> dataCollection = this.getDataCollection();
            if (dataCollection != null) {
                synchronized (dataCollection) {
                    for (T data : dataCollection)
                        indexer.add(data);
                }
            }
        }
    }

    /**
     * 删除指定的记录数。
     *
     * @param data 记录对象。
     */
    public boolean remove(T data) {
        boolean result = false;
        if (data != null) {
            Map<String, Indexer> indexerMap = this.indexerMap;
            for (Indexer indexer : indexerMap.values())
                result = indexer.remove(data);
            if (this.isEmbedCache)
                result = this.dataCollection.remove(data);
        }
        return result;
    }

    /**
     * 数据变化通知修改索引。
     *
     * @param fieldName 字段名。
     * @param data      被修改的记录对像。
     */
    public void update(String fieldName, T data) {
        if (data != null) {
            Map<String, Indexer> indexerMap = this.indexerMap;
            for (Indexer indexer : indexerMap.values())
                indexer.update(data, fieldName);
            if (this.isEmbedCache)
                this.dataCollection.add(data);
        }
    }
}
