package org.dangcat.commons.resource;

import java.awt.*;
import java.util.ListResourceBundle;

public class MyResource_en_US extends ListResourceBundle
{
    private static final Object[][] contens = { { "backgroundColor", Color.BLACK }, { "defaultPagerSize", new double[] { 100, 110 } } };

    @Override
    protected Object[][] getContents()
    {
        return contens;
    }
}
