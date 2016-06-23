package org.dangcat.framework.service;

import org.apache.log4j.Logger;
import org.dangcat.commons.reflect.GenericUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

public abstract class InjectProvider
{
    protected final Logger logger = Logger.getLogger(this.getClass());

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof InjectProvider)
        {
            InjectProvider injectProvider = (InjectProvider) obj;
            return this.getAnnotation().equals(injectProvider.getAnnotation());
        }
        return super.equals(obj);
    }

    private Class<?> getAccessClassType(Map<String, Class<?>> genericClassMap, Class<?> accessClassType, Type genericType)
    {
        if (accessClassType.equals(Object.class))
            accessClassType = GenericUtils.getGenericClassType(genericClassMap, genericType);
        return accessClassType;
    }

    public abstract Class<? extends Annotation> getAnnotation();

    protected abstract Object getObject(ServiceProvider serviceProvider, Object serviceInstance, Class<?> accessClassType, Annotation annotation);

    @Override
    public int hashCode()
    {
        return this.getAnnotation().hashCode();
    }

    public void inject(ServiceProvider serviceProvider, Object serviceInstance)
    {
        Map<String, Class<?>> genericClassMap = GenericUtils.getClassGenericInfo(serviceInstance.getClass());
        this.injectFields(serviceProvider, serviceInstance, serviceInstance.getClass(), genericClassMap);
        this.injectMethods(serviceProvider, serviceInstance, serviceInstance.getClass(), genericClassMap);
    }

    private void injectFields(ServiceProvider serviceProvider, Object serviceInstance, Class<?> classType, Map<String, Class<?>> genericClassMap)
    {
        Field[] fields = classType.getDeclaredFields();
        for (Field field : fields)
        {
            try
            {
                field.setAccessible(true);
                if (field.get(serviceInstance) != null)
                    continue;

                Annotation annotation = field.getAnnotation(this.getAnnotation());
                if (annotation != null)
                {
                    Class<?> accessClassType = this.getAccessClassType(genericClassMap, field.getType(), field.getGenericType());
                    Object injectService = this.getObject(serviceProvider, serviceInstance, accessClassType, annotation);
                    if (injectService != null && field.getType().isAssignableFrom(injectService.getClass()))
                        field.set(serviceInstance, injectService);
                }
            }
            catch (Exception e)
            {
                this.logger.error(serviceInstance, e);
            }
        }
        if (!Object.class.equals(classType.getSuperclass()))
            this.injectFields(serviceProvider, serviceInstance, classType.getSuperclass(), genericClassMap);
    }

    private void injectMethods(ServiceProvider serviceProvider, Object serviceInstance, Class<?> classType, Map<String, Class<?>> genericClassMap)
    {
        Method[] methods = classType.getDeclaredMethods();
        for (Method method : methods)
        {
            try
            {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes != null && parameterTypes.length == 1)
                {
                    Annotation annotation = method.getAnnotation(this.getAnnotation());
                    if (annotation != null)
                    {
                        Type[] genericParameterTypes = method.getGenericParameterTypes();
                        Class<?> accessClassType = this.getAccessClassType(genericClassMap, parameterTypes[0], genericParameterTypes[0]);
                        Object injectService = this.getObject(serviceProvider, serviceInstance, accessClassType, annotation);
                        if (injectService != null)
                            method.invoke(serviceInstance, injectService);
                    }
                }
            }
            catch (Exception e)
            {
                this.logger.error(serviceInstance, e);
            }
        }
        if (!Object.class.equals(classType.getSuperclass()))
            this.injectMethods(serviceProvider, serviceInstance, classType.getSuperclass(), genericClassMap);
    }
}
