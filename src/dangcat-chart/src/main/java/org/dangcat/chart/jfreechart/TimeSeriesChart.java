package org.dangcat.chart.jfreechart;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.dangcat.chart.jfreechart.data.DataModule;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public abstract class TimeSeriesChart extends TimeChart
{
    @Override
    protected XYDataset createXYDataset(Collection<Comparable<?>> rowKeys)
    {
        DataModule dataModule = this.getDataModule();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        for (Comparable<?> rowKey : dataModule.getRowKeys())
        {
            if (rowKeys != null && !rowKeys.contains(rowKey))
                continue;

            TimeSeries timeSeries = new TimeSeries(rowKey);
            timeSeries.setNotify(false);
            Map<Date, Double> valueMap = this.getTimeData(rowKey);
            for (Date date : valueMap.keySet())
                timeSeries.addOrUpdate(new Millisecond(date), valueMap.get(date));
            timeSeries.setNotify(true);
            timeSeriesCollection.addSeries(timeSeries);
        }
        return timeSeriesCollection;
    }

    protected void load(boolean isStacked)
    {
        this.createDataConverter(isStacked);

        this.setXYDataset(this.createXYDataset());
    }
}
