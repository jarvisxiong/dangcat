package org.dangcat.persistence.entity;

import java.util.List;

import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.orm.Session;

/**
 * 实体管理器。
 * @author dangcat
 * 
 */
public interface EntityManager
{
    /**
     * 开始事务。
     */
    public Session beginTransaction();

    /**
     * 提交事务。
     */
    public void commit();

    /**
     * 删除指定过滤条件的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     * @return 是否删除。
     * @throws EntityException
     */
    public <T> int delete(Class<T> entityClass, FilterExpress filterExpress) throws EntityException;

    /**
     * 删除指定主键的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param primaryKeyValues 主键值。
     * @return 是否删除。
     * @throws EntityException
     */
    public <T> int delete(Class<T> entityClass, Object... primaryKeyValues) throws EntityException;

    /**
     * 删除指定属性的的实体。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param fieldNames 字段名列表。
     * @param values 属性值。
     * @return 是否删除。
     * @throws EntityException
     */
    public <T> int delete(Class<T> entityClass, String[] fieldNames, Object... values) throws EntityException;

    /**
     * 删除指定的实体对象。
     * @param <T> 实体类型。
     * @param deleteEntityContext 删除实体上下文。
     * @return 是否删除。
     * @throws EntityException
     */
    public int delete(DeleteEntityContext deleteEntityContext) throws EntityException;

    /**
     * 删除指定的实体对象。
     * @param <T> 实体类型。
     * @param entites 删除实体对象。
     * @return 是否删除。
     * @throws EntityException
     */
    public int delete(Object... entites) throws EntityException;

    /**
     * 载入指定类型的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @return 查询结果。
     */
    public <T> List<T> load(Class<T> entityClass) throws EntityException;

    /**
     * 按照过滤条件载入指定类型的实体。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     * @return 查询结果。
     */
    public <T> List<T> load(Class<T> entityClass, FilterExpress filterExpress) throws EntityException;

    /**
     * 按照过滤条件、范围载入指定类型的实体。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     * @param range 载入范围。
     * @return 查询结果。
     */
    public <T> List<T> load(Class<T> entityClass, FilterExpress filterExpress, Range range) throws EntityException;

    /**
     * 按照过滤条件、范围、排序条件载入指定类型的实体。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     * @param range 载入范围。
     * @param orderBy 排序条件。
     * @return 查询结果。
     */
    public <T> List<T> load(Class<T> entityClass, FilterExpress filterExpress, Range range, OrderBy orderBy) throws EntityException;

    /**
     * 找到指定主键的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param primaryKeys 主键参数。
     * @return 找到的实体对象。
     * @return 查询结果。
     */
    public <T> T load(Class<T> entityClass, Object... primaryKeyValues) throws EntityException;

    /**
     * 找到指定属性的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param fieldNames 字段名列表。
     * @param values 属性值。
     * @return 找到的实体对象。
     */
    public <T> List<T> load(Class<T> entityClass, String[] fieldNames, Object... values) throws EntityException;

    /**
     * 载入指定类型的实体对象。
     * @param <T> 实体类型。
     * @param loadEntityContext 载入实体上下文。
     * @return 查询结果。
     */
    public <T> List<T> load(LoadEntityContext loadEntityContext) throws EntityException;;

    /**
     * 由数据库刷新实体实例内容。
     * @param <T>
     * @param entity 实体对象。
     * @throws EntityException 操作异常。
     */
    public <T> T refresh(T entity) throws EntityException;

    /**
     * 回滚事务。
     */
    public void rollback();

    /**
     * 保存指定实体对象。
     * @param <T> 实体类型。
     * @param entities 保存实体对象。
     * @return 保存结果。
     */
    public void save(Object... entities) throws EntityException;

    /**
     * 保存指定实体对象。
     * @param <T> 实体类型。
     * @param saveEntityContext 保存实体上下文。
     * @param entities 保存实体对象。
     * @return 保存结果。
     */
    public void save(SaveEntityContext saveEntityContext, Object... entities) throws EntityException;
}
