package org.dangcat.commons.reflect;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.dangcat.commons.reflect.GenericUtils;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.reflect.ParamInfo;
import org.dangcat.commons.reflect.examples.MethodExample2;
import org.junit.Test;

public class TestGennericClass
{
    private Map<String, MethodInfo> methodInfoMap = GenericUtils.getMethodInfoMap(MethodExample2.class);

    @Test
    public void testMethodExecute1()
    {
        MethodInfo method = methodInfoMap.get("execute1");
        ParamInfo[] paramInfos = method.getParamInfos();

        ParamInfo paramInfo = paramInfos[0];
        Assert.assertEquals("tParam", paramInfo.getName());
        Assert.assertEquals(Integer.class, paramInfo.getClassType());

        paramInfo = paramInfos[1];
        Assert.assertEquals("kParam", paramInfo.getName());
        Assert.assertEquals(String.class, paramInfo.getClassType());
    }

    @Test
    public void testMethodExecute2()
    {
        MethodInfo method = methodInfoMap.get("execute2");
        ParamInfo[] paramInfos = method.getParamInfos();

        ParamInfo paramInfo = paramInfos[0];
        Assert.assertEquals("tList", paramInfo.getName());
        Assert.assertEquals(List.class, paramInfo.getClassType());
        Assert.assertEquals(Integer.class, paramInfo.getParameterizedClasses()[0]);

        paramInfo = paramInfos[1];
        Assert.assertEquals("kList", paramInfo.getName());
        Assert.assertEquals(List.class, paramInfo.getClassType());
        Assert.assertEquals(String.class, paramInfo.getParameterizedClasses()[0]);
    }

    @Test
    public void testMethodExecute3()
    {
        MethodInfo method = methodInfoMap.get("execute3");
        ParamInfo[] paramInfos = method.getParamInfos();

        ParamInfo paramInfo = paramInfos[0];
        Assert.assertEquals("tMap", paramInfo.getName());
        Assert.assertEquals(Map.class, paramInfo.getClassType());
        Class<?>[] paramClasses = paramInfo.getParameterizedClasses();
        Assert.assertEquals(Integer.class, paramClasses[0]);
        Assert.assertEquals(String.class, paramClasses[1]);

        paramInfo = paramInfos[1];
        Assert.assertEquals("kMap", paramInfo.getName());
        Assert.assertEquals(Map.class, paramInfo.getClassType());
        paramClasses = paramInfo.getParameterizedClasses();
        Assert.assertEquals(String.class, paramClasses[0]);
        Assert.assertEquals(Integer.class, paramClasses[1]);
    }

    @Test
    public void testMethodExecute4()
    {
        MethodInfo method = methodInfoMap.get("execute4");
        ParamInfo[] paramInfos = method.getParamInfos();

        ParamInfo paramInfo = paramInfos[0];
        Assert.assertEquals("tArray", paramInfo.getName());
        Assert.assertEquals(Integer[].class, paramInfo.getClassType());
        Assert.assertEquals(Integer.class, paramInfo.getParameterizedClasses()[0]);

        paramInfo = paramInfos[1];
        Assert.assertEquals("kArray", paramInfo.getName());
        Assert.assertEquals(String[].class, paramInfo.getClassType());
        Assert.assertEquals(String.class, paramInfo.getParameterizedClasses()[0]);
    }
}
