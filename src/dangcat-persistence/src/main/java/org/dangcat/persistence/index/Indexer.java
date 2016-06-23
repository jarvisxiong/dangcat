package org.dangcat.persistence.index;

import java.util.*;

class Indexer
{
    private String[] indexFieldNames;
    private Map<Object, IndexLeaf> indexLeafMap = Collections.synchronizedMap(new HashMap<Object, IndexLeaf>());
    private IndexNode indexNode = null;
    private String name;

    Indexer(String name)
    {
        this.name = name;
        this.indexFieldNames = name.split(";");
        this.reset();
    }

    protected void add(Object data)
    {
        IndexLeaf indexLeaf = this.indexNode.addData(this.indexFieldNames, 0, data);
        if (indexLeaf != null)
            this.indexLeafMap.put(data, indexLeaf);
    }

    protected int checkHitRate(Set<String> fieldNameSet)
    {
        int hitRate = 0;
        for (String indexFieldName : this.indexFieldNames)
        {
            if (!fieldNameSet.contains(indexFieldName))
                break;
            hitRate++;
        }
        return hitRate;
    }

    protected String[] getIndexFieldNames()
    {
        return this.indexFieldNames;
    }

    protected String getName()
    {
        return this.name;
    }

    private boolean isIndexField(String fieldName)
    {
        for (String indexFieldName : this.indexFieldNames)
        {
            if (indexFieldName.equalsIgnoreCase(fieldName))
                return true;
        }
        return false;
    }

    protected boolean remove(Object data)
    {
        IndexLeaf indexLeaf = null;
        indexLeaf = this.indexLeafMap.get(data);
        if (indexLeaf != null)
        {
            indexLeaf.removeData(data);
            this.indexLeafMap.remove(data);
        }

        return indexLeaf != null;
    }

    protected void reset()
    {
        this.indexNode = new IndexNode(this.indexFieldNames[0]);
    }

    public <T> void search(Collection<T> dataList, Map<String, FilterComparable> indexFilterMap)
    {
        this.indexNode.search(dataList, indexFilterMap);
    }

    protected void update(Object data, String fieldName)
    {
        if (fieldName == null || this.isIndexField(fieldName))
        {
            this.remove(data);
            this.add(data);
        }
    }
}
