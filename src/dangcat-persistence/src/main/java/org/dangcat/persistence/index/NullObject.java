package org.dangcat.persistence.index;

public class NullObject implements Comparable<Object>
{
    @Override
    public int compareTo(Object o)
    {
        return o == null ? 0 : -1;
    }
}
