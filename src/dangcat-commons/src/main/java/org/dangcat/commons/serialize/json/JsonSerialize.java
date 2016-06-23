package org.dangcat.commons.serialize.json;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public interface JsonSerialize {
    String getSerializeName(Class<?> classType);

    boolean serialize(JsonWriter jsonWriter, String name, Object instance) throws IOException;

    boolean serializeClassType(JsonWriter jsonWriter, Class<?> classType) throws IOException;
}
