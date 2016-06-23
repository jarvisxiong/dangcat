package org.dangcat.commons.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.dangcat.commons.reflect.BeanUtils;
import org.dangcat.commons.reflect.ReflectUtils;

/**
 * 数值比较工具。
 */
class ValueCompare
{
    /**
     * 比较两个对象的大小。
     * @param from 来源对象。
     * @param to 目标对象。
     * @return
     */
    @SuppressWarnings("unchecked")
    protected static int compare(Object from, Object to)
    {
        if (from == null && to == null)
            return 0;
        else if (from == null)
            return -1;
        else if (to == null)
            return 1;
        else if (from == to)
            return 0;

        // 类型相同比较。
        if (from instanceof Collection && to instanceof Collection)
            return compareCollection((Collection) from, (Collection) to);
        else if (from instanceof Map && to instanceof Map)
            return compareMap((Map) from, (Map) to);
        else if (from.getClass().isArray() && to.getClass().isArray())
            return compareArray(from, to);
        else if (from instanceof Number && to instanceof Number)
            return compareNumber((Number) from, (Number) to);
        else if (to.getClass().isAssignableFrom(from.getClass()) || from.getClass().isAssignableFrom(to.getClass()))
        {
            if (from instanceof Timestamp)
                return compareTimestamp((Date) from, (Date) to);
            else if (from instanceof Date)
                return compareDate((Date) from, (Date) to);
            else if (from instanceof Double)
                return compareDouble((Double) from, (Double) to);
            else if (from instanceof char[])
                return compareCharArray((char[]) from, (char[]) to);
            else if (from instanceof byte[])
                return compareByteArray((byte[]) from, (byte[]) to);
            else if (from instanceof Comparable)
                return compareComparable((Comparable) from, to);
            else if (!ReflectUtils.isConstClassType(from.getClass()))
                return compareBean(from, to);
            else
                return from.toString().compareTo(to.toString());
        }
        // 当类型不相同的时候，而且也没有进行类型转换时，不返回0，而是直接返回一个1，表示两个比较值是不相等的，可以示为前者更大。
        return 1;
    }

    private static int compareArray(Object from, Object to)
    {
        int fromLength = Array.getLength(from);
        int toLength = Array.getLength(to);
        for (int i = 0; i < fromLength && i < toLength; i++)
        {
            Object fromValue = Array.get(from, i);
            Object toValue = Array.get(to, i);
            int result = compare(fromValue, toValue);
            if (result != 0)
                return result;
        }
        return toLength - fromLength;
    }

    private static int compareBean(Object from, Object to)
    {
        for (PropertyDescriptor fromPropertyDescriptor : BeanUtils.getPropertyDescriptorList(from.getClass()))
        {
            Object fromValue = null;
            try
            {
                Method method = fromPropertyDescriptor.getReadMethod();
                if (method != null)
                    fromValue = method.invoke(from);
            }
            catch (Exception e)
            {
            }
            Object toValue = ReflectUtils.getProperty(to, fromPropertyDescriptor.getName());
            int result = compare(fromValue, toValue);
            if (result != 0)
                return result;
        }
        return 0;
    }

    private static int compareByteArray(byte[] from, byte[] to)
    {
        if (to == null)
            return 1;

        for (int i = 0; i < from.length && i < to.length; i++)
        {
            if (from[i] != to[i])
                return to[i] - from[i];
        }
        return to.length - from.length;
    }

    private static int compareCharArray(char[] from, char[] to)
    {
        if (to == null)
            return 1;

        for (int i = 0; i < from.length && i < to.length; i++)
        {
            if (from[i] != to[i])
                return to[i] - from[i];
        }
        return to.length - from.length;
    }

    @SuppressWarnings("unchecked")
    private static int compareCollection(Collection from, Collection to)
    {
        Object[] fromArray = from.toArray();
        Object[] toArray = to.toArray();
        return compareArray(fromArray, toArray);
    }

    @SuppressWarnings("unchecked")
    private static int compareComparable(Comparable from, Object to)
    {
        return from.compareTo(to);
    }

    private static int compareDate(Date from, Date to)
    {
        return from == null ? -1 : DateUtils.diff(DateUtils.SECOND, to, from);
    }

    private static int compareDouble(Double from, Double to)
    {
        return to == null ? 1 : (int) ((from - to) * 10000);
    }

    private static int compareMap(Map<?, ?> from, Map<?, ?> to)
    {
        for (Entry<?, ?> entry : from.entrySet())
        {
            Object fromValue = entry.getValue();
            Object toValue = to.get(entry.getKey());
            int result = compare(fromValue, toValue);
            if (result != 0)
                return result;
        }
        return to.size() - from.size();
    }

    private static int compareNumber(Number from, Number to)
    {
        return (int) ((from.doubleValue() - to.doubleValue()) * 10000);
    }

    private static int compareTimestamp(Date from, Date to)
    {
        return from == null ? -1 : from.compareTo(to);
    }
}
