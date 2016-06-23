package org.dangcat.chart.highcharts;

public class StackedValue extends DataValue
{
    private String area;
    private String city;

    public StackedValue(String city, String area, String name, long value)
    {
        super(name, value);
        this.city = city;
        this.area = area;
    }

    public String getArea()
    {
        return area;
    }

    public void setArea(String area)
    {
        this.area = area;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }
}
