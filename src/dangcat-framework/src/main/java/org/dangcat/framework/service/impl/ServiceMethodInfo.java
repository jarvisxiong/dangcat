package org.dangcat.framework.service.impl;

import org.dangcat.commons.reflect.GenericUtils;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.resource.Resources;
import org.dangcat.commons.utils.Environment;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class ServiceMethodInfo
{
    /** 服务访问类型。 */
    private Class<?> accessClassType = null;
    /** 方法信息。 */
    private Map<String, MethodInfo> methodInfoMap = null;
    /** 方法信息。 */
    private Map<Integer, MethodInfo> methodInfoValueMap = null;
    /** 服务实例类型。 */
    private Class<?> serviceClassType = null;

    public ServiceMethodInfo(Class<?> accessClassType, Class<?> serviceClassType)
    {
        this.accessClassType = accessClassType;
        this.serviceClassType = serviceClassType;
    }

    private void addResourceClass(Collection<Class<?>> classTypeCollection, Class<?> classType)
    {
        if (classType != null)
        {
            if (Object.class.equals(classType))
                return;
            classTypeCollection.add(classType);
            this.addResourceClass(classTypeCollection, classType.getSuperclass());
        }
    }

    public MethodInfo getMethodInfo(Integer value)
    {
        return this.methodInfoValueMap == null ? null : this.methodInfoValueMap.get(value);
    }

    public MethodInfo getMethodInfo(String name)
    {
        return this.methodInfoMap == null ? null : this.methodInfoMap.get(name);
    }

    public Collection<MethodInfo> getMethodInfos()
    {
        List<MethodInfo> methodInfoList = null;
        if (this.methodInfoMap != null)
        {
            methodInfoList = new ArrayList<MethodInfo>();
            methodInfoList.addAll(this.methodInfoMap.values());
            Collections.sort(methodInfoList);
        }
        return methodInfoList;
    }

    public Map<Integer, MethodInfo> getMethodInfoValueMap()
    {
        return this.methodInfoValueMap;
    }

    public void setMethodInfoValueMap(Map<Integer, MethodInfo> methodInfoValueMap) {
        this.methodInfoValueMap = methodInfoValueMap;
    }

    public Collection<Class<?>> getResourceClassTypes()
    {
        Collection<Class<?>> classTypeCollection = new LinkedHashSet<Class<?>>();
        for (MethodInfo methodInfo : this.methodInfoMap.values())
        {
            for (Class<?> classType : methodInfo.getParameterizedClasses())
            {
                if (ServiceResourceProvider.class.isAssignableFrom(classType) && !classTypeCollection.contains(classType))
                    classTypeCollection.add(classType);
            }
        }
        this.readResources(this.accessClassType, classTypeCollection);
        this.readResources(this.serviceClassType, classTypeCollection);
        return classTypeCollection;
    }

    public void initialize()
    {
        Class<?> classType = Proxy.isProxyClass(this.serviceClassType) ? this.accessClassType : this.serviceClassType;
        Map<String, MethodInfo> methodInfoMap = GenericUtils.getMethodInfoMap(classType);
        if (this.accessClassType.isInterface())
        {
            Map<String, MethodInfo> interfaceMethodInfoMap = new HashMap<String, MethodInfo>();
            for (Method method : this.accessClassType.getMethods())
            {
                MethodInfo methodInfo = methodInfoMap.get(method.getName());
                if (methodInfo != null)
                {
                    MethodInfo interfaceMethodInfo = new MethodInfo(method);
                    interfaceMethodInfo.setParamInfoList(methodInfo.getParamInfoList());
                    interfaceMethodInfo.setGenericClassMap(methodInfo.getGenericClassMap());
                    interfaceMethodInfo.initialize();
                    interfaceMethodInfoMap.put(method.getName(), interfaceMethodInfo);
                }
            }
            this.methodInfoMap = interfaceMethodInfoMap;
        }
        else
            this.methodInfoMap = methodInfoMap;
    }

    private void readResources(Class<?> classType, Collection<Class<?>> classTypeCollection)
    {
        if (classType == null || Object.class.equals(classType))
            return;

        this.readResources(classType.getSuperclass(), classTypeCollection);

        this.addResourceClass(classTypeCollection, classType);
        Resources resourcesAnnotation = classType.getAnnotation(Resources.class);
        if (resourcesAnnotation != null)
        {
            for (Class<?> resourceClassType : resourcesAnnotation.value())
                this.addResourceClass(classTypeCollection, resourceClassType);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        if (this.methodInfoMap != null && this.methodInfoMap.size() > 0)
        {
            info.append("MethodInfos: ");
            for (MethodInfo methodInfo : this.methodInfoMap.values())
            {
                if (methodInfo.getParamInfoList() != null && methodInfo.getParamInfoList().size() > 1)
                {
                    StringBuilder methodInfoText = new StringBuilder();
                    StringTokenizer stringTokenizer = new StringTokenizer(methodInfo.toString(), Environment.LINE_SEPARATOR);
                    while (stringTokenizer.hasMoreElements())
                    {
                        info.append(Environment.LINETAB_SEPARATOR);
                        info.append(stringTokenizer.nextToken());
                    }
                    info.append(methodInfoText);
                }
                else
                {
                    info.append(Environment.LINETAB_SEPARATOR);
                    info.append(methodInfo);
                }
            }
        }
        return info.toString();
    }
}
