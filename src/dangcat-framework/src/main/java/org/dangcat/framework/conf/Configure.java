package org.dangcat.framework.conf;

import org.dangcat.commons.utils.ValueUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 资源配置。
 *
 * @author dangcat
 */
public class Configure {
    private String name;
    private Map<String, String> params = new HashMap<String, String>();
    private String type;

    public Configure(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public void configure(Properties properties) {
        String key = (this.type + "." + this.name + ".").toLowerCase();
        for (Object keyObject : properties.keySet()) {
            String propertyName = (String) keyObject;
            String lowerCasePropertyName = propertyName.toLowerCase();
            if (lowerCasePropertyName.startsWith(key)) {
                String subPropertyName = propertyName.substring(key.length());
                if (!ValueUtils.isEmpty(subPropertyName))
                    this.params.put(subPropertyName, properties.getProperty(propertyName));
            }
        }
    }

    public Map<String, String> getParams() {
        return this.params;
    }
}
