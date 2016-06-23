package org.dangcat.commons.reflect;

import junit.framework.Assert;
import org.dangcat.commons.reflect.examples.FieldValue2;
import org.junit.Test;

public class TestFieldReflect {
    @Test
    public void testFieldReflect() {
        FieldValue2 instance = new FieldValue2();
        ReflectUtils.setFieldValue(instance, "value1", 100);
        ReflectUtils.setFieldValue(instance, "value2", 200);
        Assert.assertEquals(100, ReflectUtils.getFieldValue(instance, "value1"));
        Assert.assertEquals(200, ReflectUtils.getFieldValue(instance, "value2"));
    }
}
