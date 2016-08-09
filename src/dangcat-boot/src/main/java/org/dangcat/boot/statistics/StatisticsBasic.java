package org.dangcat.boot.statistics;

import org.dangcat.commons.utils.ValueUtils;

/**
 * 数据处理统计。
 *
 * @author dangcat
 */
public abstract class StatisticsBasic implements StatisticsAble {
    /**
     * 实时数据
     */
    private static final String STATISTICS_REAL = "Real";
    /**
     * 合计数据
     */
    private static final String STATISTICS_TOTAL = "Total";
    /**
     * 统计名称。
     */
    private String name;
    /**
     * 实时数据
     */
    private StatisticsBase realData = null;
    /**
     * 总计数据。
     */
    private StatisticsBase totalData = null;

    public StatisticsBasic(String name) {
        this.name = name;
    }

    public long begin() {
        return this.begin(StatisticsBase.TimeCost);
    }

    public long begin(String name) {
        return this.getRealData().begin(name);
    }

    protected abstract StatisticsBase creatStatisticsData(String name);

    public void end() {
        this.getRealData().end(StatisticsBase.TimeCost);
    }

    public void end(long beginTime) {
        this.getRealData().end(StatisticsBase.TimeCost, beginTime);
    }

    public void end(String name) {
        this.getRealData().end(name);
    }

    public void end(String name, long beginTime) {
        this.getRealData().end(name, beginTime);
    }

    public String getName() {
        return this.name;
    }

    protected StatisticsBase getRealData() {
        if (this.realData == null)
            this.realData = this.creatStatisticsData(STATISTICS_REAL);
        return this.realData;
    }

    protected StatisticsBase getTotalData() {
        if (this.totalData == null)
            this.totalData = this.creatStatisticsData(STATISTICS_TOTAL);
        return this.totalData;
    }

    public long getValue(String name) {
        return this.getRealData().getValue(name);
    }

    protected long increase(String name) {
        return this.getRealData().increase(name);
    }

    protected long increase(String name, long value) {
        return this.getRealData().increase(name, value);
    }

    @Override
    public boolean isValid() {
        return this.getTotalData().isValid() || this.getRealData().isValid();
    }

    @Override
    public String readRealInfo() {
        StatisticsBase realData = this.getRealData();
        this.realData = null;
        this.getTotalData().assign(realData);
        return this.toString(realData, this.getTotalData());
    }

    @Override
    public void reset() {
        this.realData = null;
        this.totalData = null;
    }

    protected long setValue(String name, long value) {
        return this.getRealData().setValue(name, value);
    }

    /**
     * 显示统计信息。
     */
    @Override
    public String toString() {
        return this.toString(this.getRealData(), this.getTotalData());
    }

    private String toString(StatisticsBase realData, StatisticsBase totalData) {
        StringBuilder info = new StringBuilder();
        // 统计类型名称。
        info.append(this.getName());
        info.append(": ");
        String realDataText = realData.toString();
        if (!ValueUtils.isEmpty(realDataText)) {
            info.append(realDataText);
            info.append("\r\n\t");
        }
        info.append(totalData);
        return info.toString();
    }
}
