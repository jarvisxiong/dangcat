package org.dangcat.net.rfc.template.xml;

/**
 * 参数对象
 * @author dangcat
 * 
 */
public class Option
{
    /** 选项值。 */
    private Integer key;
    /** 选项名。 */
    private String value;

    public Integer getKey()
    {
        return key;
    }

    public void setKey(Integer key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
