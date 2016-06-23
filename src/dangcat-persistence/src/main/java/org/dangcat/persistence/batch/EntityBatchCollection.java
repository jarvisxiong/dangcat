package org.dangcat.persistence.batch;

import org.dangcat.persistence.entity.SaveEntityContext;
import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.DataStatus;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 批量存储数据集合。
 *
 * @author dangcat
 */
class EntityBatchCollection {
    private Collection<Object> deletedCollection = new LinkedHashSet<Object>();
    private Collection<Object> saveCollection = new LinkedHashSet<Object>();

    protected void clear() {
        this.deletedCollection.clear();
        this.saveCollection.clear();
    }

    protected void delete(Object entity) {
        if (entity != null) {
            this.saveCollection.remove(entity);
            this.deletedCollection.add(entity);
        }
    }

    private int getSize(Collection<Object> collection) {
        return collection == null ? 0 : collection.size();
    }

    protected void save(Object entity) {
        if (entity != null) {
            if (entity instanceof DataStatus) {
                DataStatus dataStatus = (DataStatus) entity;
                if (DataState.Deleted.equals(dataStatus.getDataState())) {
                    this.delete(entity);
                    return;
                }

                if (!DataState.Insert.equals(dataStatus.getDataState()))
                    dataStatus.setDataState(DataState.Modified);
            }
            this.saveCollection.add(entity);
        }
    }

    protected void save(SaveEntityContext saveEntityContext) {
        if (this.size() > 0) {
            for (Object entity : this.deletedCollection)
                saveEntityContext.delete(entity);

            for (Object entity : this.saveCollection)
                saveEntityContext.save(entity);
        }
    }

    protected int size() {
        int size = 0;
        size += this.getSize(this.deletedCollection);
        size += this.getSize(this.saveCollection);
        return size;
    }
}
