package org.dangcat.net.rfc.template;

import org.dangcat.net.rfc.template.rules.PacketRuleValidator;
import org.dangcat.net.rfc.template.xml.Rules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 厂商属性模板管理。
 *
 * @author dangcat
 */
public class VendorAttributeTemplateManager {
    /**
     * 属性模板总管理。
     */
    private AttributeTemplateManager attributeTemplateManager = null;
    /**
     * 属性号与模板的映射列表。
     */
    private Map<Integer, AttributeTemplate> attributeTemplateMap = new HashMap<Integer, AttributeTemplate>();
    /**
     * 属性名与模板的映射列表。
     */
    private Map<String, AttributeTemplate> attributeTemplateNameMap = null;
    /**
     * 包类型的校验表。
     */
    private Map<String, PacketRuleValidator> packetRuleValidatorMap = new HashMap<String, PacketRuleValidator>();
    /**
     * 厂商号。
     */
    private Integer vendorId = null;

    public VendorAttributeTemplateManager(Integer vendorId, AttributeTemplateManager attributeTemplateManager) {
        this.vendorId = vendorId;
        this.attributeTemplateManager = attributeTemplateManager;
    }

    public void addAttributeTemplate(Integer type, AttributeTemplate attributeTemplate) {
        attributeTemplate.setVendorAttributeTemplateManager(this);
        this.attributeTemplateMap.put(type, attributeTemplate);
        this.attributeTemplateNameMap = null;
    }

    public void addPacketRules(Rules rules) {
        PacketRuleValidator packetRuleValidator = this.getPacketRuleValidator(rules.getPacketType());
        if (packetRuleValidator == null) {
            packetRuleValidator = new PacketRuleValidator(this);
            packetRuleValidator.setPacketType(rules.getPacketType());
            this.packetRuleValidatorMap.put(rules.getPacketType(), packetRuleValidator);
        }
        packetRuleValidator.addRules(rules);
    }

    /**
     * 通过属性类型号读取属性模板。
     *
     * @param type 属性号。
     * @return 属性模板。
     */
    public AttributeTemplate getAttributeTemplate(Integer type) {
        return this.attributeTemplateMap.get(type);
    }

    /**
     * 通过厂商、属性类型号读取属性模板。
     *
     * @param name 属性名。
     * @return 属性模板。
     */
    public AttributeTemplate getAttributeTemplate(String name) {
        if (this.attributeTemplateNameMap == null) {
            synchronized (this) {
                if (this.attributeTemplateNameMap == null) {
                    Map<String, AttributeTemplate> attributeTemplateNameMap = new HashMap<String, AttributeTemplate>();
                    for (AttributeTemplate attributeTemplate : this.attributeTemplateMap.values())
                        attributeTemplateNameMap.put(attributeTemplate.getName(), attributeTemplate);
                    this.attributeTemplateNameMap = attributeTemplateNameMap;
                }
            }
        }
        return this.attributeTemplateNameMap.get(name);
    }

    public AttributeTemplateManager getAttributeTemplateManager() {
        return attributeTemplateManager;
    }

    public Map<Integer, AttributeTemplate> getAttributeTemplateMap() {
        return this.attributeTemplateMap;
    }

    public Map<String, AttributeTemplate> getAttributeTemplateNameMap() {
        return this.attributeTemplateNameMap;
    }

    public Collection<AttributeTemplate> getAttributeTemplates() {
        return this.attributeTemplateMap.values();
    }

    /**
     * 指定包类型读取校验规则。
     *
     * @param packetType 包类型。
     * @return 校验规则。
     */
    public PacketRuleValidator getPacketRuleValidator(String packetType) {
        return this.packetRuleValidatorMap.get(packetType);
    }

    public Integer getVendorId() {
        return this.vendorId;
    }
}
