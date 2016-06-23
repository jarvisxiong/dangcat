package org.dangcat.persistence.cache.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.commons.utils.ValueUtils;
import org.dom4j.Element;

/**
 * 缓存配置解析器。
 * @author dangcat
 * 
 */
public class CacheXmlResolver extends XmlResolver
{
    private static final String TEXT_INDEX = "Index";
    /** 栏位对象。 */
    private Cache cache = null;

    /**
     * 构建解析器。
     */
    public CacheXmlResolver()
    {
        super(Cache.class.getSimpleName());
    }

    /**
     * 开始解析子元素标签。
     * @param element 子元素名称。
     */
    protected void resolveChildElement(Element element)
    {
        if (element.getName().equalsIgnoreCase(TEXT_INDEX))
        {
            if (!ValueUtils.isEmpty(element.getText()))
            {
                String index = element.getText().trim();
                if (!this.cache.getIndexList().contains(index))
                    this.cache.getIndexList().add(index);
            }
        }
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement()
    {
        this.cache = new Cache();
        this.setResolveObject(this.cache);
    }
}
