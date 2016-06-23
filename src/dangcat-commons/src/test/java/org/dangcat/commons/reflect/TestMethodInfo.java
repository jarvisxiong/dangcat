package org.dangcat.commons.reflect;

import junit.framework.Assert;
import org.dangcat.commons.reflect.examples.MethodExample1;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestMethodInfo
{
    private Map<String, MethodInfo> methodInfoMap = GenericUtils.getMethodInfoMap(MethodExample1.class);

    @Test
    public void testMethodExecute1()
    {
        MethodInfo method = methodInfoMap.get("execute1");
        ParamInfo[] paramInfos = method.getParamInfos();

        ParamInfo paramInfo = paramInfos[0];
        Assert.assertEquals("intParam", paramInfo.getName());
        Assert.assertEquals(int.class, paramInfo.getClassType());

        paramInfo = paramInfos[1];
        Assert.assertEquals("stringParam", paramInfo.getName());
        Assert.assertEquals(String.class, paramInfo.getClassType());

        paramInfo = paramInfos[2];
        Assert.assertEquals("booleanParam", paramInfo.getName());
        Assert.assertEquals(boolean.class, paramInfo.getClassType());

        paramInfo = paramInfos[3];
        Assert.assertEquals("doubleParam", paramInfo.getName());
        Assert.assertEquals(double.class, paramInfo.getClassType());

        paramInfo = paramInfos[4];
        Assert.assertEquals("charParam", paramInfo.getName());
        Assert.assertEquals(char.class, paramInfo.getClassType());

        paramInfo = paramInfos[5];
        Assert.assertEquals("byteParam", paramInfo.getName());
        Assert.assertEquals(byte.class, paramInfo.getClassType());

        paramInfo = paramInfos[6];
        Assert.assertEquals("charsParam", paramInfo.getName());
        Assert.assertEquals(char[].class, paramInfo.getClassType());

        paramInfo = paramInfos[7];
        Assert.assertEquals("bytesParam", paramInfo.getName());
        Assert.assertEquals(byte[].class, paramInfo.getClassType());

        paramInfo = paramInfos[8];
        Assert.assertEquals("dateParam", paramInfo.getName());
        Assert.assertEquals(Date.class, paramInfo.getClassType());
    }

    @Test
    public void testMethodExecute2()
    {
        MethodInfo method = methodInfoMap.get("execute2");
        ParamInfo[] paramInfos = method.getParamInfos();

        ParamInfo paramInfo = paramInfos[0];
        Assert.assertEquals("intParam", paramInfo.getName());
        Assert.assertEquals(Integer.class, paramInfo.getClassType());

        paramInfo = paramInfos[1];
        Assert.assertEquals("stringParam", paramInfo.getName());
        Assert.assertEquals(String.class, paramInfo.getClassType());

        paramInfo = paramInfos[2];
        Assert.assertEquals("booleanParam", paramInfo.getName());
        Assert.assertEquals(Boolean.class, paramInfo.getClassType());

        paramInfo = paramInfos[3];
        Assert.assertEquals("doubleParam", paramInfo.getName());
        Assert.assertEquals(Double.class, paramInfo.getClassType());

        paramInfo = paramInfos[4];
        Assert.assertEquals("charParam", paramInfo.getName());
        Assert.assertEquals(Character.class, paramInfo.getClassType());

        paramInfo = paramInfos[5];
        Assert.assertEquals("byteParam", paramInfo.getName());
        Assert.assertEquals(Byte.class, paramInfo.getClassType());

        paramInfo = paramInfos[6];
        Assert.assertEquals("charsParam", paramInfo.getName());
        Assert.assertEquals(Character[].class, paramInfo.getClassType());

        paramInfo = paramInfos[7];
        Assert.assertEquals("bytesParam", paramInfo.getName());
        Assert.assertEquals(Byte[].class, paramInfo.getClassType());

        paramInfo = paramInfos[8];
        Assert.assertEquals("dateParam", paramInfo.getName());
        Assert.assertEquals(Date.class, paramInfo.getClassType());
    }

    @Test
    public void testMethodExecute3()
    {
        MethodInfo method = methodInfoMap.get("execute3");
        ParamInfo[] paramInfos = method.getParamInfos();

        ParamInfo paramInfo = paramInfos[0];
        Assert.assertEquals("intList", paramInfo.getName());
        Assert.assertEquals(List.class, paramInfo.getClassType());
        Assert.assertEquals(Integer.class, paramInfo.getParameterizedClasses()[0]);

        paramInfo = paramInfos[1];
        Assert.assertEquals("stringList", paramInfo.getName());
        Assert.assertEquals(List.class, paramInfo.getClassType());
        Assert.assertEquals(String.class, paramInfo.getParameterizedClasses()[0]);

        paramInfo = paramInfos[2];
        Assert.assertEquals("booleanList", paramInfo.getName());
        Assert.assertEquals(List.class, paramInfo.getClassType());
        Assert.assertEquals(Boolean.class, paramInfo.getParameterizedClasses()[0]);

        paramInfo = paramInfos[3];
        Assert.assertEquals("doubleList", paramInfo.getName());
        Assert.assertEquals(List.class, paramInfo.getClassType());
        Assert.assertEquals(Double.class, paramInfo.getParameterizedClasses()[0]);

        paramInfo = paramInfos[4];
        Assert.assertEquals("charList", paramInfo.getName());
        Assert.assertEquals(List.class, paramInfo.getClassType());
        Assert.assertEquals(Character.class, paramInfo.getParameterizedClasses()[0]);

        paramInfo = paramInfos[5];
        Assert.assertEquals("byteList", paramInfo.getName());
        Assert.assertEquals(List.class, paramInfo.getClassType());
        Assert.assertEquals(Byte.class, paramInfo.getParameterizedClasses()[0]);

        paramInfo = paramInfos[6];
        Assert.assertEquals("charsList", paramInfo.getName());
        Assert.assertEquals(List.class, paramInfo.getClassType());
        Assert.assertEquals(Character[].class, paramInfo.getParameterizedClasses()[0]);

        paramInfo = paramInfos[7];
        Assert.assertEquals("bytesList", paramInfo.getName());
        Assert.assertEquals(List.class, paramInfo.getClassType());
        Assert.assertEquals(Byte[].class, paramInfo.getParameterizedClasses()[0]);

        paramInfo = paramInfos[8];
        Assert.assertEquals("dateList", paramInfo.getName());
        Assert.assertEquals(List.class, paramInfo.getClassType());
        Assert.assertEquals(Date.class, paramInfo.getParameterizedClasses()[0]);
    }

    @Test
    public void testMethodExecute4()
    {
        MethodInfo method = methodInfoMap.get("execute4");
        ParamInfo[] paramInfos = method.getParamInfos();

        ParamInfo paramInfo = paramInfos[0];
        Assert.assertEquals("intMap", paramInfo.getName());
        Assert.assertEquals(Map.class, paramInfo.getClassType());
        Class<?>[] paramClasses = paramInfo.getParameterizedClasses();
        Assert.assertEquals(Integer.class, paramClasses[0]);
        Assert.assertEquals(Integer.class, paramClasses[1]);

        paramInfo = paramInfos[1];
        Assert.assertEquals("stringMap", paramInfo.getName());
        Assert.assertEquals(Map.class, paramInfo.getClassType());
        paramClasses = paramInfo.getParameterizedClasses();
        Assert.assertEquals(String.class, paramClasses[0]);
        Assert.assertEquals(String.class, paramClasses[1]);

        paramInfo = paramInfos[2];
        Assert.assertEquals("booleanMap", paramInfo.getName());
        Assert.assertEquals(Map.class, paramInfo.getClassType());
        paramClasses = paramInfo.getParameterizedClasses();
        Assert.assertEquals(Boolean.class, paramClasses[0]);
        Assert.assertEquals(Boolean.class, paramClasses[1]);

        paramInfo = paramInfos[3];
        Assert.assertEquals("doubleMap", paramInfo.getName());
        Assert.assertEquals(Map.class, paramInfo.getClassType());
        paramClasses = paramInfo.getParameterizedClasses();
        Assert.assertEquals(Double.class, paramClasses[0]);
        Assert.assertEquals(Double.class, paramClasses[1]);

        paramInfo = paramInfos[4];
        Assert.assertEquals("charMap", paramInfo.getName());
        Assert.assertEquals(Map.class, paramInfo.getClassType());
        paramClasses = paramInfo.getParameterizedClasses();
        Assert.assertEquals(Character.class, paramClasses[0]);
        Assert.assertEquals(Character.class, paramClasses[1]);

        paramInfo = paramInfos[5];
        Assert.assertEquals("byteMap", paramInfo.getName());
        Assert.assertEquals(Map.class, paramInfo.getClassType());
        paramClasses = paramInfo.getParameterizedClasses();
        Assert.assertEquals(Byte.class, paramClasses[0]);
        Assert.assertEquals(Byte.class, paramClasses[1]);

        paramInfo = paramInfos[6];
        Assert.assertEquals("charsMap", paramInfo.getName());
        Assert.assertEquals(Map.class, paramInfo.getClassType());
        paramClasses = paramInfo.getParameterizedClasses();
        Assert.assertEquals(Character[].class, paramClasses[0]);
        Assert.assertEquals(Character[].class, paramClasses[1]);

        paramInfo = paramInfos[7];
        Assert.assertEquals("bytesMap", paramInfo.getName());
        Assert.assertEquals(Map.class, paramInfo.getClassType());
        paramClasses = paramInfo.getParameterizedClasses();
        Assert.assertEquals(Byte[].class, paramClasses[0]);
        Assert.assertEquals(Byte[].class, paramClasses[1]);
    }
}
