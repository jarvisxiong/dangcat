package org.dangcat.commons.validate;

import org.dangcat.commons.utils.ValueUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator {
    private boolean matchAll = true;
    private Pattern pattern = null;

    public RegexValidator(Pattern pattern, boolean matchAll) {
        this.pattern = pattern;
        this.matchAll = matchAll;
    }

    public RegexValidator(String pattern, boolean matchAll) {
        this(Pattern.compile(pattern), matchAll);
    }

    public boolean isMatchAll() {
        return this.matchAll;
    }

    public void setMatchAll(boolean matchAll) {
        this.matchAll = matchAll;
    }

    public boolean isValid(String value) {
        if (ValueUtils.isEmpty(value))
            return false;
        Matcher matcher = this.pattern.matcher(value);
        return this.isMatchAll() ? matcher.matches() : matcher.find();
    }
}
