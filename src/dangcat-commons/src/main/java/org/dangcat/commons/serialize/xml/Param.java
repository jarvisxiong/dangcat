package org.dangcat.commons.serialize.xml;

/**
 * 参数对象
 * @author dangcat
 * 
 */
public class Param extends Value
{
    private static final long serialVersionUID = 1L;
    /**
     * 参数名。
     */
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
