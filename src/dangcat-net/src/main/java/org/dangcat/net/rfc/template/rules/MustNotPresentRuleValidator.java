package org.dangcat.net.rfc.template.rules;

import org.dangcat.net.rfc.exceptions.ProtocolValidateException;

import java.util.Map;

/**
 * 0 This attribute MUST NOT be present¡£
 * @author dangcat
 * 
 */
public class MustNotPresentRuleValidator extends RuleValidator
{
    @Override
    public void validate(Map<Integer, Integer> attributeDataCountMap) throws ProtocolValidateException
    {
        if (attributeDataCountMap.containsKey(this.getAttributeTemplate().getType()))
            throw new ProtocolValidateException(ProtocolValidateException.RULE_MUSTNOTPRESENT, this.getAttributeTemplate().getFullName());
    }
}
