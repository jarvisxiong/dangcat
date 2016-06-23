package org.dangcat.boot.permission.xml;

import org.dangcat.boot.permission.JndiName;
import org.dangcat.boot.permission.Module;
import org.dangcat.commons.serialize.xml.XmlResolver;

/**
 * 模块对象解析器。
 * @author dangcat
 * 
 */
public class ModuleXmlResolver extends XmlResolver
{
    /** 模块。 */
    private Module module = null;

    /**
     * 构建解析器。
     */
    public ModuleXmlResolver()
    {
        super(Module.class.getSimpleName());
        this.addChildXmlResolver(new JndiNameXmlResolver());
    }

    /**
     * 产生子元素对象。
     * @param elementName 子元素名称。
     * @param child 子元素对象。
     */
    protected void afterChildCreate(String elementName, Object child)
    {
        if (child instanceof JndiName)
            this.module.addJndiName((JndiName) child);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement()
    {
        this.module = new Module();
        this.setResolveObject(this.module);
    }
}
