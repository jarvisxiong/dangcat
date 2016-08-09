package org.dangcat.persistence.cache;

import org.dangcat.persistence.filter.FilterExpress;

import java.util.Collection;

/**
 * 实体缓存。
 *
 * @param <T>
 * @author dangcat
 */
public interface EntityCache<T> extends MemCache<T> {
    /**
     * 删除指定条件的缓存数据和数据库数据。
     */
    void delete(FilterExpress filterExpress);

    /**
     * 删除缓存数据。
     */
    void delete(T data);

    /**
     * 按照指定的条件在索引上查找数据。
     *
     * @param filterExpress 索引条件。
     * @return 数据集合。
     */
    Collection<T> load(FilterExpress filterExpress);

    /**
     * 根据主键值找到记录行。
     *
     * @param params 主键参数值。
     * @return 找到的数据行。
     */
    T load(Object... params);

    /**
     * 按照指定的字段值查找数据。
     *
     * @param fieldNames 字段名，多字段以分号间隔。
     * @param values     字段数值，必须与字段对应。
     * @return 找到的记录行。
     */
    Collection<T> load(String[] fieldNames, Object... values);

    /**
     * 加载缓存数据。
     */
    void loadData();

    /**
     * 刷新内存数据。
     *
     * @param data 目标数据。
     * @return 刷新后数据。
     */
    T refresh(T data);

    /**
     * 刷新指定主键的内存数据。
     *
     * @param primaryKeys 目标主键数据。
     * @return 刷新后数据。
     */
    T refreshEntity(Object... primaryKeys);

    /**
     * 添加缓存数据。
     */
    void save(T data);
}
