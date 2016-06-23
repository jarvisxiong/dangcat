package org.dangcat.persistence.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

class IndexLeaf extends Node {
    private Collection<Object> dataCollection = Collections.synchronizedCollection(new HashSet<Object>());

    IndexLeaf(IndexNode parent, String fieldName, Object nodeValue) {
        super(parent, fieldName, nodeValue);
    }

    protected void addData(Object data) {
        this.dataCollection.add(data);
    }

    protected boolean removeData(Object data) {
        boolean result = this.dataCollection.remove(data);
        if (result && this.getParent() != null && this.dataCollection.size() == 0)
            this.getParent().removeNode(this, data);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> void search(Collection<T> findDataCollection, Map<String, FilterComparable> indexFilterMap) {
        synchronized (this.dataCollection) {
            for (Object data : this.dataCollection)
                findDataCollection.add((T) data);
        }
    }

    @Override
    protected int size() {
        return this.dataCollection.size();
    }
}
