package org.dangcat.persistence.xml;

import org.dangcat.commons.serialize.xml.ParamsXmlResolver;
import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.persistence.model.Table;

/**
 * 栏位对象解析器。
 *
 * @author dangcat
 */
public class TableXmlResolver extends XmlResolver {
    /**
     * 栏位对象。
     */
    private Table table = null;

    /**
     * 构建解析器。
     */
    public TableXmlResolver() {
        super(Table.class.getSimpleName());
        this.addChildXmlResolver(new ColumnsXmlResolver());
        this.addChildXmlResolver(new SqlBuilderXmlResolver());
        this.addChildXmlResolver(new SqlsXmlResolver());
        this.addChildXmlResolver(new ParamsXmlResolver());
        this.addChildXmlResolver(new OrderByXmlResolver());
        this.addChildXmlResolver(new FilterGroupXmlResolver("FixFilter"));
        this.addChildXmlResolver(new FilterGroupXmlResolver("Filter"));
        this.addChildXmlResolver(new CalculatorsXmlResolver());
    }

    public Table getTable() {
        return table;
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement() {
        this.table = new Table();
        this.setResolveObject(table);
    }
}
