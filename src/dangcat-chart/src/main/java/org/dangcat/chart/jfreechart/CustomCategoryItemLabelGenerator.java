package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataModule;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;

/**
 * Öù×´Í¼±êÇ©Éú³ÉÆ÷¡£
 *
 * @author dangcat
 */
class CustomCategoryItemLabelGenerator extends StandardCategoryItemLabelGenerator implements CategoryToolTipGenerator, CategoryURLGenerator {
    private static final long serialVersionUID = 1L;
    private CategoryChart categoryChart = null;

    CustomCategoryItemLabelGenerator(CategoryChart categoryChart) {
        this.categoryChart = categoryChart;
    }

    @Override
    public String generateLabel(CategoryDataset dataset, int row, int column) {
        String label = null;
        if (this.categoryChart.isShowItemLabel())
            label = this.getLabel(dataset, row, column);
        return label;
    }

    @Override
    public String generateToolTip(CategoryDataset dataset, int row, int column) {
        return this.getLabel(dataset, row, column);
    }

    @Override
    public String generateURL(CategoryDataset dataset, int series, int category) {
        Comparable<?> rowKey = dataset.getRowKey(series);
        Comparable<?> columnKey = dataset.getColumnKey(category);
        StringBuilder url = new StringBuilder();
        if (!ChartUtils.isNull(rowKey) || !ChartUtils.isNull(columnKey)) {
            if (!ChartUtils.isNull(rowKey)) {
                url.append(" rowKey=\"");
                url.append(rowKey);
                url.append("\"");
            }
            if (!ChartUtils.isNull(columnKey)) {
                url.append(" columnKey=\"");
                url.append(columnKey);
                url.append("\"");
            }
        }
        return url.toString();
    }

    private String getLabel(CategoryDataset dataset, int row, int column) {
        DataModule dataModule = this.categoryChart.getDataModule();
        Comparable<?> rowKey = dataset.getRowKey(row);
        Comparable<?> columnKey = dataset.getColumnKey(column);
        return dataModule.getLabel(rowKey, columnKey);
    }
}
