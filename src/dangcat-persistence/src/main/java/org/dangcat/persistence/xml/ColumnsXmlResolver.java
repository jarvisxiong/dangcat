package org.dangcat.persistence.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Columns;

/**
 * 栏位对象解析器。
 *
 * @author dangcat
 */
public class ColumnsXmlResolver extends XmlResolver {
    /**
     * 栏位对象。
     */
    private Columns columns = null;

    /**
     * 构建解析器。
     */
    public ColumnsXmlResolver() {
        super(Columns.class.getSimpleName());
        this.addChildXmlResolver(new ColumnXmlResolver());
    }

    /**
     * 产生子元素对象。
     *
     * @param elementName 子元素名称。
     * @param child       子元素对象。
     */
    @Override
    protected void afterChildCreate(String elementName, Object child) {
        if (child != null)
            columns.add((Column) child);
    }

    @Override
    public void setResolveObject(Object resolveObject) {
        this.columns = (Columns) resolveObject;
        super.setResolveObject(resolveObject);
    }
}
