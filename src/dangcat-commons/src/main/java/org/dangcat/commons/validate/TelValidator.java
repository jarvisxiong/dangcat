package org.dangcat.commons.validate;

public class TelValidator extends RegexValidator
{
    private static final String PATTERN = "(\\d{3}|\\d{4})-\\d{8}|\\d{8}";
    private static final String SYSTEM_PATTERN = System.getProperty("TEL_PATTERN");

    public TelValidator()
    {
        super(SYSTEM_PATTERN == null ? PATTERN : SYSTEM_PATTERN, true);
    }
}
