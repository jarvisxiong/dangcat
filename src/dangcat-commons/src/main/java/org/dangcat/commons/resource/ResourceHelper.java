package org.dangcat.commons.resource;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.Environment;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 资源帮助。
 *
 * @author dangcat
 */
public class ResourceHelper {
    protected static final Logger logger = Logger.getLogger(ResourceHelper.class);
    private static final String VALUE_MAP = ".ValueMap";
    private Class<?> classType = null;
    private Locale locale = null;
    private List<ResourceBundle> resourceBundleList = null;
    private String resourceName;

    public ResourceHelper(Class<?> classType) {
        this(classType, null, null);
    }

    public ResourceHelper(Class<?> classType, Locale locale) {
        this(classType, locale, null);
    }

    public ResourceHelper(Class<?> classType, Locale locale, String resourceName) {
        this.classType = classType;
        this.locale = locale;
        this.resourceName = resourceName;
    }

    public ResourceHelper(Class<?> classType, String resourceName) {
        this(classType, null, resourceName);
    }

    public Class<?> getClassType() {
        return classType;
    }

    public Locale getLocale() {
        return this.locale == null ? Environment.getDefaultLocale() : this.locale;
    }

    /**
     * 读取资源对象。
     */
    public Object getObject(String key) {
        Object value = null;
        for (ResourceBundle resourceBundle : this.getResourceBundleList()) {
            if (resourceBundle.containsKey(key)) {
                value = resourceBundle.getObject(key);
                break;
            }
        }
        return value;
    }

    private List<ResourceBundle> getResourceBundleList() {
        if (this.resourceBundleList == null) {
            ClassLoader classLoader = this.classType.getClassLoader();
            if (classLoader != null) {
                String baseName = this.classType.getPackage().getName();
                this.resourceBundleList = ResourceManager.getInstance().getResourceBundleList(classLoader, baseName, this.getResourceName(), this.getLocale());
            }
        }
        return this.resourceBundleList;
    }

    private String getResourceName() {
        return this.resourceName == null ? this.classType.getSimpleName() : this.resourceName;
    }

    /**
     * 根据主键读取资源文字。
     */
    public String getText(String key) {
        String value = null;
        List<ResourceBundle> resourceBundleList = this.getResourceBundleList();
        if (resourceBundleList != null) {
            for (ResourceBundle resourceBundle : resourceBundleList) {
                if (resourceBundle.containsKey(key)) {
                    value = resourceBundle.getString(key);
                    break;
                }
            }
        }
        return value;
    }

    /**
     * 根据主键读取资源列表。
     */
    public Map<Integer, String> getValueMap(String key) {
        return ResourceUtils.createValueMap(this.getText(key + VALUE_MAP));
    }
}
