package org.dangcat.net.rfc.template.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;

/**
 * 选项对象解析器。
 * @author dangcat
 * 
 */
public class OptionXmlResolver extends XmlResolver
{
    private Option option = null;

    /**
     * 构建解析器。
     */
    public OptionXmlResolver()
    {
        this(Option.class.getSimpleName());
    }

    /**
     * 构建解析器。
     */
    public OptionXmlResolver(String name)
    {
        super(name);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement()
    {
        this.option = new Option();
        this.setResolveObject(this.option);
    }
}
