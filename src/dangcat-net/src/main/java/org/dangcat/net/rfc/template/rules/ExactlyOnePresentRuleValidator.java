package org.dangcat.net.rfc.template.rules;

import java.util.Map;

import org.dangcat.net.rfc.exceptions.ProtocolValidateException;

/**
 * 1 Exactly one instance of this attribute MUST be present.
 * @author dangcat
 * 
 */
public class ExactlyOnePresentRuleValidator extends RuleValidator
{
    @Override
    public void validate(Map<Integer, Integer> attributeDataCountMap) throws ProtocolValidateException
    {
        if (!attributeDataCountMap.containsKey(this.getAttributeTemplate().getType()))
            throw new ProtocolValidateException(ProtocolValidateException.RULE_EXACTLYONEPRESENT, this.getAttributeTemplate().getFullName());
    }
}
