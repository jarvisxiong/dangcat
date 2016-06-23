package org.dangcat.commons.serialize.xml;

import org.dangcat.commons.reflect.ReflectUtils;

/**
 * 栏位对象解析器。
 * @author dangcat
 * 
 */
public class ValueXmlResolver extends XmlResolver
{
    /**
     * 栏位对象。
     */
    private Value value = null;

    /**
     * 构建解析器。
     */
    public ValueXmlResolver()
    {
        this(Value.class.getSimpleName());
    }

    /**
     * 构建解析器。
     */
    public ValueXmlResolver(String name)
    {
        super(name);
    }

    /**
     * 属性文本。
     * @param value 文本值。
     */
    @Override
    protected void resolveElementText(String valueText)
    {
        this.value.setValue(ReflectUtils.parseValue(this.value.getClassType(), valueText));
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement()
    {
        this.value = new Value();
        this.setResolveObject(this.value);
    }
}
