package org.dangcat.business.systeminfo;

import java.util.Map;

import org.dangcat.framework.service.ServicePrincipal;

/**
 * 系统信息。
 * @author Administrator
 * 
 */
public class SystemInfo
{
    private String baseUrl = null;
    private String clientIp = null;
    private String copyRight = null;
    private Map<String, Object> extendInfos = null;
    private String masterTitle = null;
    private String projectTitle = null;
    private String secondTitle = null;
    private ServicePrincipal servicePrincipal = null;
    private String sessionId = null;
    private String version = null;

    public String getBaseUrl()
    {
        return this.baseUrl;
    }

    public String getClientIp()
    {
        return this.clientIp;
    }

    public String getCopyRight()
    {
        return this.copyRight;
    }

    public Map<String, Object> getExtendInfos()
    {
        return this.extendInfos;
    }

    public String getMasterTitle()
    {
        return this.masterTitle;
    }

    public String getProjectTitle()
    {
        return this.projectTitle;
    }

    public String getSecondTitle()
    {
        return this.secondTitle;
    }

    public ServicePrincipal getServicePrincipal()
    {
        return this.servicePrincipal;
    }

    public String getSessionId()
    {
        return this.sessionId;
    }

    public String getVersion()
    {
        return this.version;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public void setClientIp(String clientIp)
    {
        this.clientIp = clientIp;
    }

    public void setCopyRight(String copyRight)
    {
        this.copyRight = copyRight;
    }

    public void setExtendInfos(Map<String, Object> extendInfos)
    {
        this.extendInfos = extendInfos;
    }

    public void setMasterTitle(String masterTitle)
    {
        this.masterTitle = masterTitle;
    }

    public void setProjectTitle(String projectTitle)
    {
        this.projectTitle = projectTitle;
    }

    public void setSecondTitle(String secondTitle)
    {
        this.secondTitle = secondTitle;
    }

    public void setServicePrincipal(ServicePrincipal servicePrincipal)
    {
        this.servicePrincipal = servicePrincipal;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }
}
