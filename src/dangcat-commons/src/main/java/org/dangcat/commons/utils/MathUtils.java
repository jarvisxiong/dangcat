package org.dangcat.commons.utils;

import java.math.BigDecimal;

public class MathUtils
{
    public static final BigDecimal BIGDECIMAL_ONE = new BigDecimal(1);
    public static final Double DOUBLE_ZERO = new Double(0);
    public static final Integer INTEGER_ZERO = new Integer(0);
    public static final Long LONG_ZERO = new Long(0);
    private static final int MAX_DOUBLE_DIGIT = 5;
    private static final double MIN_DOUBLE_VALUE = 0.00001;
    public static final Short SHORT_ZERO = new Short((short) 0);

    public static Number divide(Number value1, Number value2)
    {
        return divide(value1, value2, MAX_DOUBLE_DIGIT);
    }

    public static Number divide(Number value1, Number value2, int scale)
    {
        Number result = null;
        if (value1 != null && value2 != null)
        {
            if (Math.abs(value2.doubleValue()) > MIN_DOUBLE_VALUE)
                result = round(value1.doubleValue() / value2.doubleValue(), scale);
            else
                result = DOUBLE_ZERO;
        }
        return result;
    }

    public static Number multi(Number value1, Number value2)
    {
        Number result = null;
        if (value1 != null && value2 != null)
        {
            if (value1 instanceof Double || value2 instanceof Double)
                result = value1.doubleValue() * value2.doubleValue();
            else if (value1 instanceof Long || value2 instanceof Long)
                result = value1.longValue() * value2.longValue();
            else if (value1 instanceof Integer || value2 instanceof Integer)
                result = value1.intValue() * value2.intValue();
            else if (value1 instanceof Short || value2 instanceof Short)
                result = value1.shortValue() * value2.shortValue();
        }
        return result;
    }

    public static Number plus(Number value1, Number value2)
    {
        Number result = null;
        if (value1 != null && value2 != null)
        {
            if (value1 instanceof Double || value2 instanceof Double)
                result = value1.doubleValue() + value2.doubleValue();
            else if (value1 instanceof Long || value2 instanceof Long)
                result = value1.longValue() + value2.longValue();
            else if (value1 instanceof Integer || value2 instanceof Integer)
                result = value1.intValue() + value2.intValue();
            else if (value1 instanceof Short || value2 instanceof Short)
                result = value1.shortValue() + value2.shortValue();
        }
        else if (value1 != null)
            result = value1;
        else if (value2 != null)
            result = value2;
        return result;
    }

    public static double round(double value, int scale)
    {
        if (scale < 0)
            throw new IllegalArgumentException("The scale must be a positive integer or zero.");

        BigDecimal bigDecimal = new BigDecimal(value);
        return bigDecimal.divide(BIGDECIMAL_ONE, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static Number subtract(Number value1, Number value2)
    {
        Number result = null;
        if (value1 != null && value2 != null)
        {
            if (value1 instanceof Double || value2 instanceof Double)
                result = value1.doubleValue() - value2.doubleValue();
            else if (value1 instanceof Long || value2 instanceof Long)
                result = value1.longValue() - value2.longValue();
            else if (value1 instanceof Integer || value2 instanceof Integer)
                result = value1.intValue() - value2.intValue();
            else if (value1 instanceof Short || value2 instanceof Short)
                result = value1.shortValue() - value2.shortValue();
        }
        else if (value1 != null)
            result = value1;
        else if (value2 != null)
            result = multi(new Short((short) -1), value2);
        return result;
    }
}
