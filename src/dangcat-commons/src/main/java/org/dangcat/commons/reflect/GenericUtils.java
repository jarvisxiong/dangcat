package org.dangcat.commons.reflect;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 泛型工具。
 */
public class GenericUtils
{
    /**
     * 读取泛型类的成员类型。
     * @param classType 泛型类。
     * @return 成员类型映射表
     */
    public static Map<String, Class<?>> getClassGenericInfo(Class<?> classType)
    {
        Map<String, Class<?>> genericMap = new HashMap<String, Class<?>>();
        getClassGenericInfo(classType, genericMap);
        return genericMap;
    }

    private static void getClassGenericInfo(Class<?> classType, Map<String, Class<?>> genericMap)
    {
        if (Object.class.equals(classType) || classType == null)
            return;

        getClassGenericInfo(classType.getSuperclass(), genericMap);

        List<ParameterizedType> parameterizedTypeList = new ArrayList<ParameterizedType>();

        for (Type genericType : classType.getGenericInterfaces())
        {
            if (genericType instanceof ParameterizedType)
                parameterizedTypeList.add((ParameterizedType) genericType);
        }

        Type genericType = classType.getGenericSuperclass();
        if (genericType instanceof ParameterizedType)
            parameterizedTypeList.add((ParameterizedType) genericType);

        for (ParameterizedType parameterizedType : parameterizedTypeList)
        {
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            TypeVariable<?>[] typeVariables = rawType.getTypeParameters();
            String[] typeNames = new String[typeVariables.length];
            for (int i = 0; i < typeVariables.length; i++)
                typeNames[i] = typeVariables[i].getName();

            Type[] paramTypes = parameterizedType.getActualTypeArguments();
            Class<?>[] classTypes = new Class<?>[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++)
            {
                if (paramTypes[i] instanceof Class<?>)
                    classTypes[i] = (Class<?>) paramTypes[i];
            }

            for (int i = 0; i < typeVariables.length; i++)
            {
                if (classTypes[i] != null)
                    genericMap.put(typeNames[i], classTypes[i]);
            }
        }
    }

    public static Class<?> getClassType(Map<String, Class<?>> genericClassMap, Type type, ParamInfo paramInfo)
    {
        if (type instanceof GenericArrayType)
        {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Class<?> componentType = getClassType(genericClassMap, genericArrayType.getGenericComponentType(), null);
            if (paramInfo != null)
                paramInfo.addParameterizedClass(componentType);
        }
        else if (type instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            TypeVariable<?>[] typeVariables = null;
            Class<?> classType = getGenericClassType(genericClassMap, parameterizedType);
            if (classType != null)
                typeVariables = classType.getTypeParameters();
            int index = 0;
            for (Type parameterizedClass : parameterizedType.getActualTypeArguments())
            {
                Class<?> genericClass = getClassType(genericClassMap, parameterizedClass, null);
                if (genericClass != null)
                {
                    if (paramInfo != null)
                        paramInfo.addParameterizedClass(genericClass);
                    if (typeVariables != null && !(parameterizedClass instanceof TypeVariable<?>))
                        genericClassMap.put(typeVariables[index++].getName(), genericClass);
                }
            }
        }
        return getGenericClassType(genericClassMap, type);
    }

    public static Class<?> getGenericClassType(Map<String, Class<?>> genericClassMap, Type type)
    {
        Class<?> classType = null;
        if (type instanceof GenericArrayType)
        {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Class<?> componentType = getGenericClassType(genericClassMap, genericArrayType.getGenericComponentType());
            Object array = Array.newInstance(componentType, 0);
            classType = array.getClass();
        }
        else if (type instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            classType = (Class<?>) parameterizedType.getRawType();
        }
        else if (type instanceof TypeVariable<?> && genericClassMap != null)
        {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            classType = genericClassMap.get(typeVariable.getName());
        }
        else if (!(type instanceof WildcardType))
            classType = (Class<?>) type;
        return classType;
    }

    public static Class<?>[] getMemberClassType(Map<String, Class<?>> genericClassMap, Type type)
    {
        List<Class<?>> classTypeList = new ArrayList<Class<?>>();
        if (type instanceof GenericArrayType)
        {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Class<?> componentType = getGenericClassType(genericClassMap, genericArrayType.getGenericComponentType());
            classTypeList.add(componentType);
        }
        else if (type instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            for (Type parameterizedClass : parameterizedType.getActualTypeArguments())
                classTypeList.add(getGenericClassType(genericClassMap, parameterizedClass));
        }
        else if (type instanceof TypeVariable<?>)
        {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            Class<?> classType = genericClassMap.get(typeVariable.getName());
            if (classType != null)
                classTypeList.add(classType);
        }
        else if (!(type instanceof WildcardType))
            classTypeList.add((Class<?>) type);
        return classTypeList.toArray(new Class<?>[0]);
    }

    /**
     * 读取所有参数的名称和类型列表。
     * @param classType 类型。
     * @return 方法映射表。
     */
    public static Map<String, MethodInfo> getMethodInfoMap(Class<?> classType)
    {
        Map<String, Class<?>> genericClassMap = getClassGenericInfo(classType);
        Map<String, MethodInfo> methodInfoMap = new HashMap<String, MethodInfo>();
        getMethodInfoMap(classType, genericClassMap, methodInfoMap);
        return methodInfoMap;
    }

    private static void getMethodInfoMap(Class<?> classType, Map<String, Class<?>> genericClassMap, Map<String, MethodInfo> methodInfoMap)
    {
        if (Object.class.equals(classType) || classType == null)
            return;

        getMethodInfoMap(classType.getSuperclass(), genericClassMap, methodInfoMap);

        for (Method method : classType.getDeclaredMethods())
        {
            if (!Modifier.isPublic(method.getModifiers()) || Object.class.equals(method.getDeclaringClass()) || "ServiceBase".equalsIgnoreCase(method.getDeclaringClass().getSimpleName()))
                continue;

            if (!classType.isInterface() && Modifier.isAbstract(method.getModifiers()))
                continue;

            MethodInfo methodInfo = new MethodInfo(method);
            methodInfo.setGenericClassMap(genericClassMap);
            methodInfo.initialize();
            if (methodInfo.isValid())
            {
                if (methodInfo.isIncludeAbstractParams() && methodInfoMap.containsKey(method.getName()))
                    continue;

                methodInfoMap.put(method.getName(), methodInfo);
            }
        }
    }
}
