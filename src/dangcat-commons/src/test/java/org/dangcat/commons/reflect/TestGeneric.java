package org.dangcat.commons.reflect;

import org.dangcat.commons.reflect.examples.OperatorService;
import org.dangcat.commons.serialize.json.MethodInfoSerializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class TestGeneric {
    @Test
    public void testMethodInfoSerializer() throws IOException {
        Map<String, MethodInfo> methodInfoMap = new HashMap<String, MethodInfo>();
        methodInfoMap = GenericUtils.getMethodInfoMap(OperatorService.class);
        StringWriter writer = new StringWriter();
        MethodInfoSerializer.serialize(methodInfoMap.values(), writer);
        System.out.println(writer.toString());
        Assert.assertNotNull(writer.toString());
    }
}
