package org.dangcat.framework.service;

/**
 * 服务控制。
 * @author dangcat
 * 
 */
public interface ServiceControl
{
    /**
     * 服务名称。
     */
    public String getServiceName();

    /**
     * 服务状态。
     * @return
     */
    public ServiceStatus getServiceStatus();

    /**
     * 服务是否启动。
     */
    public boolean isEnabled();

    /**
     * 重新启动。
     */
    public void restart();

    /**
     * 启动服务。
     */
    public void start();

    /**
     * 停止服务。
     */
    public void stop();
}
