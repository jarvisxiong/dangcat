package org.dangcat.boot.service;

import org.dangcat.framework.exception.ServiceException;

/**
 * 数据处理流程服务接口。
 *
 * @param <T>
 * @author dangcat
 */
public interface DataProcessService<T> {
    /**
     * 处理数据接口。
     *
     * @param data 数据对象。
     * @throws ServiceException 处理异常。
     */
    void process(T data) throws ServiceException;
}
