package org.dangcat.net.process.service;

import org.dangcat.framework.exception.ServiceException;

/**
 * 数据处理前流程服务接口。
 *
 * @param <T>
 * @author dangcat
 */
public interface ErrorDataProcess<T> {
    /**
     * 处理数据接口。
     *
     * @param data             数据对象。
     * @param ServiceException 处理异常。
     */
    void onProcessError(T data, ServiceException exception);
}
