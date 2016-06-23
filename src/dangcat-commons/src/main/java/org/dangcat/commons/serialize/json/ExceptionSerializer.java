package org.dangcat.commons.serialize.json;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ExceptionSerializer implements JsonSerialize
{
    private static final String ERROR = "error";

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        return obj instanceof ExceptionSerializer;
    }

    @Override
    public String getSerializeName(Class<?> classType)
    {
        if (Exception.class.isAssignableFrom(classType))
            return ERROR;
        return null;
    }

    @Override
    public int hashCode()
    {
        return Exception.class.hashCode();
    }

    @Override
    public boolean serialize(JsonWriter jsonWriter, String name, Object instance) throws IOException
    {
        if (instance instanceof Exception)
        {
            Exception exception = (Exception) instance;
            JsonWriter writer = JsonSerializer.getJsonWriter(jsonWriter, name);
            writer.beginObject();
            writer.name(ERROR).value(exception.getMessage());
            writer.endObject();
            return true;
        }
        return false;
    }

    @Override
    public boolean serializeClassType(JsonWriter jsonWriter, Class<?> classType) throws IOException
    {
        if (Exception.class.isAssignableFrom(classType))
        {
            JsonWriter classTypeWriter = jsonWriter.name(ERROR);
            classTypeWriter.beginObject();
            classTypeWriter.name(ERROR).value(String.class.getSimpleName());
            classTypeWriter.endObject();
            return true;
        }
        return false;
    }
}
