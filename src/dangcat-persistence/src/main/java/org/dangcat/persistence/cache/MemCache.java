package org.dangcat.persistence.cache;

import org.dangcat.persistence.filter.FilterExpress;

import java.util.Collection;

/**
 * 内存数据缓存。
 *
 * @param <T>
 * @author dangcat
 */
public interface MemCache<T> {
    /**
     * 添加缓存数据。
     */
    void add(T entity);

    /**
     * 添加缓存数据。
     */
    void addEntities(Object... entities);

    /**
     * 清除缓存。
     */
    void clear(boolean force);

    /**
     * 按照指定的条件在索引上查找数据。
     *
     * @param filterExpress 索引条件。
     * @return 数据集合。
     */
    Collection<T> find(FilterExpress filterExpress);

    /**
     * 按照指定的字段值查找数据。
     *
     * @param fieldNames 字段名，多字段以分号间隔。
     * @param values     字段数值，必须与字段对应。
     * @return 找到的记录行。
     */
    Collection<T> find(String[] fieldNames, Object... values);

    /**
     * 读取所有缓存数据。
     */
    Collection<T> getDataCollection();

    /**
     * 产生的索引的数量。
     */
    int getIndexSize();

    /**
     * 根据主键值找到记录行。
     *
     * @param params 主键参数值。
     * @return 找到的数据行。
     */
    T locate(Object... params);

    /**
     * 数据变化通知修改索引。
     *
     * @param entities 被修改的记录对像。
     */
    void modifyEntities(Object... entities);

    /**
     * 删除指定条件的缓存数据。
     */
    Collection<T> remove(FilterExpress filterExpress);

    /**
     * 删除缓存数据。
     */
    boolean remove(T data);

    /**
     * 删除缓存数据。
     */
    int removeEntities(Object... entities);

    /**
     * 删除指定主键的缓存数据。
     */
    T removeEntity(Object... primaryKeys);

    /**
     * 包含的数据数量。
     */
    int size();
}
