package org.dangcat.net.rfc.template;

import java.io.InputStream;

class AttributeResource
{
    private InputStream inputStream;
    private String name;

    AttributeResource(String name, InputStream inputStream)
    {
        this.name = name;
        this.inputStream = inputStream;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public String getName()
    {
        return name;
    }
}