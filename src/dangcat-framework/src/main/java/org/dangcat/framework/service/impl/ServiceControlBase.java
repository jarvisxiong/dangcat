package org.dangcat.framework.service.impl;

import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceControl;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.ServiceStatus;

/**
 * 服务控制基类
 *
 * @author dangcat
 */
public abstract class ServiceControlBase extends ServiceBase implements ServiceControl {
    private String name;

    /**
     * 服务状态。
     */
    private ServiceStatus serviceStatus = ServiceStatus.Stopped;

    /**
     * 构造服务对象。
     *
     * @param parent 所属服务。
     */
    public ServiceControlBase(ServiceProvider parent) {
        super(parent);
    }

    /**
     * 构造服务对象。
     *
     * @param parent 所属服务。
     * @param name   服务名称。
     */
    public ServiceControlBase(ServiceProvider parent, String name) {
        super(parent);
        this.name = name;
    }

    /**
     * 服务名称。
     */
    public String getServiceName() {
        if (!ValueUtils.isEmpty(this.name))
            return this.name;
        return this.getClass().getSimpleName();
    }

    public void setServiceName(String name) {
        this.name = name;
    }

    /**
     * 服务状态。
     */
    @Override
    public ServiceStatus getServiceStatus() {
        return this.serviceStatus;
    }

    /**
     * 设置服务状态。
     */
    protected void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    /**
     * 是否正在运行。
     */
    protected boolean isRunning() {
        return this.getServiceStatus().equals(ServiceStatus.Started);
    }

    @Override
    public void restart() {
        this.stop();
        this.start();
    }

    @Override
    public void start() {
        this.serviceStatus = ServiceStatus.Started;
        logger.info("The service " + this.getServiceName() + " started. ");
    }

    @Override
    public void stop() {
        this.serviceStatus = ServiceStatus.Stopped;
        logger.info("The service " + this.getServiceName() + " stopped. ");
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder(super.toString());
        info.append(Environment.LINETAB_SEPARATOR);
        info.append("ServiceName = " + this.getServiceName());
        info.append(Environment.LINETAB_SEPARATOR);
        info.append("isEnabled = " + this.isEnabled());
        return info.toString();
    }
}
