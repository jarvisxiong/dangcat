package org.dangcat.net.rfc.attribute;

import org.dangcat.commons.utils.Environment;
import org.dangcat.net.rfc.template.AttributeTemplate;
import org.dangcat.net.rfc.template.AttributeTemplateManager;

/**
 * 厂商扩展属性。
 *
 * @author dangcat
 */
public class VendorAttribute extends AttributeData implements NotifyAttributeChanged {
    /**
     * 属性...
     */
    private AttributeCollection attributeCollection = new AttributeCollection();
    /**
     * 厂商编号
     */
    private Integer vendorId = null;

    public VendorAttribute(AttributeTemplate attributeTemplate, Integer vendorId) {
        super(attributeTemplate);
        this.vendorId = vendorId;
        this.setValue(this.attributeCollection);
    }

    public AttributeCollection getAttributeCollection() {
        if (this.attributeCollection.getVendorAttributeTemplateManager() == null) {
            AttributeTemplateManager attributeTemplateManager = this.getAttributeTemplate().getVendorAttributeTemplateManager().getAttributeTemplateManager();
            this.attributeCollection.setVendorAttributeTemplateManager(attributeTemplateManager.getVendorAttributeTemplateManager(this.getVendorId()));
            this.attributeCollection.setNotifyAttributeChanged(this);
        }
        return this.attributeCollection;
    }

    public Integer getVendorId() {
        return this.vendorId;
    }

    @Override
    public void onAttributeChanged(Object sender) {
        this.notifyChanged();
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        String lengthText = "   " + this.getLength();
        info.append(lengthText.substring(lengthText.length() - 3));
        info.append(" ");
        info.append(this.getAttributeTemplate().getFullName());
        info.append(" ");
        info.append(VendorManager.getDescription(this.getVendorId()));
        info.append(" : ");
        if (this.getAttributeCollection().size() > 1)
            info.append("[");
        info.append(this.getAttributeCollection());
        if (this.getAttributeCollection().size() > 1) {
            info.append(Environment.LINETAB_SEPARATOR);
            info.append("     ]");
        }
        return info.toString();
    }
}
