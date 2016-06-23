package org.dangcat.boot.permission.xml;

import org.dangcat.boot.permission.JndiName;
import org.dangcat.commons.serialize.xml.XmlResolver;

/**
 * JndiName对象解析器。
 * @author dangcat
 * 
 */
public class JndiNameXmlResolver extends XmlResolver
{
    private JndiName jndiName = null;

    /**
     * 构建解析器。
     */
    public JndiNameXmlResolver()
    {
        super(JndiName.class.getSimpleName());
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement()
    {
        this.jndiName = new JndiName();
        this.setResolveObject(this.jndiName);
    }
}
