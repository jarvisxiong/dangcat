package org.dangcat.net.rfc;

import org.dangcat.net.rfc.template.AttributeTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 厂商属性管理。
 *
 * @author dangcat
 */
public class PacketVendorAttributeCollection {
    private List<AttributeTemplate> attributeTemplateList = null;
    private Map<Integer, AttributeTemplate> attributeTemplateMap = null;
    private Integer vendorId = null;
    private String vendorName = null;

    public PacketVendorAttributeCollection(Integer vendorId, String vendorName, List<AttributeTemplate> attributeTemplateList) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.attributeTemplateList = attributeTemplateList;
    }

    public AttributeTemplate getAttributeTemplate(Integer type) {
        if (this.attributeTemplateMap == null) {
            Map<Integer, AttributeTemplate> attributeTemplateMap = new HashMap<Integer, AttributeTemplate>();
            for (AttributeTemplate attributeTemplate : this.attributeTemplateList)
                attributeTemplateMap.put(attributeTemplate.getType(), attributeTemplate);
            this.attributeTemplateMap = attributeTemplateMap;
        }
        return this.attributeTemplateMap.get(type);
    }

    public List<AttributeTemplate> getAttributeTemplateList() {
        return attributeTemplateList;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }
}
