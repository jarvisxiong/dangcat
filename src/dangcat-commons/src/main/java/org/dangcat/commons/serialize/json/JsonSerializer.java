package org.dangcat.commons.serialize.json;

import com.google.gson.stream.JsonWriter;
import org.apache.log4j.Logger;
import org.dangcat.commons.reflect.BeanUtils;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.commons.utils.ValueUtils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * JSON格式序列化。
 */
public class JsonSerializer {
    protected static final Logger logger = Logger.getLogger(JsonSerializer.class);
    public static JsonSerialize[] serializes = null;
    private static Collection<JsonSerialize> serializeCollection = new HashSet<JsonSerialize>();

    static {
        // addSerialize(new ExceptionSerializer());
        addSerialize(new MethodInfoSerializer());
    }

    public synchronized static void addSerialize(JsonSerialize serialize) {
        if (serialize != null && !serializeCollection.contains(serialize)) {
            serializeCollection.add(serialize);
            serializes = serializeCollection.toArray(new JsonSerialize[0]);
        }
    }

    public static JsonWriter getJsonWriter(JsonWriter jsonWriter, String name) throws IOException {
        return name == null ? jsonWriter : jsonWriter.name(name);
    }

    @SuppressWarnings("unchecked")
    public static void serialize(JsonWriter jsonWriter, String name, Object instance) throws IOException {
        if (instance == null)
            getJsonWriter(jsonWriter, name).nullValue();
        else if (instance.getClass().isArray())
            serializeArray(jsonWriter, name, instance);
        else if (instance instanceof Collection) {
            Collection collection = (Collection) instance;
            serializeArray(jsonWriter, name, collection.toArray());
        } else if (instance instanceof Map) {
            Map map = (Map) instance;
            serializeMap(jsonWriter, name, map);
        } else if (ValueUtils.isNumber(instance.getClass()))
            getJsonWriter(jsonWriter, name).value((Number) instance);
        else if (ValueUtils.isBoolean(instance.getClass()))
            getJsonWriter(jsonWriter, name).value((Boolean) instance);
        else if (ReflectUtils.isConstClassType(instance.getClass()))
            getJsonWriter(jsonWriter, name).value(ValueUtils.toString(instance));
        else if (instance.getClass().isEnum())
            getJsonWriter(jsonWriter, name).value(ValueUtils.toString(instance));
        else
            serializeObject(jsonWriter, name, instance);
    }

    public static String serialize(Object instance) {
        StringWriter writer = new StringWriter();
        serialize(instance, writer, null);
        return writer.toString();
    }

    /**
     * 序列化对象以JSON格式输出。
     *
     * @param instance 对象实例。
     * @param writer   输出流。
     */
    public static void serialize(Object instance, Writer writer) {
        serialize(instance, writer, null);
    }

    public static void serialize(Object instance, Writer writer, String indent) {
        JsonWriter jsonWriter = null;
        try {
            long beginTime = System.currentTimeMillis();
            jsonWriter = new JsonWriter(writer);
            if (indent != null)
                jsonWriter.setIndent(indent);
            if (ReflectUtils.isConstClassType(instance.getClass())) {
                jsonWriter.beginObject();
                serialize(jsonWriter, "value", instance);
                jsonWriter.endObject();
            } else
                serialize(jsonWriter, null, instance);

            long timeCost = System.currentTimeMillis() - beginTime;
            if (timeCost > 500l) {
                String message = "serialize the object " + instance.getClass() + " cost time " + timeCost + "(ms)";
                if (logger.isDebugEnabled())
                    logger.debug(message);
                else if (timeCost > 1000l)
                    logger.info(message);
            }
        } catch (Exception e) {
            logger.error("serialize the object " + instance.getClass() + " is error: ", e);
        } finally {
            try {
                if (jsonWriter != null)
                    jsonWriter.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 序列化数组。
     *
     * @param jsonWriter JSON格式写入器。
     * @param instance   数组实例。
     * @throws IOException 运行异常。
     */
    public static void serializeArray(JsonWriter jsonWriter, String name, Object instance) throws IOException {
        if (instance != null && instance.getClass().isArray()) {
            JsonWriter writer = getJsonWriter(jsonWriter, name);
            writer.beginArray();
            for (int i = 0; i < Array.getLength(instance); i++)
                serialize(jsonWriter, null, Array.get(instance, i));
            writer.endArray();
        }
    }

    public static boolean serializeCustom(JsonWriter jsonWriter, String name, Object instance) throws IOException {
        if (instance != null) {
            if (serializes != null) {
                for (JsonSerialize jsonSerialize : serializes) {
                    if (jsonSerialize.serialize(jsonWriter, name, instance))
                        return true;
                }
            }
            org.dangcat.commons.serialize.json.annotation.JsonSerialize jsonSerializeAnnotation = ReflectUtils.findAnnotation(instance.getClass(),
                    org.dangcat.commons.serialize.json.annotation.JsonSerialize.class);
            if (jsonSerializeAnnotation != null) {
                JsonSerialize jsonSerialize = (JsonSerialize) ReflectUtils.newInstance(jsonSerializeAnnotation.value());
                if (jsonSerialize != null) {
                    addSerialize(jsonSerialize);
                    if (jsonSerialize.serialize(jsonWriter, name, instance))
                        return true;
                }
            }
        }
        return false;
    }

    public static void serializeMap(JsonWriter jsonWriter, String name, Map<?, ?> instanceMap) throws IOException {
        if (instanceMap != null && instanceMap.size() > 0) {
            JsonWriter writer = getJsonWriter(jsonWriter, name);
            writer.beginObject();
            for (Entry<?, ?> entry : instanceMap.entrySet()) {
                JsonWriter entryJsonWriter = getJsonWriter(jsonWriter, entry.getKey().toString());
                serialize(entryJsonWriter, null, entry.getValue());
            }
            writer.endObject();
        }
    }

    public static void serializeObject(JsonWriter jsonWriter, String name, Object instance) throws IOException {
        if (instance == null || Object.class.equals(instance.getClass()))
            return;

        if (serializeCustom(jsonWriter, name, instance))
            return;

        List<PropertyDescriptor> propertyDescriptorList = BeanUtils.getPropertyDescriptorList(instance.getClass());
        if (propertyDescriptorList.size() > 0) {
            JsonWriter writer = getJsonWriter(jsonWriter, name);
            writer.beginObject();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptorList) {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null) {
                    try {
                        String propertyName = propertyDescriptor.getName();
                        Object propertyValue = readMethod.invoke(instance);

                        Serialize serializeAnnotation = readMethod.getAnnotation(Serialize.class);
                        if (serializeAnnotation != null) {
                            if (serializeAnnotation.ignore())
                                continue;

                            if (!ValueUtils.isEmpty(serializeAnnotation.name()))
                                propertyName = serializeAnnotation.name();

                            if (!ValueUtils.isEmpty(serializeAnnotation.ignoreValue())) {
                                Object value = ValueUtils.parseValue(readMethod.getReturnType(), serializeAnnotation.ignoreValue());
                                if (value != null && value.equals(propertyValue))
                                    continue;
                            }
                        }

                        if (propertyValue != null)
                            serialize(writer, propertyName, propertyValue);
                    } catch (Exception e) {
                        String message = "The object " + instance + " invoke " + readMethod.getName() + " is error: ";
                        if (logger.isDebugEnabled())
                            logger.error(message, e);
                        else
                            logger.error(message);
                    }
                }
            }
            writer.endObject();
        }
    }
}
