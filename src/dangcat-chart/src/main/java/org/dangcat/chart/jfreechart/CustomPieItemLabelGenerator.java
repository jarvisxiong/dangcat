package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.commons.utils.ValueUtils;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.data.general.PieDataset;

import java.text.AttributedString;

/**
 * ±ý×´Í¼±êÇ©Éú³ÉÆ÷¡£
 *
 * @author dangcat
 */
class CustomPieItemLabelGenerator implements PieSectionLabelGenerator, PieToolTipGenerator, PieURLGenerator {
    private static final long serialVersionUID = 1L;
    private PieChart pieChart = null;

    CustomPieItemLabelGenerator(PieChart pieChart) {
        this.pieChart = pieChart;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AttributedString generateAttributedSectionLabel(PieDataset arg0, Comparable arg1) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String generateSectionLabel(PieDataset dataset, Comparable key) {
        return this.getLabel(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String generateToolTip(PieDataset dataset, Comparable key) {
        return this.getLabel(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String generateURL(PieDataset dataset, Comparable key, int pieIndex) {
        return " key=\"" + key + "\"";
    }

    @SuppressWarnings("unchecked")
    private String getLabel(Comparable key) {
        DataModule dataModule = this.pieChart.getDataModule();
        String label = dataModule.getLabel(ChartUtils.NULL, key);
        if (ValueUtils.isEmpty(label))
            label = key.toString();
        return label;
    }
}
