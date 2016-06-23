package org.dangcat.commons.validate;

public class NameValidator extends RegexValidator
{
    private static final String PATTERN = "^([a-z_A-Z-.0-9]+)$";

    public NameValidator()
    {
        super(PATTERN, true);
    }
}
