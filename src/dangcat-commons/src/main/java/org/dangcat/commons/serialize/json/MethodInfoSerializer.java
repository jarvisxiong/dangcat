package org.dangcat.commons.serialize.json;

import com.google.gson.stream.JsonWriter;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.reflect.ParamInfo;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MethodInfoSerializer implements JsonSerialize {
    private static final String PARAMS = "params";
    private static final String TYPES = "types";

    public static void serialize(Collection<MethodInfo> methodInfoCollection, Writer writer) throws IOException {
        JsonWriter jsonWriter = null;
        try {
            jsonWriter = new JsonWriter(writer);
            jsonWriter.beginObject();
            for (MethodInfo methodInfo : methodInfoCollection)
                JsonSerializer.serialize(jsonWriter, null, methodInfo);
            jsonWriter.endObject();
            writer.flush();
        } finally {
            try {
                if (jsonWriter != null)
                    jsonWriter.close();
            } catch (IOException e) {
            }
        }
    }

    public static void serialize(JsonWriter jsonWriter, MethodInfo methodInfo) throws IOException {
        JsonWriter methodWriter = jsonWriter.name(methodInfo.getName());
        methodWriter.beginObject();
        // 所应用到的类型
        List<Class<?>> usedClassTypeCollection = new LinkedList<Class<?>>();
        // 参数信息。
        ParamInfo[] paramInfos = methodInfo.getParamInfos();
        if (paramInfos != null && paramInfos.length > 0) {
            JsonWriter paramsWriter = jsonWriter.name(PARAMS);
            paramsWriter.beginObject();
            for (ParamInfo paramInfo : paramInfos)
                ParamInfoSerializer.serializeParamInfo(paramsWriter, paramInfo, usedClassTypeCollection, false);
            paramsWriter.endObject();
        }
        // 返回值。
        List<Class<?>> returnClassTypeCollection = new LinkedList<Class<?>>();
        ParamInfo returnInfo = methodInfo.getReturnInfo();
        if (returnInfo != null && !void.class.equals(returnInfo.getClassType()))
            ParamInfoSerializer.serializeParamInfo(jsonWriter, returnInfo, returnClassTypeCollection, true);
        usedClassTypeCollection.addAll(returnClassTypeCollection);
        // 所用到的公用类型。
        if (usedClassTypeCollection.size() > 0) {
            JsonWriter typesWriter = jsonWriter.name(TYPES);
            typesWriter.beginObject();
            for (int i = 0; i < usedClassTypeCollection.size(); i++) {
                Class<?> classType = usedClassTypeCollection.get(i);
                boolean isReturnType = returnClassTypeCollection.contains(classType);
                ParamInfoSerializer.serializeClassType(typesWriter, classType, methodInfo.getGenericClassMap(), usedClassTypeCollection, isReturnType);
            }
            typesWriter.endObject();
        }
        methodWriter.endObject();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        return obj instanceof MethodInfoSerializer;
    }

    @Override
    public String getSerializeName(Class<?> classType) {
        return null;
    }

    @Override
    public int hashCode() {
        return MethodInfo.class.hashCode();
    }

    public boolean serialize(JsonWriter jsonWriter, String name, Object instance) throws IOException {
        if (instance instanceof MethodInfo) {
            JsonWriter writer = JsonSerializer.getJsonWriter(jsonWriter, name);
            serialize(writer, (MethodInfo) instance);
            return true;
        }
        return false;
    }

    @Override
    public boolean serializeClassType(JsonWriter jsonWriter, Class<?> classType) throws IOException {
        return false;
    }
}
