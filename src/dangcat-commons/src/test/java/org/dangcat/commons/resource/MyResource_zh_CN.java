package org.dangcat.commons.resource;

import java.awt.Color;
import java.util.ListResourceBundle;

public class MyResource_zh_CN extends ListResourceBundle
{
    private static final Object[][] contens = { { "backgroundColor", Color.RED }, { "defaultPagerSize", new double[] { 200, 220 } } };

    @Override
    protected Object[][] getContents()
    {
        return contens;
    }
}
