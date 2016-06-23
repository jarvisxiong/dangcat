package org.dangcat.commons.validate;

public class EmailValidator extends RegexValidator
{
    private static final String PATTERN = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

    public EmailValidator()
    {
        super(PATTERN, true);
    }
}
