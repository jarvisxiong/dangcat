package org.dangcat.persistence.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterGroup;

/**
 * 栏位对象解析器。
 *
 * @author dangcat
 */
public class FilterGroupXmlResolver extends XmlResolver {
    /**
     * 栏位对象。
     */
    private FilterGroup filterGroup = null;

    /**
     * 构建解析器。
     */
    public FilterGroupXmlResolver() {
        this(FilterGroup.class.getSimpleName());
    }

    /**
     * 构建解析器。
     */
    public FilterGroupXmlResolver(String name) {
        super(name);
        this.addChildXmlResolver(new FilterUnitXmlResolver());
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
            this.filterGroup.getFilterExpressList().add((FilterExpress) child);
    }

    /**
     * 解析元素标签结束。
     *
     * @return 解析对象。
     */
    @Override
    protected Object endElement() {
        if (filterGroup.getFilterExpressList().size() == 1)
            return filterGroup.getFilterExpressList().get(0);
        return super.endElement();
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement() {
        filterGroup = new FilterGroup();
        if (!containsChildXmlResolver(FilterGroup.class.getSimpleName()))
            this.addChildXmlResolver(new FilterGroupXmlResolver());
        this.setResolveObject(filterGroup);
    }
}
