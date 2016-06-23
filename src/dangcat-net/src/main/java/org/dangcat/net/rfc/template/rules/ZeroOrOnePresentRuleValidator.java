package org.dangcat.net.rfc.template.rules;

import org.dangcat.net.rfc.exceptions.ProtocolValidateException;

import java.util.Map;

/**
 * 0-1 Zero or one instance of this attribute MAY be present.
 * @author dangcat
 * 
 */
public class ZeroOrOnePresentRuleValidator extends RuleValidator
{
    @Override
    public void validate(Map<Integer, Integer> attributeDataCountMap) throws ProtocolValidateException
    {
        Integer count = attributeDataCountMap.get(this.getAttributeTemplate().getType());
        if (count != null && count > 1)
            throw new ProtocolValidateException(ProtocolValidateException.RULE_ZEROORONEPRESENT, this.getAttributeTemplate().getFullName());
    }
}
