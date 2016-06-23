package org.dangcat.commons.serialize.json;

public class JsonResult
{
    private String content = null;

    public JsonResult(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return this.content;
    }

    @Override
    public String toString()
    {
        return this.getContent();
    }
}
