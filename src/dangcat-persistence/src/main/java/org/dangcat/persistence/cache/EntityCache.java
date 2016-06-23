package org.dangcat.persistence.cache;

import java.util.Collection;

import org.dangcat.persistence.filter.FilterExpress;

/**
 * 实体缓存。
 * @author dangcat
 * 
 * @param <T>
 */
public interface EntityCache<T> extends MemCache<T>
{
    /**
     * 删除指定条件的缓存数据和数据库数据。
     */
    public void delete(FilterExpress filterExpress);

    /**
     * 删除缓存数据。
     */
    public void delete(T data);

    /**
     * 按照指定的条件在索引上查找数据。
     * @param filterExpress 索引条件。
     * @return 数据集合。
     */
    public Collection<T> load(FilterExpress filterExpress);

    /**
     * 根据主键值找到记录行。
     * @param params 主键参数值。
     * @return 找到的数据行。
     */
    public T load(Object... params);

    /**
     * 按照指定的字段值查找数据。
     * @param fieldNames 字段名，多字段以分号间隔。
     * @param values 字段数值，必须与字段对应。
     * @return 找到的记录行。
     */
    public Collection<T> load(String[] fieldNames, Object... values);

    /**
     * 加载缓存数据。
     */
    public void loadData();

    /**
     * 刷新内存数据。
     * @param data 目标数据。
     * @return 刷新后数据。
     */
    public T refresh(T data);

    /**
     * 刷新指定主键的内存数据。
     * @param primaryKeys 目标主键数据。
     * @return 刷新后数据。
     */
    public T refreshEntity(Object... primaryKeys);

    /**
     * 添加缓存数据。
     */
    public void save(T data);
}
