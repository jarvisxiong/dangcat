package org.dangcat.persistence.cache;

import java.util.Collection;

/**
 * 数据更新通知。
 */
public interface EntityUpdateNotifier {
    /**
     * 通知更新数据。
     *
     * @param tableName          表名。
     * @param deletedPrimaryKeys 被删除的主键集合。
     * @param updateEntities     更新的数据。
     */
    void notifyUpdate(String tableName, Collection<Object> deletedPrimaryKeys, Collection<Object> updateEntities);
}
