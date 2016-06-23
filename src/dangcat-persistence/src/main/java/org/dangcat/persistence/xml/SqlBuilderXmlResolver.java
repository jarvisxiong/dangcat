package org.dangcat.persistence.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.persistence.orm.SqlBuilder;

/**
 * 栏位对象解析器。
 *
 * @author dangcat
 */
public class SqlBuilderXmlResolver extends XmlResolver {
    public static final String RESOLVER_NAME = "Sql";

    /**
     * 构建解析器。
     */
    public SqlBuilderXmlResolver() {
        super(RESOLVER_NAME);
    }

    /**
     * 属性文本。
     *
     * @param value 文本值。
     */
    @Override
    protected void resolveElementText(String value) {
        SqlBuilder sqlBuilder = (SqlBuilder) this.getResolveObject();
        sqlBuilder.append(value);
    }
}
