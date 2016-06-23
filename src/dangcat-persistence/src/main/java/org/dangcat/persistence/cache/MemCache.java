package org.dangcat.persistence.cache;

import java.util.Collection;

import org.dangcat.persistence.filter.FilterExpress;

/**
 * 内存数据缓存。
 * @author dangcat
 * 
 * @param <T>
 */
public interface MemCache<T>
{
    /**
     * 添加缓存数据。
     */
    public void add(T entity);

    /**
     * 添加缓存数据。
     */
    public void addEntities(Object... entities);

    /**
     * 清除缓存。
     */
    public void clear(boolean force);

    /**
     * 按照指定的条件在索引上查找数据。
     * @param filterExpress 索引条件。
     * @return 数据集合。
     */
    public Collection<T> find(FilterExpress filterExpress);

    /**
     * 按照指定的字段值查找数据。
     * @param fieldNames 字段名，多字段以分号间隔。
     * @param values 字段数值，必须与字段对应。
     * @return 找到的记录行。
     */
    public Collection<T> find(String[] fieldNames, Object... values);

    /**
     * 读取所有缓存数据。
     */
    public Collection<T> getDataCollection();

    /**
     * 产生的索引的数量。
     */
    public int getIndexSize();

    /**
     * 根据主键值找到记录行。
     * @param params 主键参数值。
     * @return 找到的数据行。
     */
    public T locate(Object... params);

    /**
     * 数据变化通知修改索引。
     * @param entities 被修改的记录对像。
     */
    public void modifyEntities(Object... entities);

    /**
     * 删除指定条件的缓存数据。
     */
    public Collection<T> remove(FilterExpress filterExpress);

    /**
     * 删除缓存数据。
     */
    public boolean remove(T data);

    /**
     * 删除缓存数据。
     */
    public int removeEntities(Object... entities);

    /**
     * 删除指定主键的缓存数据。
     */
    public T removeEntity(Object... primaryKeys);

    /**
     * 包含的数据数量。
     */
    public int size();
}
