package org.dangcat.net.rfc.template.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;

import java.util.Map;

/**
 * 选项列表对象解析器。
 * @author dangcat
 * 
 */
public class OptionsXmlResolver extends XmlResolver
{
    public static final String RESOLVER_NAME = "Options";

    /**
     * 构建解析器。
     */
    public OptionsXmlResolver()
    {
        super(RESOLVER_NAME);
        this.addChildXmlResolver(new OptionXmlResolver());
    }

    /**
     * 产生子元素对象。
     * @param elementName 子元素名称。
     * @param child 子元素对象。
     */
    protected void afterChildCreate(String elementName, Object child)
    {
        Option option = (Option) child;
        if (option != null)
            this.getOptions().put(option.getKey(), option.getValue());
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, String> getOptions()
    {
        return (Map<Integer, String>) this.getResolveObject();
    }
}
