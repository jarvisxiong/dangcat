package org.dangcat.commons.validate;

public class MobileValidator extends RegexValidator {
    private static final String PATTERN = "^1[3|4|5|8][0-9]\\d{4,8}$";
    private static final String SYSTEM_PATTERN = System.getProperty("MOBILE_PATTERN");

    public MobileValidator() {
        super(SYSTEM_PATTERN == null ? PATTERN : SYSTEM_PATTERN, true);
    }
}
