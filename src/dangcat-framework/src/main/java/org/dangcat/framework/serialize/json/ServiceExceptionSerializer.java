package org.dangcat.framework.serialize.json;

import com.google.gson.stream.JsonWriter;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.serialize.json.JsonSerialize;
import org.dangcat.commons.serialize.json.JsonSerializer;

import java.io.IOException;

public class ServiceExceptionSerializer implements JsonSerialize
{
    private static final String ERROR = "error";
    private static final String FIELDNAME = "fieldName";
    private static final String ID = "id";
    private static final String MESSAGE_ID = "messageId";

    public static void serialize(JsonWriter jsonWriter, Exception exception) throws IOException
    {
        jsonWriter.beginObject();
        Object messageId = ReflectUtils.getProperty(exception, MESSAGE_ID);
        if (messageId != null)
            jsonWriter.name(ID).value(messageId.toString());
        Object fieldName = ReflectUtils.getProperty(exception, FIELDNAME);
        if (fieldName != null && fieldName instanceof String)
        {
            String value = (String) fieldName;
            jsonWriter.name(FIELDNAME).value(ReflectUtils.toPropertyName(value));
        }
        jsonWriter.name(ERROR).value(exception.getMessage());
        jsonWriter.endObject();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        return obj instanceof ServiceExceptionSerializer;
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
            JsonWriter writer = JsonSerializer.getJsonWriter(jsonWriter, name);
            serialize(writer, (Exception) instance);
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
            classTypeWriter.name(ID).value(Integer.class.getSimpleName());
            classTypeWriter.name(FIELDNAME).value(String.class.getSimpleName());
            classTypeWriter.name(ERROR).value(String.class.getSimpleName());
            classTypeWriter.endObject();
            return true;
        }
        return false;
    }
}
