package org.dangcat.boot.statistics;

/**
 * 统计接口。
 * @author dangcat
 * 
 */
public interface StatisticsAble
{
    /**
     * 统计信息是否有效。
     */
    public boolean isValid();

    /**
     * 读取并重置实时信息。
     */
    public String readRealInfo();

    /**
     * 重置统计信息。
     */
    public void reset();
}
