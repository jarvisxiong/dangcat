package org.dangcat.commons.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dangcat.commons.utils.ValueUtils;

public class RegexValidator
{
    private boolean matchAll = true;
    private Pattern pattern = null;

    public RegexValidator(Pattern pattern, boolean matchAll)
    {
        this.pattern = pattern;
        this.matchAll = matchAll;
    }

    public RegexValidator(String pattern, boolean matchAll)
    {
        this(Pattern.compile(pattern), matchAll);
    }

    public boolean isMatchAll()
    {
        return this.matchAll;
    }

    public boolean isValid(String value)
    {
        if (ValueUtils.isEmpty(value))
            return false;
        Matcher matcher = this.pattern.matcher(value);
        return this.isMatchAll() ? matcher.matches() : matcher.find();
    }

    public void setMatchAll(boolean matchAll)
    {
        this.matchAll = matchAll;
    }
}
