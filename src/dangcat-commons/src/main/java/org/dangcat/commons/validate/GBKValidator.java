package org.dangcat.commons.validate;

public class GBKValidator extends RegexValidator
{
    private static final String PATTERN = "[\u4e00-\u9fa5]+";

    public GBKValidator()
    {
        super(PATTERN, false);
    }
}
