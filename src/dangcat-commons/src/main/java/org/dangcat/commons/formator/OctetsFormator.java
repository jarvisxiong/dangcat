package org.dangcat.commons.formator;

/**
 * 流量格式化。
 */
public class OctetsFormator extends ValueFormator {
    /**
     * 转换常量。
     */
    private final static int[] UNIT_TRANSCONSTS = new int[]{1, 1024, 1024, 1024, 1024, 1024};
    /**
     * traffic units
     */
    private final static String[] UNITS = new String[]{"Byte", "KB", "MB", "GB", "TB", "PB"};

    @Override
    public int[] getTransConsts() {
        return UNIT_TRANSCONSTS;
    }

    @Override
    public String[] getUnits() {
        return UNITS;
    }
}
