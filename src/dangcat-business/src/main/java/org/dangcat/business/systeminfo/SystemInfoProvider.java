package org.dangcat.business.systeminfo;

/**
 * 提供扩展系统信息。
 */
public interface SystemInfoProvider {
    /**
     * 产生扩展系统信息。
     *
     * @param systemInfo 系统信息对象。
     */
    void createExtendInfos(SystemInfo systemInfo);
}
