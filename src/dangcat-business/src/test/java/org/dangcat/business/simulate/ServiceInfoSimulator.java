package org.dangcat.business.simulate;

import org.dangcat.business.domain.ServiceInfo;
import org.dangcat.persistence.simulate.table.EntityData;

public class ServiceInfoSimulator extends EntityData<ServiceInfo>
{
    public ServiceInfoSimulator()
    {
        super(ServiceInfo.class);
    }
}
