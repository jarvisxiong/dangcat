package org.dangcat.commons.serialize.json;

import com.google.gson.stream.JsonWriter;
import org.dangcat.commons.reflect.BeanUtils;
import org.dangcat.commons.reflect.GenericUtils;
import org.dangcat.commons.reflect.ParamInfo;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.commons.utils.ValueUtils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class ParamInfoSerializer
{
    private static final String ARRAY = "Array";
    private static final String MAP = "Map";
    private static final String PACKAGE_JAVA = "java";

    private static void addClassType(Collection<Class<?>> usedClassTypeCollection, Class<?> classType)
    {
        if (usedClassTypeCollection != null && !isConstClassType(classType) && !usedClassTypeCollection.contains(classType))
            usedClassTypeCollection.add(classType);
    }

    private static String getSerializeName(Class<?> classType)
    {
        if (JsonSerializer.serializes != null)
        {
            for (JsonSerialize jsonSerialize : JsonSerializer.serializes)
            {
                if (jsonSerialize.getSerializeName(classType) != null)
                    return jsonSerialize.getSerializeName(classType);
            }
        }
        return classType.getSimpleName();
    }

    private static boolean isArrayClassType(Class<?> classType)
    {
        return classType.isArray() || Collection.class.isAssignableFrom(classType);
    }

    private static boolean isConstClassType(Class<?> classType)
    {
        return ReflectUtils.isConstClassType(classType) || classType.isEnum() || Object.class.equals(classType);
    }

    private static void serializeArray(JsonWriter jsonWriter, String type, Collection<Class<?>> usedClassTypeCollection, Class<?>[] parameterizedClasses) throws IOException
    {
        String value = type;
        if (parameterizedClasses != null && parameterizedClasses.length > 0)
        {
            StringBuilder valueBuider = new StringBuilder();
            for (Class<?> parameterizedClass : parameterizedClasses)
            {
                if (valueBuider.length() > 0)
                    valueBuider.append(", ");
                valueBuider.append(getSerializeName(parameterizedClass));
                addClassType(usedClassTypeCollection, parameterizedClass);
            }
            value += "[" + valueBuider.toString() + "]";
        }
        jsonWriter.value(value);
    }

    protected static void serializeClassType(JsonWriter jsonWriter, Class<?> classType, Map<String, Class<?>> genericClassMap, Collection<Class<?>> usedClassTypeCollection, boolean isReturnType)
            throws IOException
    {
        if (isConstClassType(classType))
            jsonWriter.name(getSerializeName(classType)).value(classType.getSimpleName());
        else if (isArrayClassType(classType))
            jsonWriter.name(getSerializeName(classType)).value(ARRAY);
        else if (Map.class.isAssignableFrom(classType))
            jsonWriter.name(getSerializeName(classType)).value(MAP);
        else if (classType.getPackage().getName().startsWith(PACKAGE_JAVA))
            jsonWriter.name(getSerializeName(classType)).value(classType.getName());
        else
            serializeObjectClassType(jsonWriter, classType, genericClassMap, usedClassTypeCollection, isReturnType);
    }

    private static void serializeObject(JsonWriter jsonWriter, ParamInfo paramInfo, Collection<Class<?>> usedClassTypeCollection, Map<String, Method> propertyMap, boolean isReturnType)
            throws IOException
    {
        if (JsonSerializer.serializes != null)
        {
            for (JsonSerialize jsonSerialize : JsonSerializer.serializes)
            {
                if (jsonSerialize.serializeClassType(jsonWriter, paramInfo.getClassType()))
                    return;
            }
        }

        Map<String, Class<?>> genericClassMap = paramInfo.getMethodInfo().getGenericClassMap();
        jsonWriter.beginObject();
        for (Entry<String, Method> entry : propertyMap.entrySet())
            serializeProperty(jsonWriter, entry.getKey(), entry.getValue(), genericClassMap, usedClassTypeCollection, isReturnType);
        jsonWriter.endObject();
    }

    private static void serializeObjectClassType(JsonWriter jsonWriter, Class<?> classType, Map<String, Class<?>> genericClassMap, Collection<Class<?>> usedClassTypeCollection, boolean isReturnType)
            throws IOException
    {
        if (JsonSerializer.serializes != null)
        {
            for (JsonSerialize jsonSerialize : JsonSerializer.serializes)
            {
                if (jsonSerialize.serializeClassType(jsonWriter, classType))
                    return;
            }
        }

        List<PropertyDescriptor> propertyDescriptorList = BeanUtils.getPropertyDescriptorList(classType);
        if (propertyDescriptorList != null && propertyDescriptorList.size() > 0)
        {
            JsonWriter classTypeWriter = jsonWriter.name(classType.getSimpleName());
            classTypeWriter.beginObject();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptorList)
            {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null)
                    serializeProperty(jsonWriter, propertyDescriptor.getName(), readMethod, genericClassMap, usedClassTypeCollection, isReturnType);
            }
            classTypeWriter.endObject();
        }
    }

    protected static void serializeParamInfo(JsonWriter jsonWriter, ParamInfo paramInfo, Collection<Class<?>> usedClassTypeCollection, boolean isReturnType) throws IOException
    {
        JsonWriter paramWriter = jsonWriter.name(paramInfo.getName());
        Class<?> classType = paramInfo.getClassType();
        Class<?>[] parameterizedClasses = paramInfo.getParameterizedClasses();

        if (isConstClassType(classType))
            paramWriter.value(classType.getSimpleName());
        else if (isArrayClassType(classType))
            serializeArray(paramWriter, ARRAY, usedClassTypeCollection, parameterizedClasses);
        else if (Map.class.isAssignableFrom(classType))
            serializeArray(paramWriter, MAP, usedClassTypeCollection, parameterizedClasses);
        else if (classType.getPackage().getName().startsWith(PACKAGE_JAVA))
            paramWriter.value(classType.getName());
        else
        {
            if (JsonSerializer.serializes != null)
            {
                for (JsonSerialize jsonSerialize : JsonSerializer.serializes)
                {
                    String className = jsonSerialize.getSerializeName(classType);
                    if (className != null)
                    {
                        paramWriter.value(className);
                        addClassType(usedClassTypeCollection, classType);
                        return;
                    }
                }
            }

            Map<String, Method> propertyMap = BeanUtils.getPropertyMethodMap(classType, false);
            if (propertyMap != null && propertyMap.size() > 0)
            {
                serializeObject(paramWriter, paramInfo, usedClassTypeCollection, propertyMap, isReturnType);
                return;
            }
            paramWriter.value(classType.getName());
        }
    }

    private static void serializeProperty(JsonWriter jsonWriter, String name, Method method, Map<String, Class<?>> genericClassMap, Collection<Class<?>> usedClassTypeCollection, boolean isReturnType)
            throws IOException
    {
        Serialize serializeAnnotation = method.getAnnotation(Serialize.class);
        if (serializeAnnotation != null)
        {
            if (serializeAnnotation.ignore())
                return;

            if (!ValueUtils.isEmpty(serializeAnnotation.name()))
                name = serializeAnnotation.name();
        }

        if (!isReturnType && (name.equalsIgnoreCase("errors") || name.equalsIgnoreCase("infos")))
            return;

        Type returnType = method.getGenericReturnType();
        Class<?> classType = GenericUtils.getGenericClassType(genericClassMap, returnType);
        Class<?>[] parameterizedClasses = GenericUtils.getMemberClassType(genericClassMap, returnType);

        JsonWriter methodWriter = jsonWriter.name(name);
        if (isConstClassType(classType))
            methodWriter.value(classType.getSimpleName());
        else if (isArrayClassType(classType))
            serializeArray(methodWriter, ARRAY, usedClassTypeCollection, parameterizedClasses);
        else if (Map.class.isAssignableFrom(classType))
            serializeArray(methodWriter, MAP, usedClassTypeCollection, parameterizedClasses);
        else if (classType.getPackage().getName().startsWith(PACKAGE_JAVA))
            methodWriter.value(classType.getName());
        else
        {
            methodWriter.value(getSerializeName(classType));
            addClassType(usedClassTypeCollection, classType);
        }
    }
}
