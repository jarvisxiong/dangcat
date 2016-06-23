package org.dangcat.boot.menus;

import java.util.Collection;

public class Menu extends MenuDataCollection
{
    private String url = null;

    public Collection<MenuData> getData()
    {
        return this.getDataCollection();
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
