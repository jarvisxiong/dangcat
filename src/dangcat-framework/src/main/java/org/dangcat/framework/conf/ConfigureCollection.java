package org.dangcat.framework.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 资源配置集合。
 *
 * @author dangcat
 */
public class ConfigureCollection {
    private Map<String, Configure> configureMap = new HashMap<String, Configure>();
    private String type;

    public ConfigureCollection(String type) {
        this.type = type;
    }

    public void configure(Properties properties) {
        String key = this.type + ".";
        for (Object keyObject : properties.keySet()) {
            String propertyName = (String) keyObject;
            String lowerCasePropertyName = propertyName.toLowerCase();
            if (lowerCasePropertyName.startsWith(key)) {
                String[] propertyNames = propertyName.split("\\.");
                if (propertyNames != null && propertyNames.length > 1) {
                    String resourceName = propertyNames[1];
                    Configure configure = this.configureMap.get(resourceName);
                    if (configure == null) {
                        configure = new Configure(this.type, resourceName);
                        this.configureMap.put(resourceName, configure);
                    }
                    configure.configure(properties);
                }
            }
        }
    }

    public Map<String, Configure> getConfigureMap() {
        return this.configureMap;
    }
}
