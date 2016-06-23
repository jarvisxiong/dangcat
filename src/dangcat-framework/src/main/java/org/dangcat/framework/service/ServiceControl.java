package org.dangcat.framework.service;

/**
 * 服务控制。
 *
 * @author dangcat
 */
public interface ServiceControl {
    /**
     * 服务名称。
     */
    String getServiceName();

    /**
     * 服务状态。
     *
     * @return
     */
    ServiceStatus getServiceStatus();

    /**
     * 服务是否启动。
     */
    boolean isEnabled();

    /**
     * 重新启动。
     */
    void restart();

    /**
     * 启动服务。
     */
    void start();

    /**
     * 停止服务。
     */
    void stop();
}
