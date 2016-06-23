package org.dangcat.commons.serialize.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dangcat.commons.utils.ValueUtils;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public abstract class CustomJsonDeserializer implements JsonDeserialize
{
    protected abstract Object createInstance(String name);

    public Object deserialize(JsonReader jsonReader, Class<?> classType, Object instance) throws Exception
    {
        return this.deserializeObject(jsonReader, null, instance);
    }

    private Object deserialize(JsonReader jsonReader, String name) throws IOException
    {
        Object value = null;
        JsonToken jsonToken = jsonReader.peek();
        if (jsonToken == JsonToken.STRING)
            value = jsonReader.nextString();
        else if (jsonToken == JsonToken.NUMBER)
        {
            String text = jsonReader.nextString();
            if (!ValueUtils.isEmpty(text))
            {
                if (text.indexOf(".") == -1)
                    value = ValueUtils.parseInt(text);
                else
                    value = ValueUtils.parseDouble(text);
            }
        }
        else if (jsonToken == JsonToken.BOOLEAN)
            value = jsonReader.nextBoolean();
        else if (jsonToken == JsonToken.BEGIN_ARRAY)
            value = this.deserializeArray(jsonReader, name);
        else if (jsonToken == JsonToken.BEGIN_OBJECT)
            value = this.deserializeObject(jsonReader, name, null);
        else
            jsonReader.skipValue();
        return value;

    }

    private Object deserializeArray(JsonReader jsonReader, String name) throws IOException
    {
        List<Object> instanceList = null;
        jsonReader.beginArray();
        while (jsonReader.hasNext())
        {
            Object value = this.deserialize(jsonReader, name);
            if (value != null)
            {
                if (instanceList == null)
                    instanceList = new ArrayList<Object>();
                instanceList.add(value);
            }
        }
        jsonReader.endArray();
        return instanceList;
    }

    private Object deserializeObject(JsonReader jsonReader, String parentName, Object instance) throws IOException
    {
        JsonToken jsonToken = jsonReader.peek();
        if (jsonToken == JsonToken.BEGIN_OBJECT)
        {
            if (instance == null)
                instance = this.createInstance(parentName);
            jsonReader.beginObject();
            while (jsonReader.hasNext())
            {
                String name = JsonDeserializer.nextName(jsonReader);
                Object value = this.deserialize(jsonReader, name);
                this.setValue(instance, parentName, name, value);
            }
            jsonReader.endObject();
        }
        return instance;
    }

    protected abstract void setValue(Object instance, String parentName, String name, Object value);
}
