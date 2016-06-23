package org.dangcat.boot.service;

import org.dangcat.persistence.batch.EntityBatchStorer;

/**
 * 数据批量操作服务。
 * @author dangcat
 * 
 */
public interface EntityBatchService
{
    /**
     * 清除所有待处理数据。
     */
    void clear();

    /**
     * 得到指定数据库的批量存储。
     * @return 批量操作对象。
     */
    EntityBatchStorer getEntityBatchStorer();

    /**
     * 得到指定数据库的批量存储。
     * @param databaseName 数据库名。
     * @return 批量操作对象。
     */
    EntityBatchStorer getEntityBatchStorer(String databaseName);

    /**
     * 调用批量存储操作。
     */
    void save();
}
