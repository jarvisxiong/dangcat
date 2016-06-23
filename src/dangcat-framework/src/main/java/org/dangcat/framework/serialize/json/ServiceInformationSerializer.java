package org.dangcat.framework.serialize.json;

import java.io.IOException;

import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.serialize.json.JsonSerialize;
import org.dangcat.commons.serialize.json.JsonSerializer;
import org.dangcat.framework.exception.ServiceInformation;

import com.google.gson.stream.JsonWriter;

public class ServiceInformationSerializer implements JsonSerialize
{
    private static final String FIELDNAME = "fieldName";
    private static final String ID = "id";
    private static final String INFO = "info";

    public static void serialize(JsonWriter jsonWriter, ServiceInformation serviceInformation) throws IOException
    {
        jsonWriter.beginObject();
        if (serviceInformation.getMessageId() != null)
            jsonWriter.name(ID).value(serviceInformation.getMessageId().toString());
        Object fieldName = ReflectUtils.getProperty(serviceInformation, FIELDNAME);
        if (fieldName != null && fieldName instanceof String)
        {
            String value = (String) fieldName;
            jsonWriter.name(FIELDNAME).value(ReflectUtils.toPropertyName(value));
        }
        jsonWriter.name(INFO).value(serviceInformation.getMessage());
        jsonWriter.endObject();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        return obj instanceof ServiceInformationSerializer;
    }

    @Override
    public String getSerializeName(Class<?> classType)
    {
        if (ServiceInformation.class.isAssignableFrom(classType))
            return INFO;
        return null;
    }

    @Override
    public int hashCode()
    {
        return ServiceInformation.class.hashCode();
    }

    public boolean serialize(JsonWriter jsonWriter, String name, Object instance) throws IOException
    {
        if (instance instanceof ServiceInformation)
        {
            JsonWriter writer = JsonSerializer.getJsonWriter(jsonWriter, name);
            serialize(writer, (ServiceInformation) instance);
            return true;
        }
        return false;
    }

    @Override
    public boolean serializeClassType(JsonWriter jsonWriter, Class<?> classType) throws IOException
    {
        if (ServiceInformation.class.isAssignableFrom(classType))
        {
            JsonWriter classTypeWriter = jsonWriter.name(INFO);
            classTypeWriter.beginObject();
            classTypeWriter.name(ID).value(Integer.class.getSimpleName());
            classTypeWriter.name(FIELDNAME).value(String.class.getSimpleName());
            classTypeWriter.name(INFO).value(String.class.getSimpleName());
            classTypeWriter.endObject();
            return true;
        }
        return false;
    }
}
