package org.dangcat.persistence.index;

import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class FilterComparable implements Comparable<Object>, FilterExpress
{
    private static final long serialVersionUID = 1L;
    private List<Object> equalsList = new ArrayList<Object>();
    private List<FilterUnit> filterUnitList = new ArrayList<FilterUnit>();
    private RangeComparable rangeComparable = new RangeComparable();

    protected void add(FilterUnit filterUnit)
    {
        if (filterUnit != null && !this.filterUnitList.contains(filterUnit))
        {
            this.filterUnitList.add(filterUnit);
            this.rangeComparable.createRange(filterUnit);
            if (FilterType.eq.equals(filterUnit.getFilterType()) && filterUnit.getParams() != null)
            {
                for (Object value : filterUnit.getParams())
                {
                    if (value != null)
                        this.equalsList.add(value);
                }
            }
        }
    }

    protected void clear()
    {
        this.filterUnitList.clear();
        this.rangeComparable.clear();
        this.equalsList.clear();
    }

    @Override
    public int compareTo(Object o)
    {
        return this.rangeComparable.compareTo(o);
    }

    @SuppressWarnings("unchecked")
    protected Object[] find(BinaryTree binaryTree)
    {
        Set<Object> entrySet = null;
        Object[] keys = this.equalsList.toArray();
        if (keys != null && keys.length > 0)
        {
            entrySet = new HashSet<Object>();
            for (Object key : keys)
            {
                if (this.rangeComparable.compareTo(key) != 0)
                    continue;

                Object entry = binaryTree.getEntry(key);
                if (entry != null && !entrySet.contains(entry))
                    entrySet.add(entry);
            }
        }
        else
            entrySet = binaryTree.find(this);
        return entrySet.toArray();
    }

    @Override
    public boolean isValid(Object value)
    {
        for (FilterUnit filterUnit : this.filterUnitList)
        {
            if (!filterUnit.isValid(value))
                return false;
        }
        return true;
    }
}
