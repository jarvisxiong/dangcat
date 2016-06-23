package org.dangcat.boot.menus;

import java.util.Map;

public class MenuItem extends MenuBase
{
    private String jndiName = null;
    private Map<String, Object> params = null;
    private String url = null;

    public String getJndiName()
    {
        return jndiName;
    }

    public void setJndiName(String jndiName)
    {
        this.jndiName = jndiName;
    }

    public Map<String, Object> getParams()
    {
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
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
