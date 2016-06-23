package org.dangcat.boot.security.impl;

import org.dangcat.commons.utils.ValueUtils;

public class SignInfo
{
    private String key;
    private String no;

    public String getKey()
    {
        return this.key;
    }

    public String getNo()
    {
        return this.no;
    }

    public boolean isValid()
    {
        return !ValueUtils.isEmpty(this.getNo()) && !ValueUtils.isEmpty(this.getKey());
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public void setNo(String no)
    {
        this.no = no;
    }
}
