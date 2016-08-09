package org.dangcat.business.service;

import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.EntityBase;

import java.util.Map;

/**
 * 业务服务接口。
 *
 * @param <V>
 * @author dangcat
 */
public interface BusinessService<Q extends EntityBase, V extends EntityBase, F extends DataFilter> {
    String QUERY_TABLENAME = "Q";
    String VIEW_TABLENAME = "V";

    /**
     * 保存参数配置。
     */
    EntityBase config(EntityBase config) throws ServiceException;

    /**
     * 新增实体数据。
     *
     * @param entity 实体对象。
     * @return 运行结果。
     */
    V create(V entity) throws ServiceException;

    /**
     * 删除指定条件的数据。
     *
     * @param id 主键。
     * @return 执行结果。
     */
    boolean delete(Integer id) throws ServiceException;

    /**
     * 判断是否有权限。
     *
     * @param permissionId 权限码。
     * @return 是否有指定权限。
     */
    boolean hasPermission(Integer permissionId);

    /**
     * 查询指定条件的数据。
     *
     * @param dataFilter 查询范围。
     * @return 查询结果。
     */
    QueryResult<Q> query(F dataFilter) throws ServiceException;

    /**
     * 保存实体数据。
     *
     * @param entity 实体对象。
     * @return 运行结果。
     */
    V save(V entity) throws ServiceException;

    /**
     * 查询指定条件的列表。
     *
     * @param dataFilter 查询范围。
     * @return 查询结果。
     */
    Map<Number, String> selectMap(F dataFilter) throws ServiceException;

    /**
     * 查询指定主键的数据。
     *
     * @param id 主键值。
     * @return 查询结果。
     */
    V view(Integer id) throws ServiceException;
}
