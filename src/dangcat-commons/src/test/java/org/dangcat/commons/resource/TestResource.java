package org.dangcat.commons.resource;

import java.awt.Color;
import java.util.Locale;

import junit.framework.Assert;

import org.dangcat.commons.resource.ResourceHelper;
import org.junit.Test;

public class TestResource
{
    @Test
    public void testResourceBundle()
    {
        ResourceHelper chinaResourceHelper = new ResourceHelper(TestResource.class, Locale.SIMPLIFIED_CHINESE, "MyResource");
        for (int i = 0; i < 5; i++)
            Assert.assertEquals("我的祖国" + i, chinaResourceHelper.getText("dangcat.title" + i));
        Assert.assertEquals(Color.RED, (Color) chinaResourceHelper.getObject("backgroundColor"));
        double[] values = (double[]) chinaResourceHelper.getObject("defaultPagerSize");
        Assert.assertEquals(2, values.length);
        Assert.assertEquals(200.0, values[0]);
        Assert.assertEquals(220.0, values[1]);

        ResourceHelper usResourceHelper = new ResourceHelper(TestResource.class, Locale.US, "MyResource");
        for (int i = 0; i < 5; i++)
            Assert.assertEquals("My hometown" + i, usResourceHelper.getText("dangcat.title" + i));
        Assert.assertEquals(Color.BLACK, (Color) usResourceHelper.getObject("backgroundColor"));
        values = (double[]) usResourceHelper.getObject("defaultPagerSize");
        Assert.assertEquals(2, values.length);
        Assert.assertEquals(100.0, values[0]);
        Assert.assertEquals(110.0, values[1]);
    }

    @Test
    public void testResourceHelper()
    {
        ResourceHelper chinaResourceHelper = new ResourceHelper(TestResource.class, Locale.SIMPLIFIED_CHINESE);
        for (int i = 0; i < 6; i++)
            Assert.assertEquals("我的祖国" + i, chinaResourceHelper.getText("dangcat.title" + i));

        ResourceHelper usResourceHelper = new ResourceHelper(TestResource.class, Locale.US);
        for (int i = 0; i < 6; i++)
            Assert.assertEquals("My hometown" + i, usResourceHelper.getText("dangcat.title" + i));
    }
}
