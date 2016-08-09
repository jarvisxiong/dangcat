package org.dangcat.commons.formator;

/**
 * 流速格式化。
 */
public class OctetsVelocityFormator extends ValueFormator {
    /**
     * 转换常量。
     */
    private final static int[] UNIT_TRANSCONSTS = new int[]{1, 1024, 1024, 1024, 1024, 1024};
    /**
     * traffic units
     */
    private final static String[] UNITS = new String[]{"B/S", "KB/S", "MB/S", "GB/S", "TB/S", "PB/S"};

    @Override
    public int[] getTransConsts() {
        return UNIT_TRANSCONSTS;
    }

    @Override
    public String[] getUnits() {
        return UNITS;
    }
}
