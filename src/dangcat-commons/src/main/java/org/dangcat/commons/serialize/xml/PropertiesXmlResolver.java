package org.dangcat.commons.serialize.xml;

import java.util.List;

/**
 * 属性列表对象解析器。
 * @author dangcat
 * 
 */
public class PropertiesXmlResolver extends XmlResolver
{
    private static final String RESOLVER_NAME = "Properties";
    /** 属性列表。 */
    private List<Property> propertyList;

    /**
     * 构建解析器。
     */
    public PropertiesXmlResolver()
    {
        super(RESOLVER_NAME);
        this.addChildXmlResolver(new PropertyXmlResolver());
    }

    /**
     * 产生子元素对象。
     * @param elementName 子元素名称。
     * @param child 子元素对象。
     */
    protected void afterChildCreate(String elementName, Object child)
    {
        Property property = (Property) child;
        if (property != null)
            this.propertyList.add(property);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setResolveObject(Object resolveObject)
    {
        this.propertyList = (List<Property>) resolveObject;
        super.setResolveObject(resolveObject);
    }
}
