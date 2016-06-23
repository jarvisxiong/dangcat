package org.dangcat.boot.menus;

public abstract class MenuBase implements MenuData
{
    private Boolean checked = null;
    private String icon = null;
    private String keyTitle = null;
    private String name = null;
    private String title = null;

    public Boolean getChecked()
    {
        return checked;
    }

    public void setChecked(Boolean checked)
    {
        this.checked = checked;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getKeyTitle()
    {
        return keyTitle;
    }

    public void setKeyTitle(String keyTitle)
    {
        this.keyTitle = keyTitle;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}
