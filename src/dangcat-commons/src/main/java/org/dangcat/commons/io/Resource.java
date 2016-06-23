package org.dangcat.commons.io;

import java.io.File;
import java.io.InputStream;

public class Resource
{
    private InputStream inputStream;
    private String name;
    private String path;

    public Resource(String path, String name, InputStream inputStream)
    {
        this.path = path;
        this.name = name;
        this.inputStream = inputStream;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Resource)
        {
            Resource resource = (Resource) obj;
            return this.getName().equalsIgnoreCase(resource.getName());
        }
        return super.equals(obj);
    }

    public InputStream getInputStream()
    {
        return this.inputStream;
    }

    public String getName()
    {
        return this.name;
    }

    public String getPath()
    {
        return this.path;
    }

    @Override
    public int hashCode()
    {
        return this.getName().hashCode();
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        if (this.getPath() != null)
        {
            info.append(this.getPath());
            info.append(File.separator);
        }
        info.append(this.getName());
        return info.toString();
    }
}
