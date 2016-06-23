package org.dangcat.commons.formator;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberFormator implements DataFormator
{
    private NumberFormat numberFormat = null;

    public NumberFormator(String format)
    {
        this.numberFormat = new DecimalFormat(format);
    }

    @Override
    public String format(Object data)
    {
        return this.numberFormat.format(data);
    }
}
