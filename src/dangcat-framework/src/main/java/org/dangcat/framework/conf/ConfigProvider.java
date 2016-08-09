package org.dangcat.framework.conf;

import java.util.Map;

/**
 * 配置控制表
 *
 * @author dangcat
 */
public interface ConfigProvider {
    Map<String, ConfigValue> getConfigValueMap();
}
