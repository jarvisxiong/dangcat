package org.dangcat.boot.menus;

import java.util.Collection;
import java.util.LinkedList;

public class Menus
{
    private String baseUrl = null;
    private Collection<Menu> data = new LinkedList<Menu>();
    private String dir = null;

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public Collection<Menu> getData()
    {
        return data;
    }

    public String getDir()
    {
        return dir;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public void setDir(String dir)
    {
        this.dir = dir;
    }
}
