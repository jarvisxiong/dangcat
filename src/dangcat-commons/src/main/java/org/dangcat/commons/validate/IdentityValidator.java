package org.dangcat.commons.validate;

public class IdentityValidator extends RegexValidator
{
    private static final String PATTERN = "\\d{15}|\\d{18}|\\d{14}[xX]|\\d{17}[xX]";

    public IdentityValidator()
    {
        super(PATTERN, true);
    }
}
