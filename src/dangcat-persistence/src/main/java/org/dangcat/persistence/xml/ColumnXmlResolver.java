package org.dangcat.persistence.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.persistence.model.Column;

/**
 * 栏位对象解析器。
 * @author dangcat
 * 
 */
public class ColumnXmlResolver extends XmlResolver
{
    /** 栏位对象。 */
    private Column column = null;

    /**
     * 构建解析器。
     */
    public ColumnXmlResolver()
    {
        super(Column.class.getSimpleName());
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement()
    {
        this.column = new Column();
        this.setResolveObject(column);
    }
}
