package org.dangcat.commons.formator;

/**
 * 速率格式化。
 * 
 */
public class VelocityFormator extends ValueFormator
{
    /** 流量单位。 */
    private final static String[] UNITS = new String[] { "/S", "K/S", "M/S", "G/S" };

    @Override
    public String[] getUnits()
    {
        return UNITS;
    }
}
