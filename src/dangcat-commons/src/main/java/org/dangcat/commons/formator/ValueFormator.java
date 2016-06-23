package org.dangcat.commons.formator;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 数值格式化。
 */
public class ValueFormator implements DataFormator {
    /**
     * 默认数字格式化模板
     */
    private static final String DEFAULT_NUMBER_FORMAT = "#.###";
    /**
     * 转换常量。
     */
    private final static int[] UNIT_TRANSCONSTS = new int[]{1, 1000, 1000, 1000};
    /**
     * 数值单位。
     */
    private final static String[] UNITS = new String[]{"", "K", "M", "G"};
    /**
     * 数字格式化模板
     */
    private String format = null;
    /**
     * 数字格式模版。
     */
    private NumberFormat numberFormat = null;

    /**
     * 取得最佳单位
     *
     * @param longValue 转换值。
     * @return 最佳单位。
     */
    public String calculatePerfectUnit(long longValue) {
        int[] transConsts = this.getTransConsts();
        String[] units = this.getUnits();
        String perfectUnit = units[0];
        double perfectValue = longValue * 1.0;
        for (int i = 1; i < units.length; i++) {
            if ((int) (perfectValue / transConsts[i]) == 0)
                break;
            perfectValue /= transConsts[i];
            perfectUnit = units[i];
        }
        return perfectUnit;
    }

    public double calculatePerfectValue(long longValue) {
        int[] transConsts = this.getTransConsts();
        double perfectValue = longValue * 1.0;
        for (int i = 1; i < transConsts.length; i++) {
            if ((int) (perfectValue / transConsts[i]) == 0)
                break;
            perfectValue /= transConsts[i];
        }
        return perfectValue;
    }

    /**
     * 计算指定单位下的转换率。
     *
     * @param perfectUnit 指定单位。
     * @return 转换率
     */
    public double calculateTransRate(String perfectUnit) {
        int[] transConsts = this.getTransConsts();
        String[] units = this.getUnits();
        long transRate = 1;
        for (int i = 0; i < units.length; i++) {
            transRate *= transConsts[i];
            if (perfectUnit.equals(units[i]))
                break;
        }
        return 1.0 / transRate;
    }

    @Override
    public String format(Object data) {
        String text = null;
        if (data instanceof Long) {
            Long longValue = (Long) data;
            String perfectUnit = this.calculatePerfectUnit(longValue);
            double value = longValue * this.calculateTransRate(perfectUnit);
            text = this.getNumberFormat().format(value) + perfectUnit;
        } else if (data instanceof Double)
            text = this.getNumberFormat().format(data);
        return text;
    }

    protected String getDefaultFormat() {
        return DEFAULT_NUMBER_FORMAT;
    }

    public String getFormat() {
        return this.format == null ? this.getDefaultFormat() : this.format;
    }

    public void setFormat(String format) {
        this.format = format;
        this.numberFormat = null;
    }

    protected NumberFormat getNumberFormat() {
        if (this.numberFormat == null)
            this.numberFormat = new DecimalFormat(this.getFormat());
        return this.numberFormat;
    }

    public int[] getTransConsts() {
        return UNIT_TRANSCONSTS;
    }

    public String[] getUnits() {
        return UNITS;
    }
}
