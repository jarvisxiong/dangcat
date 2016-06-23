package org.dangcat.commons.utils;

import junit.framework.Assert;
import org.junit.Test;

public class TestMathUtils {
    @Test
    public void testDivide() {
        Assert.assertTrue(ValueUtils.compare(5, MathUtils.divide(10, 2)) == 0);
        Assert.assertTrue(ValueUtils.compare(3.33333, MathUtils.divide(10, 3)) == 0);
        Assert.assertTrue(ValueUtils.compare(3.33334, MathUtils.divide(10.000019, 3)) == 0);
        Assert.assertTrue(ValueUtils.compare(2.47, MathUtils.divide(12.36, 5, 2)) == 0);
        Assert.assertTrue(ValueUtils.compare(2.5, MathUtils.divide(12.36, 5, 1)) == 0);
    }

    @Test
    public void testMulti() {
        Assert.assertTrue(ValueUtils.compare(20, MathUtils.multi(10, 2)) == 0);
        Assert.assertTrue(ValueUtils.compare(30.6, MathUtils.multi(10.2, 3)) == 0);
        Assert.assertEquals(new Integer(30), MathUtils.multi(new Short("5"), new Integer(6)));
    }

    @Test
    public void testPlus() {
        Assert.assertTrue(ValueUtils.compare(12, MathUtils.plus(10, 2)) == 0);
        Assert.assertTrue(ValueUtils.compare(13.2, MathUtils.plus(10.2, 3)) == 0);
        Assert.assertEquals(new Integer(11), MathUtils.plus(new Short("5"), new Integer(6)));
    }

    @Test
    public void testSubtract() {
        Assert.assertTrue(ValueUtils.compare(8, MathUtils.subtract(10, 2)) == 0);
        Assert.assertTrue(ValueUtils.compare(7.2, MathUtils.subtract(10.2, 3)) == 0);
        Assert.assertEquals(new Integer(-1), MathUtils.subtract(new Short("5"), new Integer(6)));
    }
}
