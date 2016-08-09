package org.dangcat.commons.serialize.json;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.apache.log4j.Logger;
import org.dangcat.commons.reflect.BeanUtils;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.ValueUtils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * JSON格式反序列化对象。
 */
public class JsonDeserializer {
    protected static final Logger logger = Logger.getLogger(JsonDeserializer.class);
    public static JsonDeserialize[] deserializes = null;
    private static Collection<JsonDeserialize> deserializeCollection = new HashSet<JsonDeserialize>();

    public synchronized static void addDeserialize(JsonDeserialize deserialize) {
        if (deserializes != null && !deserializeCollection.contains(deserializes)) {
            deserializeCollection.add(deserialize);
            deserializes = deserializeCollection.toArray(new JsonDeserialize[0]);
        }
    }

    private static Object deserialize(JsonReader jsonReader, Class<?>[] classTypes, Object instance) throws Exception {
        Object value = null;
        if (classTypes[0].isArray())
            value = deserializeArray(jsonReader, classTypes[0].getComponentType());
        else if (Collection.class.isAssignableFrom(classTypes[0]))
            value = deserializeCollection(jsonReader, classTypes, instance);
        else if (Map.class.isAssignableFrom(classTypes[0]))
            value = deserializeMap(jsonReader, classTypes, instance);
        else if (ReflectUtils.isConstClassType(classTypes[0]) || classTypes[0].isEnum()) {
            JsonToken jsonToken = jsonReader.peek();
            if (jsonToken == JsonToken.STRING || jsonToken == JsonToken.NUMBER)
                value = ValueUtils.parseValue(classTypes[0], jsonReader.nextString());
            else if (jsonToken == JsonToken.BOOLEAN)
                value = jsonReader.nextBoolean();
            else
                jsonReader.skipValue();
        } else
            value = deserializeObject(jsonReader, classTypes[0], instance);
        return value;
    }

    /**
     * 从数据流反序列化对象。
     *
     * @param inputStream 来源数据流。
     * @param instance    目标数据对象。
     * @return 序列化后的对象实例。
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Reader reader, Class<?>[] classTypes, Object instance) {
        T result = null;
        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(reader);
            if (jsonReader.hasNext()) {
                long beginTime = System.currentTimeMillis();
                JsonToken jsonToken = jsonReader.peek();
                if (jsonToken == JsonToken.BEGIN_OBJECT || jsonToken == JsonToken.BEGIN_ARRAY)
                    result = (T) deserialize(jsonReader, classTypes, instance);

                long timeCost = System.currentTimeMillis() - beginTime;
                if (timeCost > 500l) {
                    String message = "deserialize the object " + instance.getClass() + " cost time " + timeCost + "(ms)";
                    if (logger.isDebugEnabled())
                        logger.debug(message);
                    else if (timeCost > 1000l)
                        logger.info(message);
                }
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled())
                logger.error("Deserialize the error: ", e);
            else
                logger.error(e);
        } finally {
            try {
                if (jsonReader != null)
                    jsonReader.close();
            } catch (IOException e) {
            }
        }
        return result;
    }

    /**
     * 从数据流反序列化对象。
     *
     * @param reader    来源数据流。
     * @param classType 目标数据类型。
     * @return 序列化后的对象实例。
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> deserialize(Reader reader, Class<T> classType) {
        List<T> instanceList = null;
        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(reader);
            if (jsonReader.hasNext()) {
                JsonToken jsonToken = jsonReader.peek();
                if (jsonToken == JsonToken.BEGIN_ARRAY) {
                    Object arrayInstance = deserializeArray(jsonReader, classType);
                    if (arrayInstance != null) {
                        if (instanceList == null)
                            instanceList = new ArrayList<T>();
                        for (int index = 0; index < Array.getLength(arrayInstance); index++)
                            instanceList.add((T) Array.get(arrayInstance, index));
                    }
                } else if (jsonToken == JsonToken.BEGIN_OBJECT) {
                    Object instance = ReflectUtils.newInstance(classType);
                    T result = (T) deserialize(jsonReader, new Class<?>[]{classType}, instance);
                    if (result != null) {
                        if (instanceList == null)
                            instanceList = new ArrayList<T>();
                        instanceList.add(result);
                    }
                }
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled())
                logger.error("Deserialize the " + classType + " error: ", e);
            else
                logger.error(e);
        } finally {
            try {
                if (jsonReader != null)
                    jsonReader.close();
            } catch (IOException e) {
            }
        }
        return instanceList;
    }

    /**
     * 从数据流反序列化对象。
     *
     * @param reader    来源数据流。
     * @param classType 目标数据类型。
     * @return 序列化后的对象实例。
     */
    public static Map<String, Object> deserialize(Reader reader, Map<String, Class<?>[]> classTypeMap) {
        Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
        if (reader == null)
            return valueMap;
        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(reader);
            if (jsonReader.hasNext()) {
                JsonToken jsonToken = jsonReader.peek();
                if (jsonToken == JsonToken.BEGIN_OBJECT) {
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String name = nextName(jsonReader);
                        Class<?>[] classTypes = classTypeMap.get(name);
                        if (classTypes == null) {
                            logger.warn("The property " + name + " class type is not found.");
                            jsonReader.skipValue();
                        } else {
                            Object instance = deserialize(jsonReader, classTypes, null);
                            valueMap.put(name, instance);
                        }
                    }
                    jsonReader.endObject();
                }
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled())
                logger.error("Deserialize the object error: ", e);
            else
                logger.error(e);
        } finally {
            try {
                if (jsonReader != null)
                    jsonReader.close();
            } catch (IOException e) {
            }
        }
        return valueMap;
    }

    private static Object deserializeArray(JsonReader jsonReader, Class<?> classType) throws Exception {
        List<Object> instanceList = null;
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            Object value = deserialize(jsonReader, new Class<?>[]{classType}, null);
            if (value != null) {
                if (instanceList == null)
                    instanceList = new ArrayList<Object>();
                instanceList.add(value);
            }
        }
        jsonReader.endArray();
        Object arrayInstance = null;
        if (instanceList != null) {
            arrayInstance = Array.newInstance(classType, instanceList.size());
            for (int index = 0; index < instanceList.size(); index++)
                Array.set(arrayInstance, index, instanceList.get(index));
        }
        return arrayInstance;
    }

    @SuppressWarnings({"unchecked"})
    private static Object deserializeCollection(JsonReader jsonReader, Class<?>[] classTypes, Object instance) throws Exception {
        if (instance == null) {
            if (classTypes[0].isInterface()) {
                if (Set.class.equals(classTypes[0]))
                    instance = new LinkedHashSet<Object>();
                else if (List.class.equals(classTypes[0]) || Collection.class.equals(classTypes[0]))
                    instance = new LinkedList<Object>();
            } else
                instance = ReflectUtils.newInstance(classTypes[0]);
        }

        if (instance == null)
            jsonReader.skipValue();
        else {
            Object arrayInstance = deserializeArray(jsonReader, classTypes[1]);
            if (arrayInstance != null) {
                Collection collection = (Collection) instance;
                for (int index = 0; index < Array.getLength(arrayInstance); index++)
                    collection.add(Array.get(arrayInstance, index));
            }
        }
        return instance;
    }

    public static Object deserializeCustom(JsonReader jsonReader, Class<?> classType, Object instance) throws Exception {
        if (deserializes != null) {
            for (JsonDeserialize jsonDeserialize : deserializes) {
                Object result = jsonDeserialize.deserialize(jsonReader, classType, instance);
                if (result != null)
                    return result;
            }
        }
        org.dangcat.commons.serialize.json.annotation.JsonDeserialize jsonDeserializeAnnotation = ReflectUtils.findAnnotation(classType,
                org.dangcat.commons.serialize.json.annotation.JsonDeserialize.class);
        if (jsonDeserializeAnnotation != null) {
            JsonDeserialize jsonDeserialize = (JsonDeserialize) ReflectUtils.newInstance(jsonDeserializeAnnotation.value());
            if (jsonDeserialize != null) {
                addDeserialize(jsonDeserialize);
                Object result = jsonDeserialize.deserialize(jsonReader, classType, instance);
                if (result != null)
                    return result;
            }
        }
        return null;
    }

    public static <T> List<T> deserializeList(String text, Class<T> classType) {
        List<T> instanceList = null;
        if (!ValueUtils.isEmpty(text))
            instanceList = deserialize(new StringReader(text), classType);
        return instanceList;
    }

    @SuppressWarnings("unchecked")
    private static Object deserializeMap(JsonReader jsonReader, Class<?>[] classTypes, Object instance) throws Exception {
        if (instance == null) {
            if (classTypes[0].isInterface())
                instance = new LinkedHashMap<Object, Object>();
            else
                instance = ReflectUtils.newInstance(classTypes[0]);
        }
        if (instance == null)
            jsonReader.skipValue();
        else {
            Map map = (Map) instance;
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = nextName(jsonReader);
                Object key = ValueUtils.parseValue(classTypes[1], name);
                Object value = deserialize(jsonReader, new Class<?>[]{classTypes[2]}, null);
                if (key != null)
                    map.put(key, value);
            }
            jsonReader.endObject();
        }
        return instance;
    }

    private static Object deserializeObject(JsonReader jsonReader, Class<?> classType, Object instance) throws Exception {
        Object customResult = deserializeCustom(jsonReader, classType, instance);
        if (customResult != null)
            return customResult;

        if (instance == null)
            instance = ReflectUtils.newInstance(classType);
        if (instance != null) {
            Map<String, PropertyDescriptor> propertyDescriptorMap = getPropertyDescriptorMap(classType);
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = nextName(jsonReader);
                PropertyDescriptor propertyDescriptor = propertyDescriptorMap.get(name);
                if (propertyDescriptor != null) {
                    Method readMethod = propertyDescriptor.getReadMethod();
                    if (readMethod != null) {
                        Object value = readMethod.invoke(instance);
                        Class<?>[] classTypes = getClassTypes(propertyDescriptor);
                        Object result = deserialize(jsonReader, classTypes, value);
                        Method writeMethod = propertyDescriptor.getWriteMethod();
                        if (writeMethod != null)
                            writeMethod.invoke(instance, result);
                    }
                } else
                    jsonReader.skipValue();
            }
            jsonReader.endObject();
        } else
            jsonReader.skipValue();
        return instance;
    }

    public static <T> T deserializeObject(String text, Class<T> classType) {
        List<T> instanceList = deserializeList(text, classType);
        if (instanceList == null || instanceList.isEmpty())
            return null;
        return instanceList.get(0);
    }

    private static Class<?>[] getClassTypes(PropertyDescriptor propertyDescriptor) {
        Class<?>[] classTypes = new Class<?>[]{propertyDescriptor.getPropertyType(), null, null};
        Method readMethod = propertyDescriptor.getReadMethod();
        if (readMethod != null) {
            Type returnType = propertyDescriptor.getReadMethod().getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) returnType;
                int index = 1;
                for (Type actualType : parameterizedType.getActualTypeArguments()) {
                    if (actualType instanceof Class<?>)
                        classTypes[index++] = (Class<?>) actualType;
                }
            }
        }
        return classTypes;
    }

    /**
     * 读取类型的属性描述表。
     *
     * @param classType 目标类型。
     * @return 属性描述表。
     */
    private static Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> classType) {
        Map<String, PropertyDescriptor> methodMap = null;
        if (!ReflectUtils.isConstClassType(classType)) {
            methodMap = new TreeMap<String, PropertyDescriptor>(String.CASE_INSENSITIVE_ORDER);
            for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptorList(classType))
                methodMap.put(propertyDescriptor.getName(), propertyDescriptor);
        }
        return methodMap;
    }

    public static String nextName(JsonReader jsonReader) throws IOException {
        return jsonReader.nextName();
    }
}
