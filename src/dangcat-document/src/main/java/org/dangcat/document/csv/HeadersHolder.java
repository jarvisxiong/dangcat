package org.dangcat.document.csv;

import java.util.HashMap;
import java.util.Map;

class HeadersHolder
{
    protected String[] Headers = null;
    protected Map<String, Integer> IndexByName = new HashMap<String, Integer>();
    protected int Length = 0;
}
