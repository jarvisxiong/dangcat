package org.dangcat.commons.formator;

/**
 * 时间格式化。
 * 
 */
public class TimeLengthFormator extends ValueFormator
{
    /** 转换常量。 */
    private final static int[] UNIT_TRANSCONSTS = new int[] { 1, 1000, 60, 60 };
    /** 流量单位。 */
    private final static String[] UNITS = new String[] { "ms", "Sec", "Min", "Hour" };

    @Override
    public int[] getTransConsts()
    {
        return UNIT_TRANSCONSTS;
    }

    @Override
    public String[] getUnits()
    {
        return UNITS;
    }
}
