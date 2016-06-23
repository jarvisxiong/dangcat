package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataConverter;
import org.dangcat.commons.formator.DateFormator;
import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.utils.DateUtils;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

class CustomXYItemLabelGenerator implements XYToolTipGenerator, XYURLGenerator
{
    private DateType dateType = DateType.Second;
    private String format = "{0} {1}: {2}";
    private int index = 0;
    private String numberFormat = "#.###";
    private XYToolTipGenerator standXYToolTipGenerator = null;
    private TimeChart timeChart = null;

    public CustomXYItemLabelGenerator(TimeChart timeChart, int index)
    {
        this.timeChart = timeChart;
        this.index = index;
    }

    @Override
    public String generateToolTip(XYDataset dataset, int series, int item)
    {
        XYToolTipGenerator xyToolTipGenerator = this.getStandXYToolTipGenerator();
        String text = xyToolTipGenerator.generateToolTip(dataset, series, item);
        DataConverter dataConverter = this.timeChart.getDataConverter(this.index);
        if (dataConverter != null)
            text += dataConverter.getUnit();
        return text;
    }

    @Override
    public String generateURL(XYDataset dataset, int series, int item)
    {
        Number number = dataset.getY(series, item);
        if (number == null)
            return null;

        StringBuilder url = new StringBuilder();
        Comparable<?> rowKey = dataset.getSeriesKey(series);
        Date columnKey = new Date(number.longValue());
        url.append(" rowKey=\"");
        url.append(rowKey);
        url.append("\"");
        url.append(" columnKey=\"");
        url.append(DateUtils.format(columnKey, DateType.Second));
        url.append("\"");
        return url.toString();
    }

    public DateType getDateType()
    {
        return dateType;
    }

    public void setDateType(DateType dateType) {
        this.dateType = dateType;
        this.standXYToolTipGenerator = null;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
        this.standXYToolTipGenerator = null;
    }

    private XYToolTipGenerator getStandXYToolTipGenerator()
    {
        if (this.standXYToolTipGenerator == null)
        {
            DateFormat dateFormat = DateFormator.getDateFormat(this.dateType);
            NumberFormat numberFormat = new DecimalFormat(this.numberFormat);
            this.standXYToolTipGenerator = new StandardXYToolTipGenerator(this.format, dateFormat, numberFormat);
        }
        return this.standXYToolTipGenerator;
    }
}
