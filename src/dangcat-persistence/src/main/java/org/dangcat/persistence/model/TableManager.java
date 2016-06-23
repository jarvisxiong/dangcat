package org.dangcat.persistence.model;

import org.dangcat.persistence.exception.TableException;

/**
 * 表管理器。
 * @author dangcat
 * 
 */
public interface TableManager
{
    /**
     * 在当前的数据源中构建数据表。
     * @param table 表对象。
     * @return 建表结果。
     * @throws TableException 运行异常。
     */
    int create(Table table) throws TableException;

    /**
     * 删除指定表的数据。
     * @param table 表对象。
     * @return 删除数量。
     * @throws TableException 运行异常。
     */
    int delete(Table table) throws TableException;

    /**
     * 删除指定的表。
     * @param tableName 表名。
     * @return 删除结果。
     * @throws TableException 运行异常。
     */
    int drop(String tableName) throws TableException;

    /**
     * 删除指定的表。
     * @param table 表对象。
     * @return 删除结果。
     * @throws TableException 运行异常。
     */
    int drop(Table table) throws TableException;

    /**
     * 执行表的SQL语句。
     * @param table 表对象。
     * @return 执行结果。
     * @throws TableException 运行异常。
     */
    int execute(Table table) throws TableException;

    /**
     * 判断表是否存在。
     * @param tableName 表名。
     * @return 是否存在。
     */
    boolean exists(String tableName);

    /**
     * 判断表是否存在。
     * @param table 表对象。
     * @return 是否存在。
     */
    boolean exists(Table table);

    /**
     * 载入指定表的数据。
     * @param table 表对象。
     * @throws TableException 运行异常。
     */
    void load(Table table) throws TableException;

    /**
     * 载入元数据内容。
     * @param table 表对象。
     * @throws TableException 运行异常。
     */
    void loadMetaData(Table table) throws TableException;

    /**
     * 存储指定表的数据。
     * @param table 表对象。
     * @throws TableException 运行异常。
     */
    void save(Table table) throws TableException;

    /**
     * 清除指定表数据。
     * @param tableName 表名称。
     * @return 清除结果。
     * @throws TableException 运行异常。
     */
    int truncate(String tableName) throws TableException;

    /**
     * 清除指定表数据。
     * @param table 表对象。
     * @return 清除结果。
     * @throws TableException 运行异常。
     */
    int truncate(Table table) throws TableException;

}
