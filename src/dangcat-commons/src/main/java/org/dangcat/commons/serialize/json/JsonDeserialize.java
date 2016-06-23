package org.dangcat.commons.serialize.json;

import com.google.gson.stream.JsonReader;

/**
 * 反序列化接口。
 */
public interface JsonDeserialize
{
    public Object deserialize(JsonReader jsonReader, Class<?> classType, Object instance) throws Exception;
}
