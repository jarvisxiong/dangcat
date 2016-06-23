package org.dangcat.persistence.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.persistence.model.DataSet;
import org.dangcat.persistence.model.Table;

/**
 * 栏位对象解析器。
 * @author dangcat
 * 
 */
public class DataSetXmlResolver extends XmlResolver
{
    /**
     * 栏位对象。
     */
    private DataSet dataSet = null;

    /**
     * 构建解析器。
     */
    public DataSetXmlResolver()
    {
        super(DataSet.class.getSimpleName());
        this.addChildXmlResolver(new TableXmlResolver());
    }

    /**
     * 产生子元素对象。
     * @param elementName 子元素名称。
     * @param child 子元素对象。
     */
    protected void afterChildCreate(String elementName, Object child)
    {
        Table table = (Table) child;
        if (table != null)
            this.dataSet.add(table);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement()
    {
        this.dataSet = new DataSet();
        this.setResolveObject(dataSet);
    }
}
