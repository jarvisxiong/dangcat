package org.dangcat.persistence.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.persistence.orderby.OrderBy;

/**
 * 栏位对象解析器。
 *
 * @author dangcat
 */
public class OrderByXmlResolver extends XmlResolver {
    /**
     * 栏位对象。
     */
    private OrderBy orderBy = null;

    /**
     * 构建解析器。
     */
    public OrderByXmlResolver() {
        super(OrderBy.class.getSimpleName());
    }

    /**
     * 属性文本。
     *
     * @param value 文本值。
     */
    @Override
    protected void resolveElementText(String value) {
        this.orderBy = OrderBy.parse(value);
        this.setResolveObject(orderBy);
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement() {
        orderBy = new OrderBy();
        this.setResolveObject(orderBy);
    }
}
