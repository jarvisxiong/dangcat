package org.dangcat.persistence.model;

import org.dangcat.persistence.exception.TableException;
import org.junit.Assert;
import org.junit.Test;

public class TestModelField
{
    @Test
    public void testField() throws TableException
    {
        Field shortField = Field.newInstance();
        shortField.setObject(Short.valueOf("5"));
        Field integerField = Field.newInstance();
        integerField.setObject(Integer.valueOf("7"));
        Assert.assertTrue(shortField.compareTo(integerField) < 0);

        Field integerField2 = Field.newInstance();
        integerField2.setObject(Integer.valueOf("5"));
        Assert.assertTrue(shortField.compareTo(integerField2) == 0);

        Field integerField3 = Field.newInstance();
        integerField3.setObject(Integer.valueOf("0"));
        Assert.assertTrue(shortField.compareTo(integerField3) > 0);
    }
}
