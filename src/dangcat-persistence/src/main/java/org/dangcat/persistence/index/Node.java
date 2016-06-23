package org.dangcat.persistence.index;

import java.util.Collection;
import java.util.Map;

abstract class Node
{
    protected static final NullObject NULL = new NullObject();
    private String fieldName;
    private Object nodeValue = null;
    private IndexNode parent = null;

    Node()
    {
    }

    Node(IndexNode parent, String fieldName, Object nodeValue)
    {
        this.parent = parent;
        this.fieldName = fieldName;
        this.nodeValue = nodeValue;
    }

    protected String getFieldName()
    {
        return fieldName;
    }

    protected Object getNodeValue()
    {
        return nodeValue;
    }

    protected IndexNode getParent()
    {
        return this.parent;
    }

    protected abstract <T> void search(Collection<T> dataCollection, Map<String, FilterComparable> indexFilterMap);

    protected abstract int size();
}
