package org.dangcat.persistence.cache.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;

import java.util.List;

/**
 * 缓存配置解析器。
 *
 * @author dangcat
 */
public class CachesXmlResolver extends XmlResolver {
    private static final String RESOLVER_NAME = "Caches";
    /**
     * 缓存集合。
     */
    private List<Cache> cacheList = null;

    /**
     * 构建解析器。
     */
    public CachesXmlResolver() {
        super(RESOLVER_NAME);
        this.addChildXmlResolver(new CacheXmlResolver());
    }

    /**
     * 产生子元素对象。
     *
     * @param elementName 子元素名称。
     * @param child       子元素对象。
     */
    @Override
    protected void afterChildCreate(String elementName, Object child) {
        if (child != null)
            this.cacheList.add((Cache) child);
    }

    public List<Cache> getCacheList() {
        return cacheList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setResolveObject(Object resolveObject) {
        this.cacheList = (List<Cache>) resolveObject;
        super.setResolveObject(resolveObject);
    }
}
