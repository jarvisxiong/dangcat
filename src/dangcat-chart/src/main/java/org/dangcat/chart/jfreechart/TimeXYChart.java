package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.commons.utils.DateUtils;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

public abstract class TimeXYChart extends TimeChart
{
    @Override
    protected XYDataset createXYDataset(Collection<Comparable<?>> rowKeys)
    {
        DataModule dataModule = this.getDataModule();
        TimeTableXYDataset timeTableXYDataset = new TimeTableXYDataset();
        for (Comparable<?> rowKey : dataModule.getRowKeys())
        {
            if (rowKeys != null && !rowKeys.contains(rowKey))
                continue;

            Map<Date, Double> valueMap = this.getTimeData(rowKey);
            for (Date date : valueMap.keySet())
            {
                Double value = valueMap.get(date);
                if (value == null)
                    value = ZREO;
                Date endTime = DateUtils.add(DateUtils.SECOND, date, this.getTimeStep());
                timeTableXYDataset.add(new SimpleTimePeriod(date, endTime), value, (String) rowKey);
            }
        }
        return timeTableXYDataset;
    }

    protected void load(boolean isStacked)
    {
        this.createDataConverter(isStacked);

        this.setXYDataset(this.createXYDataset());
    }
}
