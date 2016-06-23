package org.dangcat.commons.serialize.xml;

/**
 * 属性对象
 * @author dangcat
 * 
 */
public class Property implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;
    /** 属性名。 */
    private String name;
    /** 属性值。 */
    private String value;

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.name + " = " + this.value;
    }
}
