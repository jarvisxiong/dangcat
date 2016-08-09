package org.dangcat.persistence.xml;

import org.dangcat.commons.serialize.xml.Value;
import org.dangcat.commons.serialize.xml.ValueXmlResolver;
import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.persistence.filter.FilterUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * 栏位对象解析器。
 *
 * @author dangcat
 */
public class FilterUnitXmlResolver extends XmlResolver {
    /**
     * 栏位对象。
     */
    private FilterUnit filterUnit = null;

    /**
     * 栏位对象。
     */
    private List<Object> paramList = null;

    /**
     * 构建解析器。
     */
    public FilterUnitXmlResolver() {
        super(FilterUnit.class.getSimpleName());
        this.addChildXmlResolver(new ValueXmlResolver());
    }

    /**
     * 产生子元素对象。
     *
     * @param elementName 子元素名称。
     * @param child       子元素对象。
     */
    @Override
    protected void afterChildCreate(String elementName, Object child) {
        if (elementName.equalsIgnoreCase(Value.class.getSimpleName())) {
            Value value = (Value) child;
            this.paramList.add(value.getValue());
        }
    }

    /**
     * 解析元素标签结束。
     *
     * @return 解析对象。
     */
    @Override
    protected Object endElement() {
        this.filterUnit.setParams(paramList.toArray());
        return super.endElement();
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement() {
        this.filterUnit = new FilterUnit();
        this.paramList = new ArrayList<Object>();
        this.setResolveObject(filterUnit);
    }
}
