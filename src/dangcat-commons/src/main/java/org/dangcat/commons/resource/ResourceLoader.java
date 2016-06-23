package org.dangcat.commons.resource;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.Environment;

import java.util.*;

/**
 * 资源加载器。
 * @author dangcat
 * 
 */
class ResourceLoader
{
    protected static final Logger logger = Logger.getLogger(ResourceHelper.class);
    private static final String RESOURCE_GLOBAL = "global";
    private static final String RESOURCE_PACKAGE = "package";
    private Locale locale = null;
    private Map<String, ResourceBundle> resourceLoaderMap = new HashMap<String, ResourceBundle>();
    private ResourceLocaleLoader resourceLocaleLoader = null;

    protected ResourceLoader(ResourceLocaleLoader resourceLocaleLoader, Locale locale)
    {
        this.resourceLocaleLoader = resourceLocaleLoader;
        this.locale = locale;
    }

    /**
     * 分解位置名。
     * @param baseName 位置名称。
     * @return 位置集合。
     */
    private String[] getBaseNames(String baseName)
    {
        List<String> resourceNameList = new ArrayList<String>();
        while (baseName.length() > 0)
        {
            resourceNameList.add(baseName);
            int index = baseName.lastIndexOf(".");
            baseName = (index == -1) ? "" : baseName.substring(0, index);
        }
        return resourceNameList.toArray(new String[0]);
    }

    /**
     * 加载资源包。
     * @param baseName 位置名称。
     * @param resourceName 资源名称。
     * @return 资源包。
     */
    private ResourceBundle getResourceBundle(String baseName, String resourceName)
    {
        ResourceBundle resourceBundle = null;
        try
        {
            String rootName = resourceName;
            if (baseName != null)
                rootName = baseName + "." + resourceName;
            resourceBundle = ResourceBundle.getBundle(rootName, this.locale, this.resourceLocaleLoader.getClassLoader());
        }
        catch (MissingResourceException e)
        {
            if (Environment.isDebugEnabled() && logger.isDebugEnabled())
                logger.error(this, e);
        }
        return resourceBundle;
    }

    /**
     * 根据位置名称和资源名称得到位置集合。
     * @param baseName 位置名称。
     * @param resourceName 资源名称。
     * @return 资源包集合。
     */
    protected List<ResourceBundle> getResourceBundleList(String baseName, String resourceName)
    {
        String rootName = baseName + "." + resourceName;
        List<ResourceBundle> resourceBundleList = new ArrayList<ResourceBundle>();
        if (!this.resourceLoaderMap.containsKey(rootName))
            this.loadResourceBundle(baseName, resourceName);

        ResourceBundle resourceBundle = this.resourceLoaderMap.get(rootName);
        if (resourceBundle != null)
            resourceBundleList.add(resourceBundle);
        for (String packageBaseName : this.getBaseNames(baseName))
        {
            resourceBundle = this.resourceLoaderMap.get(packageBaseName);
            if (resourceBundle != null)
                resourceBundleList.add(resourceBundle);
        }
        resourceBundle = this.resourceLoaderMap.get(RESOURCE_GLOBAL);
        if (resourceBundle != null)
            resourceBundleList.add(resourceBundle);
        return resourceBundleList;
    }

    /**
     * 载入资源包。
     * @param baseName 位置名。
     * @param resourceName 资源名。
     */
    private void loadResourceBundle(String baseName, String resourceName)
    {
        ResourceBundle resourceBundle = this.getResourceBundle(baseName, resourceName);
        Map<String, ResourceBundle> resourceLoaderMap = new HashMap<String, ResourceBundle>();
        resourceLoaderMap.put(baseName + "." + resourceName, resourceBundle);
        // 包资源
        for (String packageBaseName : this.getBaseNames(baseName))
        {
            resourceBundle = this.getResourceBundle(packageBaseName, RESOURCE_PACKAGE);
            resourceLoaderMap.put(packageBaseName, resourceBundle);
        }
        resourceLoaderMap.putAll(this.resourceLoaderMap);
        // 全局资源。
        if (!resourceLoaderMap.containsKey(RESOURCE_GLOBAL))
        {
            resourceBundle = this.getResourceBundle(null, RESOURCE_GLOBAL);
            resourceLoaderMap.put(RESOURCE_GLOBAL, resourceBundle);
        }

        this.resourceLoaderMap = resourceLoaderMap;
    }
}
