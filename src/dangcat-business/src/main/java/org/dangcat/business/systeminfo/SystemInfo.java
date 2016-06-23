package org.dangcat.business.systeminfo;

import org.dangcat.framework.service.ServicePrincipal;

import java.util.Map;

/**
 * 系统信息。
 *
 * @author Administrator
 */
public class SystemInfo {
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

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getClientIp() {
        return this.clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getCopyRight() {
        return this.copyRight;
    }

    public void setCopyRight(String copyRight) {
        this.copyRight = copyRight;
    }

    public Map<String, Object> getExtendInfos() {
        return this.extendInfos;
    }

    public void setExtendInfos(Map<String, Object> extendInfos) {
        this.extendInfos = extendInfos;
    }

    public String getMasterTitle() {
        return this.masterTitle;
    }

    public void setMasterTitle(String masterTitle) {
        this.masterTitle = masterTitle;
    }

    public String getProjectTitle() {
        return this.projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getSecondTitle() {
        return this.secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public ServicePrincipal getServicePrincipal() {
        return this.servicePrincipal;
    }

    public void setServicePrincipal(ServicePrincipal servicePrincipal) {
        this.servicePrincipal = servicePrincipal;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
