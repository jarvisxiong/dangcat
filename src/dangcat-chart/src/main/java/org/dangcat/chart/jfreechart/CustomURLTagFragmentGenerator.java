package org.dangcat.chart.jfreechart;

import org.jfree.chart.imagemap.ImageMapUtilities;
import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.URLTagFragmentGenerator;

class CustomURLTagFragmentGenerator implements URLTagFragmentGenerator, ToolTipTagFragmentGenerator {
    @Override
    public String generateToolTipFragment(String toolTipText) {
        return " title=\"" + ImageMapUtilities.htmlEscape(toolTipText) + "\"";
    }

    @Override
    public String generateURLFragment(String urlText) {
        return urlText;
    }
}
