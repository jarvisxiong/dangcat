package org.dangcat.persistence.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.persistence.sql.Sql;

/**
 * 栏位对象解析器。
 *
 * @author dangcat
 */
public class SqlXmlResolver extends XmlResolver {
    /**
     * 栏位对象。
     */
    private Sql sql = null;

    /**
     * 构建解析器。
     */
    public SqlXmlResolver() {
        super(Sql.class.getSimpleName());
    }

    /**
     * 属性文本。
     *
     * @param value 文本值。
     */
    @Override
    protected void resolveElementText(String value) {
        this.sql.setSql(value);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement() {
        this.sql = new Sql();
        this.setResolveObject(sql);
    }
}
