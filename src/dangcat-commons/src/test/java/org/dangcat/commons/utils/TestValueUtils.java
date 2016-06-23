package org.dangcat.commons.utils;

import org.junit.Assert;
import org.junit.Test;

public class TestValueUtils
{
    @Test
    public void testEmpty()
    {
        Assert.assertTrue(ValueUtils.isEmpty(null));
        Assert.assertTrue(ValueUtils.isEmpty(""));
        Assert.assertTrue(ValueUtils.isEmpty("    "));
        Assert.assertTrue(ValueUtils.isEmpty(new Character[] {}));
        Assert.assertTrue(ValueUtils.isEmpty(new Byte[] {}));
        Assert.assertTrue(ValueUtils.isEmpty(new char[] {}));
        Assert.assertTrue(ValueUtils.isEmpty(new byte[] {}));
        Assert.assertTrue(ValueUtils.isEmpty(new Integer[] {}));

        Assert.assertFalse(ValueUtils.isEmpty(10));
        Assert.assertFalse(ValueUtils.isEmpty("a"));
        Assert.assertFalse(ValueUtils.isEmpty(" s   "));
        Assert.assertFalse(ValueUtils.isEmpty(new Character[] { 'A' }));
        Assert.assertFalse(ValueUtils.isEmpty(new Byte[] { 1 }));
        Assert.assertFalse(ValueUtils.isEmpty(new char[] { '2' }));
        Assert.assertFalse(ValueUtils.isEmpty(new byte[] { 1 }));
        Assert.assertFalse(ValueUtils.isEmpty(new Integer[] { 10 }));
    }

    @Test
    public void testNumber()
    {
        Long value = TextParser.parseLong("1.881E9", null);
        Assert.assertEquals((Long) 1881000000l, value);
    }
}
