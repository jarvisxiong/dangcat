package org.dangcat.persistence.index;

import org.dangcat.commons.utils.ValueUtils;

import java.util.Comparator;

public class ValueComparator implements Comparator<Object> {
    @Override
    public int compare(Object from, Object to) {
        return ValueUtils.compare(from, to);
    }
}
