package org.dangcat.commons.validate;

public class NoValidator extends RegexValidator {
    private static final String PATTERN = "[a-zA-Z0-9_]{";

    public NoValidator(int minLength, int maxLength) {
        // 允许5-16字节，允许字母数字下划线
        super(PATTERN + minLength + "," + maxLength + "}$", true);
    }
}
