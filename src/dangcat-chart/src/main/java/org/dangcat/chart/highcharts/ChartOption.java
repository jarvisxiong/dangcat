package org.dangcat.chart.highcharts;

import org.dangcat.commons.serialize.annotation.Serialize;

import java.util.Collection;
import java.util.LinkedList;

public class ChartOption {
    private Object data = null;
    private Integer height = null;
    private String logic = null;
    private Collection<Column> primaryKeyColumns = null;
    private Boolean showLegend = null;
    private String subtitle = null;
    private String title = null;
    private ChartType type = null;
    private Collection<Column> valueColumns = null;
    private Integer width = null;

    public void addPrimaryKeyColumn(Column column) {
        if (column != null) {
            if (this.primaryKeyColumns == null)
                this.primaryKeyColumns = new LinkedList<Column>();
            if (!this.primaryKeyColumns.contains(column))
                this.primaryKeyColumns.add(column);
        }
    }

    public void addValueColumn(Column column) {
        if (column != null) {
            if (this.valueColumns == null)
                this.valueColumns = new LinkedList<Column>();
            if (!this.valueColumns.contains(column))
                this.valueColumns.add(column);
        }
    }

    @Serialize(ignore = true)
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    public Collection<Column> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    public Boolean getShowLegend() {
        return showLegend;
    }

    public void setShowLegend(Boolean showLegend) {
        this.showLegend = showLegend;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ChartType getType() {
        return type;
    }

    public Collection<Column> getValueColumns() {
        return valueColumns;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }
}
