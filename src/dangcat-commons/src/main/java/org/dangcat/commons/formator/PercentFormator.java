package org.dangcat.commons.formator;

/**
 * 百分数格式化。
 * 
 */
public class PercentFormator extends ValueFormator
{
    /** 默认数字格式化模板 */
    private static final String DEFAULT_NUMBER_FORMAT = "0.00";
    /** 最小百分数 */
    private static final double PERCENT_MINVALUE = 0.01;
    /** 零数值输出 */
    private static final String PERCENT_NEGATIVE_ZERO = ">-0.01";
    /** 零数值输出 */
    private static final String PERCENT_POSITIVE_ZERO = "<0.01";
    /** 转换常量。 */
    private final static int[] UNIT_TRANSCONSTS = new int[] { 1 };
    /** 流量单位。 */
    private final static String[] UNITS = new String[] { "%" };

    /**
     * 格式化数据。
     * @param data 转换值。
     * @return 转换最佳单位的浮点数。
     */
    @Override
    public String format(Object data)
    {
        double doubleValue = 0.0;
        if (data instanceof Number)
            doubleValue = ((Number) data).doubleValue();
        String text = null;
        double value = Math.abs(doubleValue);
        if (value != 0.0 && value < PERCENT_MINVALUE)
        {
            if (doubleValue < 0.0)
                text = PERCENT_NEGATIVE_ZERO;
            else
                text = PERCENT_POSITIVE_ZERO;
        }
        else
            text = super.format(doubleValue);
        return text + UNITS[0];
    }

    @Override
    protected String getDefaultFormat()
    {
        return DEFAULT_NUMBER_FORMAT;
    }

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
