package org.dangcat.commons.validate;

import org.dangcat.commons.utils.ValueUtils;

public class PortValidator extends RegexValidator
{
    private static final int MAX_PORT = 65535;
    private static final int MIN_PORT = 1;
    private static final String PATTERN = "\\d+";

    public PortValidator()
    {
        super(PATTERN, true);
    }

    @Override
    public boolean isValid(String value)
    {
        if (!super.isValid(value))
            return false;
        Integer port = ValueUtils.parseInt(value);
        return !(port == null || port < MIN_PORT || port > MAX_PORT);
    }
}
