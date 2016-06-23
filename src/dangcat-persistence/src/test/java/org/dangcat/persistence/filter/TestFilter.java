package org.dangcat.persistence.filter;

import junit.framework.Assert;
import org.junit.Test;

public class TestFilter {
    @Test
    public void testRangeFilter() {
        String expected = "FieldA > 100";
        String[] fieldNames = new String[]{"FieldA"};
        Object[] values = new Object[]{100};
        FilterExpress filterExpress = FilterUtils.createRangeFilter(fieldNames, values, FilterType.gt);
        Assert.assertEquals(expected, filterExpress.toString());

        expected = "(FieldA > 100 OR (FieldA = 100 AND FieldB > 'BBBB') OR (FieldA = 100 AND FieldB = 'BBBB' AND FieldC >= 135.67))";
        fieldNames = new String[]{"FieldA", "FieldB", "FieldC"};
        values = new Object[]{100, "BBBB", 135.67};
        filterExpress = FilterUtils.createRangeFilter(fieldNames, values, FilterType.ge);
        Assert.assertEquals(expected, filterExpress.toString());

        expected = "(FieldA > 100 OR (FieldA = 100 AND FieldB > 'BBBB') OR (FieldA = 100 AND FieldB = 'BBBB' AND FieldC > 135.67))";
        filterExpress = FilterUtils.createRangeFilter(fieldNames, values, FilterType.gt);
        Assert.assertEquals(expected, filterExpress.toString());

        expected = "(FieldA < 100 OR (FieldA = 100 AND FieldB < 'BBBB') OR (FieldA = 100 AND FieldB = 'BBBB' AND FieldC < 135.67))";
        filterExpress = FilterUtils.createRangeFilter(fieldNames, values, FilterType.lt);
        Assert.assertEquals(expected, filterExpress.toString());

        expected = "(FieldA < 100 OR (FieldA = 100 AND FieldB < 'BBBB') OR (FieldA = 100 AND FieldB = 'BBBB' AND FieldC <= 135.67))";
        filterExpress = FilterUtils.createRangeFilter(fieldNames, values, FilterType.le);
        Assert.assertEquals(expected, filterExpress.toString());
    }
}
