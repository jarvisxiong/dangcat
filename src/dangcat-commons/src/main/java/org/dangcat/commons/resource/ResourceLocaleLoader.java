package org.dangcat.commons.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 本地化资源加载器。
 * @author dangcat
 * 
 */
class ResourceLocaleLoader
{
    private ClassLoader classLoader = null;
    private Map<Locale, ResourceLoader> resourceLocaleLoaderMap = new HashMap<Locale, ResourceLoader>();

    protected ResourceLocaleLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    protected ClassLoader getClassLoader()
    {
        return classLoader;
    }

    /**
     * 根据位置名称和资源名称得到位置集合。
     * @param baseName 位置名称。
     * @param resourceName 资源名称。
     * @return 资源包集合。
     */
    protected List<ResourceBundle> getResourceBundleList(String baseName, String resourceName, Locale locale)
    {
        ResourceLoader resourceLoader = this.getResourceLoader(locale);
        if (resourceLoader == null)
        {
            resourceLoader = new ResourceLoader(this, locale);
            this.putResourceLoader(locale, resourceLoader);
        }
        return resourceLoader.getResourceBundleList(baseName, resourceName);
    }

    private synchronized ResourceLoader getResourceLoader(Locale locale)
    {
        return this.resourceLocaleLoaderMap.get(locale);
    }

    private synchronized void putResourceLoader(Locale locale, ResourceLoader resourceLoader)
    {
        this.resourceLocaleLoaderMap.put(locale, resourceLoader);
    }
}
