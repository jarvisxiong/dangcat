package org.dangcat.persistence.filter;

import java.util.HashMap;
import java.util.Map;

class FilterFactory
{
    private static FilterFactory instance = new FilterFactory();

    static FilterFactory getInstance()
    {
        return instance;
    }

    private Map<FilterType, Filter[]> filterMap = new HashMap<FilterType, Filter[]>();

    private FilterFactory()
    {
        this.filterMap.put(FilterType.between, new Filter[] { new BetweenFilter(), new NotBetweenFilter() });
        this.filterMap.put(FilterType.eq, new Filter[] { new EqualFilter(), new NotBetweenFilter() });
        this.filterMap.put(FilterType.gt, new Filter[] { new GreaterFilter(), new LessEqualFilter() });
        this.filterMap.put(FilterType.ge, new Filter[] { new GreaterEqualFilter(), new LessFilter() });
        this.filterMap.put(FilterType.lt, new Filter[] { new LessFilter(), new GreaterEqualFilter() });
        this.filterMap.put(FilterType.le, new Filter[] { new LessEqualFilter(), new GreaterFilter() });
        this.filterMap.put(FilterType.like, new Filter[] { new LikeFilter(), new NotLikeFilter() });
        this.filterMap.put(FilterType.sql, new Filter[] { new SqlFilter(), new SqlFilter() });
    }

    Filter getFilter(FilterUnit filterUnit)
    {
        Filter filter = null;
        Filter[] filterArray = this.filterMap.get(filterUnit.getFilterType());
        if (filterArray != null)
            filter = filterArray[filterUnit.isNot() ? 1 : 0];
        return filter;
    }
}
