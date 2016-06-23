package org.dangcat.persistence.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;

@Table
public class UserServerBind extends UserService
{
    public static final String BindItemId = "BindItemId";
    private static final long serialVersionUID = 1L;
    public static final String Value = "Value";

    @Column(isPrimaryKey = true)
    private Integer bindItemId;

    @Column(displaySize = 128)
    private String value;

    public Integer getBindItemId()
    {
        return bindItemId;
    }

    @Column(isPrimaryKey = true, displaySize = 40)
    public String getBindTime()
    {
        return super.getBindTime();
    }

    public String getValue()
    {
        return value;
    }

    public void setBindItemId(Integer bindItemId)
    {
        this.bindItemId = bindItemId;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}