package org.dangcat.chart.highcharts;

public class Column
{
    private String logic = null;
    private String name = null;
    private String stack = null;
    private String title = null;
    private Integer yAxis = null;

    public Column(String name)
    {
        this.name = name;
    }

    public Column(String name, String title)
    {
        this.name = name;
        this.title = title;
    }

    public Column(String name, String title, String logic)
    {
        this.name = name;
        this.title = title;
        this.logic = logic;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Column other = (Column) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }

    public String getLogic()
    {
        return logic;
    }

    public String getName()
    {
        return name;
    }

    public String getStack()
    {
        return stack;
    }

    public String getTitle()
    {
        return title;
    }

    public Integer getYAxis()
    {
        return yAxis;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public void setLogic(String logic)
    {
        this.logic = logic;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setStack(String stack)
    {
        this.stack = stack;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setYAxis(Integer yAxis)
    {
        this.yAxis = yAxis;
    }
}
