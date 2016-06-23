package org.dangcat.chart.highcharts;

public class YAxis {
    private String baseTitle = null;

    public YAxis(String baseTitle) {
        this.baseTitle = baseTitle;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        YAxis other = (YAxis) obj;
        if (baseTitle == null) {
            if (other.baseTitle != null)
                return false;
        } else if (!baseTitle.equals(other.baseTitle))
            return false;
        return true;
    }

    public String getBaseTitle() {
        return baseTitle;
    }

    public void setBaseTitle(String baseTitle) {
        this.baseTitle = baseTitle;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseTitle == null) ? 0 : baseTitle.hashCode());
        return result;
    }
}
