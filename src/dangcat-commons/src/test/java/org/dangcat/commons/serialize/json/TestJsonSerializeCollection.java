package org.dangcat.commons.serialize.json;

import junit.framework.Assert;
import org.dangcat.commons.utils.ValueUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class TestJsonSerializeCollection
{
    @Test
    public void testArray() throws IOException
    {
        String[] srcArray = new String[100];
        for (int i = 0; i < 100; i++)
            srcArray[i] = "value " + i;
        String data = JsonSerializer.serialize(srcArray);
        StringReader reader = new StringReader(data);
        String[] destArray = JsonDeserializer.deserialize(reader, new Class[]{srcArray.getClass(), String.class}, null);
        Assert.assertEquals(srcArray.length, destArray.length);
        Assert.assertEquals(0, ValueUtils.compare(srcArray, destArray));
    }

    @Test
    public void testCollection() throws IOException
    {
        Collection<Integer> srcCollection = new ArrayList<Integer>();
        Collection<Integer> destCollection = new ArrayList<Integer>();
        this.testCollection(srcCollection, destCollection);

        Collection<Integer> srcHashSet = new HashSet<Integer>();
        Collection<Integer> destHashSet = new HashSet<Integer>();
        this.testCollection(srcHashSet, destHashSet);
    }

    private void testCollection(Collection<Integer> srcCollection, Collection<Integer> destCollection) throws IOException
    {
        for (int i = 0; i < 100; i++)
            srcCollection.add(i);
        String data = JsonSerializer.serialize(srcCollection);
        StringReader reader = new StringReader(data);
        JsonDeserializer.deserialize(reader, new Class[] { srcCollection.getClass(), Integer.class }, destCollection);
        Assert.assertEquals(srcCollection.size(), destCollection.size());
        Assert.assertEquals(0, ValueUtils.compare(srcCollection, destCollection));
    }

    @Test
    public void testMap() throws IOException
    {
        Map<Integer, String> srcValueMap = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < 100; i++)
            srcValueMap.put(i, "value " + i);
        String data = JsonSerializer.serialize(srcValueMap);
        StringReader reader = new StringReader(data);
        Map<Integer, String> dstValueMap = new LinkedHashMap<Integer, String>();
        JsonDeserializer.deserialize(reader, new Class[] { Map.class, Integer.class, String.class }, dstValueMap);
        Assert.assertEquals(srcValueMap.size(), dstValueMap.size());
        Assert.assertEquals(0, ValueUtils.compare(srcValueMap, dstValueMap));

    }

    @Test
    public void testParams() throws IOException
    {
        Map<String, Object> srcParamMap = new LinkedHashMap<String, Object>();
        srcParamMap.put("Param1", 100);
        srcParamMap.put("Param2", 99.9);
        srcParamMap.put("Param3", "String Value");
        srcParamMap.put("Param4", new Integer[] { 1, 2, 3, 4, 5 });
        srcParamMap.put("Param5", new String[] { "String Vlaue 1", "String Vlaue 2", "String Vlaue 3", "String Vlaue 4" });
        String data = JsonSerializer.serialize(srcParamMap);

        Map<String, Class<?>[]> classTypeMap = new LinkedHashMap<String, Class<?>[]>();
        classTypeMap.put("Param1", new Class<?>[] { Integer.class });
        classTypeMap.put("Param2", new Class<?>[] { double.class });
        classTypeMap.put("Param3", new Class<?>[] { String.class });
        classTypeMap.put("Param4", new Class<?>[] { Integer[].class });
        classTypeMap.put("Param5", new Class<?>[] { String[].class });

        StringReader reader = new StringReader(data);
        Map<String, Object> dstParamMap = JsonDeserializer.deserialize(reader, classTypeMap);
        Assert.assertEquals(srcParamMap.size(), dstParamMap.size());
        Assert.assertEquals(0, ValueUtils.compare(srcParamMap, dstParamMap));

    }
}
