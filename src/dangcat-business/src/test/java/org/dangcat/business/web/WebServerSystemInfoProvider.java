package org.dangcat.business.web;

import org.dangcat.business.systeminfo.SystemInfo;
import org.dangcat.business.systeminfo.SystemInfoProvider;

import java.util.LinkedHashMap;
import java.util.Map;

public class WebServerSystemInfoProvider implements SystemInfoProvider {
    @Override
    public void createExtendInfos(SystemInfo systemInfo) {
        Map<String, Object> extendInfos = new LinkedHashMap<String, Object>();
        extendInfos.put("loginInfo", "This a test version.");
        systemInfo.setExtendInfos(extendInfos);
    }
}
