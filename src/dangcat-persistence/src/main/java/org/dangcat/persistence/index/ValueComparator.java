package org.dangcat.persistence.index;

import java.util.Comparator;

import org.dangcat.commons.utils.ValueUtils;

public class ValueComparator implements Comparator<Object>
{
    @Override
    public int compare(Object from, Object to)
    {
        return ValueUtils.compare(from, to);
    }
}
