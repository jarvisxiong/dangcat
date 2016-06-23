package org.dangcat.framework.service;

import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.exception.ServiceInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务信息反馈。
 *
 * @author dangcat
 */
public class ServiceFeedback {
    /**
     * 异常信息表。
     */
    private List<ServiceException> serviceExceptionList = null;
    /**
     * 提示信息表。
     */
    private List<ServiceInformation> serviceInformationList = null;

    public void addServiceException(ServiceException serviceException) {
        if (this.serviceExceptionList == null)
            this.serviceExceptionList = new ArrayList<ServiceException>();
        if (serviceException != null && !this.serviceExceptionList.contains(serviceException))
            this.serviceExceptionList.add(serviceException);
    }

    public void addServiceInformation(ServiceInformation serviceInformation) {
        if (this.serviceInformationList == null)
            this.serviceInformationList = new ArrayList<ServiceInformation>();
        if (serviceInformation != null && !this.serviceInformationList.contains(serviceInformation))
            this.serviceInformationList.add(serviceInformation);
    }

    public void clearServiceException() {
        this.serviceExceptionList = null;
    }

    public void clearServiceInformation() {
        this.serviceInformationList = null;
    }

    public ServiceException findServiceException(Integer messageId) {
        ServiceException found = null;
        if (this.serviceExceptionList != null) {
            for (ServiceException serviceException : this.serviceExceptionList) {
                if (serviceException.getMessageId().equals(messageId)) {
                    found = serviceException;
                    break;
                }
            }
        }
        return found;
    }

    public ServiceInformation findServiceInformation(Integer messageId) {
        ServiceInformation found = null;
        if (this.serviceInformationList != null) {
            for (ServiceInformation serviceInformation : this.serviceInformationList) {
                if (serviceInformation.getMessageId().equals(messageId)) {
                    found = serviceInformation;
                    break;
                }
            }
        }
        return found;
    }

    @Serialize(name = "errors")
    public List<ServiceException> getServiceExceptionList() {
        return this.serviceExceptionList;
    }

    @Serialize(name = "infos")
    public List<ServiceInformation> getServiceInformationList() {
        return this.serviceInformationList;
    }

    public boolean hasError() {
        return this.serviceExceptionList != null && this.serviceExceptionList.size() > 0;
    }

    public boolean hasInformation() {
        return this.serviceInformationList != null && this.serviceInformationList.size() > 0;
    }
}
