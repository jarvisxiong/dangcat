package org.dangcat.net.rfc.template.rules;

import java.util.Map;

import org.dangcat.net.rfc.exceptions.ProtocolValidateException;
import org.dangcat.net.rfc.template.AttributeTemplate;
import org.dangcat.net.rfc.template.VendorAttributeTemplateManager;
import org.dangcat.net.rfc.template.xml.Rule;

/**
 * 属性规则校验器。
 * @author dangcat
 * 
 */
public abstract class RuleValidator
{
    public static final String EXACTLYONE_PRESENT = "1";
    public static final String MUSTNOT_PRESENT = "0";
    public static final String ZEROORONE_PRESENT = "0-1";

    public static RuleValidator createInstance(Rule rule)
    {
        RuleValidator ruleValidator = null;
        if (MUSTNOT_PRESENT.equalsIgnoreCase(rule.getRuleType()))
            ruleValidator = new MustNotPresentRuleValidator();
        else if (ZEROORONE_PRESENT.equalsIgnoreCase(rule.getRuleType()))
            ruleValidator = new ZeroOrOnePresentRuleValidator();
        else if (EXACTLYONE_PRESENT.equalsIgnoreCase(rule.getRuleType()))
            ruleValidator = new ExactlyOnePresentRuleValidator();
        if (ruleValidator != null)
        {
            ruleValidator.setName(rule.getName());
            ruleValidator.setRuleType(rule.getRuleType());
        }
        return ruleValidator;
    }

    private AttributeTemplate attributeTemplate = null;
    private String name = null;
    private PacketRuleValidator packetRuleValidator;
    private String ruleType = null;

    public AttributeTemplate getAttributeTemplate()
    {
        if (this.attributeTemplate == null)
        {
            VendorAttributeTemplateManager vendorAttributeTemplateManager = this.getPacketRuleValidator().getVendorAttributeTemplateManager();
            this.attributeTemplate = vendorAttributeTemplateManager.getAttributeTemplate(this.getName());
        }
        return this.attributeTemplate;
    }

    public String getName()
    {
        return name;
    }

    protected PacketRuleValidator getPacketRuleValidator()
    {
        return packetRuleValidator;
    }

    public String getRuleType()
    {
        return ruleType;
    }

    private void setName(String name)
    {
        this.name = name;
    }

    protected void setPacketRuleValidator(PacketRuleValidator packetRuleValidator)
    {
        this.packetRuleValidator = packetRuleValidator;
    }

    private void setRuleType(String ruleType)
    {
        this.ruleType = ruleType;
    }

    /**
     * 属性是否有效。
     */
    public abstract void validate(Map<Integer, Integer> attributeDataCountMap) throws ProtocolValidateException;
}
