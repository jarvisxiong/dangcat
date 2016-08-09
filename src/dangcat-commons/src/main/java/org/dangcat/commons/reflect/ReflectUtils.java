package org.dangcat.commons.reflect;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 转换辅助工具。
 */
public class ReflectUtils {
    protected static final Logger logger = Logger.getLogger(ReflectUtils.class);

    /**
     * 增加类加载器。
     *
     * @param classLoader 类加载器。
     */
    public static void addClassLoader(ClassLoader classLoader) {
        InstanceBuilder.addClassLoader(classLoader);
    }

    /**
     * 对两个对象拷贝属性。
     *
     * @param srcObject 来源对象。
     * @param dstObject 目标对象。
     */
    public static void copyProperties(Object srcObject, Object dstObject) {
        if (srcObject == null || dstObject == null)
            return;

        try {
            boolean isSameClass = srcObject.getClass().equals(dstObject.getClass());
            BeanInfo beanInfo = Introspector.getBeanInfo(srcObject.getClass());
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                if (Class.class.getSimpleName().equalsIgnoreCase(propertyDescriptor.getName()))
                    continue;

                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null) {
                    Object value = readMethod.invoke(srcObject);
                    if (isSameClass) {
                        Method writeMethod = propertyDescriptor.getWriteMethod();
                        if (writeMethod != null)
                            writeMethod.invoke(dstObject, value);
                    } else
                        setProperty(dstObject, propertyDescriptor.getName(), value);
                }
            }
        } catch (Exception e) {
            logger.error(srcObject, e);
        }
    }

    /**
     * 从一个类中找到包含指定注释的注释对象。
     *
     * @param classType      指定类型。
     * @param annotationType 注释类型。
     */
    public static <T extends Annotation> T findAnnotation(Class<?> classType, Class<T> annotationType) {
        return BeanUtils.findAnnotation(classType, annotationType);
    }

    /**
     * 从一个类中找到包含指定注释的所有注释对象。
     *
     * @param annotationList 注释列表。
     * @param classType      指定类型。
     * @param annotationType 注释类型。
     */
    public static void findAnnotations(Class<?> classType, Class<? extends Annotation> annotationType, List<Annotation> annotationList) {
        BeanUtils.findAnnotations(classType, annotationType, annotationList);
    }

    /**
     * 从一个类中找到包含指定注释的字段。
     *
     * @param classType      指定类型。
     * @param annotationType 注释类型。
     */
    public static Field findField(Class<?> classType, Class<? extends Annotation> annotationType) {
        return BeanUtils.findField(classType, annotationType);
    }

    /**
     * 从一个类中找到包含指定注释的所有字段。
     *
     * @param classType      指定类型。
     * @param annotationType 注释类型。
     */
    public static List<Field> findFields(Class<?> classType, Class<? extends Annotation> annotationType) {
        List<Field> fieldList = new LinkedList<Field>();
        BeanUtils.findFields(classType, annotationType, fieldList);
        return fieldList;
    }

    /**
     * 通过反射读取对象字段的值。
     *
     * @param instance  对象实例。
     * @param fieldName 字段名称。
     */
    public static Object getFieldValue(Object instance, String fieldName) {
        return BeanUtils.getFieldValue(instance, fieldName);
    }

    public static Object getInstance(Class<?> classType) {
        return ReflectUtils.invoke(classType, "getInstance");
    }

    /**
     * 读取对象属性。
     *
     * @param instance     对象实例。
     * @param propertyName 属性名。
     */
    public static Object getProperty(Object instance, String propertyName) {
        return BeanUtils.getProperty(instance, propertyName);
    }

    public static Object invoke(Object instance, Method method, Object... params) {
        return BeanUtils.invoke(instance, method, params);
    }

    public static Object invoke(Object instance, String methodName, Object... params) {
        Object result = null;
        try {
            result = BeanUtils.invoke(instance, methodName, params);
        } catch (Exception e) {
            logger.error(instance, e);
        }
        return result;
    }

    /**
     * 是否是常规类型。
     */
    public static boolean isConstClassType(Class<?> classType) {
        return InstanceBuilder.getConstClassList().contains(classType);
    }

    /**
     * 载入类型对象。
     */
    public static Class<?> loadClass(String className) {
        return InstanceBuilder.loadClass(className.trim());
    }

    /**
     * 根据名称产生实例。
     */
    public static Object newInstance(Class<?> classType) {
        return InstanceBuilder.newInstance(classType);
    }

    /**
     * 根据指定类名、参数类型和参数构建对象实例。
     *
     * @param classType      类型名称。
     * @param parameterTypes 参数类型。
     * @param parameters     构建参数。
     * @return 构建的对象实例。
     */
    public static Object newInstance(Class<?> classType, Class<?>[] parameterTypes, Object[] parameters) {
        return InstanceBuilder.newInstance(classType, parameterTypes, parameters);
    }

    /**
     * 根据名称产生实例。
     */
    public static Object newInstance(String className) {
        return InstanceBuilder.newInstance(className);
    }

    /**
     * 根据指定类名、参数类型和参数构建对象实例。
     *
     * @param className      类型名称。
     * @param parameterTypes 参数类型。
     * @param parameters     构建参数。
     * @return 构建的对象实例。
     */
    public static Object newInstance(String className, Class<?>[] parameterTypes, Object[] parameters) {
        return InstanceBuilder.newInstance(className, parameterTypes, parameters);
    }

    /**
     * 文字值转换成实际值对象。
     */
    public static Object parseValue(Class<?> classType, String value) {
        Object result = null;
        try {
            if (ValueUtils.isEmpty(value))
                return result;
            if (value.getClass().equals(classType))
                return value;
            if (classType.equals(Class.class))
                result = InstanceBuilder.loadClass(value.trim());
            else
                result = ValueUtils.parseValue(classType, value);
        } catch (Exception e) {
            logger.error(classType, e);
        }
        return result;
    }

    /**
     * 通过反射读取对象字段的值。
     *
     * @param instance  对象实例。
     * @param fieldName 字段名称。
     */
    public static void setFieldValue(Object instance, String fieldName, Object value) {
        BeanUtils.setFieldValue(instance, fieldName, value);
    }

    /**
     * 设置对象属性。
     *
     * @param instance     对象实例。
     * @param propertyName 属性名。
     * @param value        属性值。
     */
    public static void setProperty(Object instance, String propertyName, Object value) {
        BeanUtils.setProperty(instance, propertyName, value);
    }

    public static String toFieldName(String value) {
        if (!ValueUtils.isEmpty(value) && Character.isLowerCase(value.charAt(0)))
            return value.substring(0, 1).toUpperCase() + value.substring(1);
        return value;
    }

    public static String toPropertyName(String value) {
        if (!ValueUtils.isEmpty(value) && Character.isUpperCase(value.charAt(0)))
            return value.substring(0, 1).toLowerCase() + value.substring(1);
        return value;
    }
}
