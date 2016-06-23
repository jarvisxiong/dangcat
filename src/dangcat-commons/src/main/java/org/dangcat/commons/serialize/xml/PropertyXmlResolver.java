package org.dangcat.commons.serialize.xml;

/**
 * 属性对象解析器。
 * @author dangcat
 * 
 */
public class PropertyXmlResolver extends XmlResolver
{
    /** 属性对象。 */
    private Property property = null;

    /**
     * 构建解析器。
     */
    public PropertyXmlResolver()
    {
        super(Property.class.getSimpleName());
    }

    /**
     * 属性文本。
     * @param value 文本值。
     */
    @Override
    protected void resolveElementText(String valueText)
    {
        this.property.setValue(valueText);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement()
    {
        this.property = new Property();
        this.setResolveObject(this.property);
    }
}
