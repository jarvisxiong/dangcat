package org.dangcat.commons.serialize.json;

import java.io.IOException;

import com.google.gson.stream.JsonWriter;

public interface JsonSerialize
{
    public String getSerializeName(Class<?> classType);

    public boolean serialize(JsonWriter jsonWriter, String name, Object instance) throws IOException;

    public boolean serializeClassType(JsonWriter jsonWriter, Class<?> classType) throws IOException;
}
