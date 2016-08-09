package org.dangcat.net.rfc.template.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;

import java.util.HashMap;

class AttributeXmlResolver extends XmlResolver {
    private static final String RESOLVER_NAME = "Attribute";
    /**
     * 解析对象。
     */
    private Attribute attribute = null;

    AttributeXmlResolver() {
        super(RESOLVER_NAME);
        this.addChildXmlResolver(new OptionsXmlResolver());
    }

    /**
     * 解析子元素之前。
     *
     * @param name        属性名称。
     * @param xmlResolver 解析器。
     */
    protected void beforeChildResolve(String elementName, XmlResolver xmlResolver) {
        if (OptionsXmlResolver.RESOLVER_NAME.equalsIgnoreCase(elementName)) {
            this.attribute.setOptions(new HashMap<Integer, String>());
            xmlResolver.setResolveObject(this.attribute.getOptions());
        }
    }

    /**
     * 开始解析元素标签。
     */
    @Override
    protected void startElement() {
        this.attribute = new Attribute();
        this.setResolveObject(this.attribute);
    }
}
