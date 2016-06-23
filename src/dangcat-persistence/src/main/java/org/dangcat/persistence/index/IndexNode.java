package org.dangcat.persistence.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.dangcat.persistence.filter.FilterUtils;

class IndexNode extends Node
{
    private BinaryTree<Object, Node> binaryTree = new BinaryTree<Object, Node>(new ValueComparator());

    IndexNode(IndexNode parent, String fieldName, Object nodeValue)
    {
        super(parent, fieldName, nodeValue);
    }

    IndexNode(String fieldName)
    {
        super(null, fieldName, null);
    }

    protected IndexLeaf addData(String[] indexNames, int deepth, Object data)
    {
        IndexLeaf indexLeaf = null;
        if (deepth < indexNames.length - 1)
            indexLeaf = this.addIndexNode(indexNames, deepth, data);
        else
            indexLeaf = this.addIndexLeaf(indexNames, deepth, data);
        return indexLeaf;
    }

    private IndexLeaf addIndexLeaf(String[] indexNames, int deepth, Object data)
    {
        IndexLeaf indexLeaf = null;
        synchronized (this.binaryTree)
        {
            Object key = this.getKey(data);
            indexLeaf = (IndexLeaf) this.getValue(key);
            if (indexLeaf == null)
            {
                indexLeaf = new IndexLeaf(this, indexNames[deepth], key);
                this.binaryTree.put(key, indexLeaf);
            }
        }
        indexLeaf.addData(data);
        return indexLeaf;
    }

    private IndexLeaf addIndexNode(String[] indexNames, int deepth, Object data)
    {
        IndexNode indexNode = null;
        synchronized (this.binaryTree)
        {
            Object key = this.getKey(data);
            indexNode = (IndexNode) this.getValue(key);
            if (indexNode == null)
            {
                indexNode = new IndexNode(this, indexNames[deepth], key);
                this.binaryTree.put(key, indexNode);
            }
        }
        return indexNode.addData(indexNames, deepth + 1, data);
    }

    protected Object[] getEntries(FilterComparable filterComparable)
    {
        Object[] entries = null;
        synchronized (this.binaryTree)
        {
            if (filterComparable != null)
                entries = filterComparable.find(this.binaryTree);
            else
                entries = this.binaryTree.getEntities().toArray();
        }
        return entries;
    }

    protected Object getKey(Object data)
    {
        Object value = FilterUtils.getValue(data, this.getFieldName());
        return value == null ? NULL : value;
    }

    private Object getValue(Object key)
    {
        return this.binaryTree.get(key);
    }

    protected void removeNode(Node node, Object data)
    {
        synchronized (this.binaryTree)
        {
            this.binaryTree.remove(node.getNodeValue());
            if (this.getParent() != null && this.size() == 0)
                this.getParent().removeNode(this, data);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> void search(Collection<T> dataCollection, Map<String, FilterComparable> indexFilterMap)
    {
        FilterComparable filterComparable = indexFilterMap.get(this.getFieldName());
        if (filterComparable == null)
            indexFilterMap = Collections.emptyMap();

        for (Object entryObject : this.getEntries(filterComparable))
        {
            Entry<Object, Node> entry = (Entry<Object, Node>) entryObject;
            if (filterComparable == null || filterComparable.isValid(entry.key))
                entry.getValue().search(dataCollection, indexFilterMap);
        }
    }

    protected int size()
    {
        return this.binaryTree.size();
    }
}
