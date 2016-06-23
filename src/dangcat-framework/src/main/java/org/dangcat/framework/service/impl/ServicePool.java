package org.dangcat.framework.service.impl;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.pool.ObjectPool;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceHelper;
import org.dangcat.framework.service.ServiceProvider;

public class ServicePool extends ObjectPool<ServiceLiver> {
    private ServiceProvider parent = null;
    private ServiceInfo serviceInfo = null;

    public ServicePool(ServiceProvider parent, ServiceInfo serviceInfo) {
        this.parent = parent;
        this.serviceInfo = serviceInfo;
    }

    public void cleatTimeOut(long timeout) {
        ServiceLiver serviceLiver = this.peekPooled();
        while (serviceLiver != null) {
            if (DateUtils.currentTimeMillis() - serviceLiver.getLastAccessTime() > timeout) {
                this.destroy(serviceLiver);
                serviceLiver = this.peekPooled();
            } else
                serviceLiver = null;
        }
    }

    @Override
    protected void close(ServiceLiver serviceLiver) {
    }

    @Override
    protected ServiceLiver create() {
        Object service = this.serviceInfo.createInstance(this.parent);
        if (service instanceof ServiceBase)
            ((ServiceBase) service).initialize();
        if (service != null)
            ServiceHelper.inject(this.parent, service);
        return service != null ? new ServiceLiver(service) : null;
    }

    public ServiceInfo getServiceInfo() {
        return this.serviceInfo;
    }
}
