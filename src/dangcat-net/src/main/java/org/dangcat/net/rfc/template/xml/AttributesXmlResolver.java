package org.dangcat.net.rfc.template.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.net.rfc.template.AttributeTemplate;
import org.dangcat.net.rfc.template.VendorAttributeTemplateManager;

/**
 * 栏位对象解析器。
 *
 * @author dangcat
 */
public class AttributesXmlResolver extends XmlResolver {
    private static final String RESOLVER_NAME = "Attributes";
    /**
     * 厂商属性模板管理器。
     */
    private VendorAttributeTemplateManager vendorAttributeTemplateManager = null;

    /**
     * 构建解析器。
     */
    public AttributesXmlResolver() {
        super(RESOLVER_NAME);
        this.addChildXmlResolver(new AttributeXmlResolver());
        this.addChildXmlResolver(new RulesXmlResolver());
    }

    /**
     * 产生子元素对象。
     *
     * @param elementName 子元素名称。
     * @param child       子元素对象。
     */
    @Override
    protected void afterChildCreate(String elementName, Object child) {
        if (child != null) {
            if (child instanceof Attribute) {
                Attribute attribute = (Attribute) child;
                AttributeTemplate attributeTemplate = attribute.createAttributeTemplate();
                if (attributeTemplate != null)
                    this.vendorAttributeTemplateManager.addAttributeTemplate(attributeTemplate.getType(), attributeTemplate);
            } else if (child instanceof Rules)
                this.vendorAttributeTemplateManager.addPacketRules((Rules) child);
        }
    }

    public VendorAttributeTemplateManager getVendorAttributeTemplateManager() {
        return this.vendorAttributeTemplateManager;
    }

    public void setVendorAttributeTemplateManager(VendorAttributeTemplateManager vendorAttributeTemplateManager) {
        this.vendorAttributeTemplateManager = vendorAttributeTemplateManager;
    }
}
