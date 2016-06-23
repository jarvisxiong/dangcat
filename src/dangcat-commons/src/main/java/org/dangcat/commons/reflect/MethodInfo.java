package org.dangcat.commons.reflect;

import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class MethodInfo implements Comparable<MethodInfo>
{
    private Map<String, Class<?>> genericClassMap = new HashMap<String, Class<?>>();
    private boolean hasAbstractParams = false;
    private Integer id = null;
    private Method method = null;
    private List<ParamInfo> paramInfoList = null;
    private Permission permission = null;
    private Method proxyMethod = null;
    private ParamInfo returnInfo = null;
    private Integer value = null;

    public MethodInfo(Method method)
    {
        this.method = method;
    }

    @Override
    public int compareTo(MethodInfo to)
    {
        if (to == null)
            return -1;
        return this.getName().compareToIgnoreCase(to.getName());
    }

    private void createParamInfos()
    {
        Type[] genericParameterTypes = this.method.getGenericParameterTypes();
        if (genericParameterTypes == null || genericParameterTypes.length == 0)
            return;

        Annotation[][] parameterAnnotations = this.method.getParameterAnnotations();
        LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = localVariableTableParameterNameDiscoverer.getParameterNames(this.method);

        for (int i = 0; i < genericParameterTypes.length; i++)
        {
            Type parameterType = genericParameterTypes[i];
            String parameterName = null;
            if (parameterAnnotations != null && i < parameterAnnotations.length)
            {
                Annotation[] parameterAnnotation = parameterAnnotations[i];
                if (parameterAnnotation != null && parameterAnnotation.length > 0)
                {
                    for (Annotation annotation : parameterAnnotation)
                    {
                        if (annotation instanceof Parameter)
                        {
                            Parameter parameter = (Parameter) annotation;
                            parameterName = parameter.name();
                            if (!Object.class.equals(parameter.classType()))
                                parameterType = parameter.classType();
                            break;
                        }
                    }
                }
            }
            if (parameterName == null && paramNames != null && i < paramNames.length)
                parameterName = paramNames[i];

            if (!ValueUtils.isEmpty(parameterName))
            {
                ParamInfo paramInfo = new ParamInfo(this.method, parameterName);
                Class<?> classType = GenericUtils.getClassType(this.genericClassMap, parameterType, paramInfo);
                if (classType != null)
                {
                    if (Modifier.isAbstract(classType.getModifiers()))
                        this.setIncludeAbstractParams(true);
                    paramInfo.setClassType(classType);
                    this.setParamInfo(i, paramInfo);
                }
            }
        }
    }

    private void createReturnInfo()
    {
        Type returnType = this.method.getGenericReturnType();
        if (returnType != null)
        {
            ParamInfo returnInfo = new ParamInfo(this.method);
            Class<?> classType = GenericUtils.getClassType(this.genericClassMap, returnType, returnInfo);
            if (classType != null)
            {
                if (Modifier.isAbstract(classType.getModifiers()))
                    this.setIncludeAbstractParams(true);
                returnInfo.setClassType(classType);
                this.setReturnInfo(returnInfo);
            }
        }
    }

    private Method findMethod(Object instance)
    {
        Method method = this.method;
        if (Proxy.isProxyClass(instance.getClass()))
        {
            if (this.proxyMethod == null)
            {
                Method findMethod = null;
                for (Method proxyMethod : instance.getClass().getMethods())
                {
                    if (!proxyMethod.getName().equalsIgnoreCase(this.getMethod().getName()))
                        continue;

                    Class<?>[] parameterTypes = this.getMethod().getParameterTypes();
                    Class<?>[] proxyParameterTypes = proxyMethod.getParameterTypes();
                    if (proxyParameterTypes.length != parameterTypes.length)
                        continue;

                    boolean found = true;
                    for (int i = 0; i < parameterTypes.length; i++)
                    {
                        if (!parameterTypes[i].isAssignableFrom(proxyParameterTypes[i]))
                        {
                            found = false;
                            break;
                        }
                    }
                    if (found)
                        findMethod = proxyMethod;
                }
                if (findMethod != null)
                    this.proxyMethod = findMethod;
            }
            method = this.proxyMethod;
        }
        return method;
    }

    public Map<String, Class<?>> getGenericClassMap()
    {
        return genericClassMap;
    }

    public void setGenericClassMap(Map<String, Class<?>> genericClassMap) {
        this.genericClassMap.putAll(genericClassMap);
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Method getMethod()
    {
        return method;
    }

    public String getName()
    {
        return this.getMethod().getName();
    }

    public Map<String, Class<?>[]> getParamClassTypeMap()
    {
        ParamInfo[] paramInfos = this.getParamInfos();
        Map<String, Class<?>[]> paramClassTypeMap = null;
        if (paramInfos != null && paramInfos.length > 0)
        {
            paramClassTypeMap = new LinkedHashMap<String, Class<?>[]>();
            for (ParamInfo paramInfo : paramInfos)
                paramClassTypeMap.put(paramInfo.getName(), paramInfo.getClassTypes());
        }
        return paramClassTypeMap;
    }

    public Collection<Class<?>> getParameterizedClasses()
    {
        Collection<Class<?>> classTypeCollection = new LinkedHashSet<Class<?>>();
        if (this.genericClassMap != null)
            classTypeCollection.addAll(this.genericClassMap.values());
        if (this.paramInfoList != null)
        {
            for (ParamInfo paramInfo : this.paramInfoList)
            {
                Class<?>[] classTypes = paramInfo.getParameterizedClasses();
                if (classTypes != null)
                {
                    for (Class<?> classType : paramInfo.getParameterizedClasses())
                        classTypeCollection.add(classType);
                }
            }
        }
        if (this.returnInfo != null)
        {
            Class<?>[] classTypes = this.returnInfo.getParameterizedClasses();
            if (classTypes != null)
            {
                for (Class<?> classType : this.returnInfo.getParameterizedClasses())
                    classTypeCollection.add(classType);
            }
        }
        return classTypeCollection;
    }

    public List<ParamInfo> getParamInfoList()
    {
        return paramInfoList;
    }

    public void setParamInfoList(List<ParamInfo> paramInfoList) {
        this.paramInfoList = paramInfoList;
    }

    public ParamInfo[] getParamInfos()
    {
        return this.paramInfoList == null ? null : this.paramInfoList.toArray(new ParamInfo[0]);
    }

    public Permission getPermission()
    {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public ParamInfo getReturnInfo()
    {
        return returnInfo;
    }

    public void setReturnInfo(ParamInfo returnInfo) {
        returnInfo.setMethodInfo(this);
        returnInfo.setName("return");
        this.returnInfo = returnInfo;
    }

    public Integer getValue()
    {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void initialize()
    {
        this.createParamInfos();
        this.createReturnInfo();
    }

    public Object invoke(Object instance, Object... params) throws Exception
    {
        Object result = null;
        try
        {
            Method method = this.findMethod(instance);
            if (params != null && params.length > 0)
                result = method.invoke(instance, params);
            else
                result = method.invoke(instance);
        }
        catch (IllegalArgumentException e)
        {
            throw e;
        }
        catch (IllegalAccessException e)
        {
            throw e;
        }
        catch (InvocationTargetException e)
        {
            throw (Exception) e.getTargetException();
        }
        return result;

    }

    protected boolean isIncludeAbstractParams()
    {
        return hasAbstractParams;
    }

    protected void setIncludeAbstractParams(boolean hasAbstractParams) {
        this.hasAbstractParams = hasAbstractParams;
    }

    public boolean isValid()
    {
        Type[] genericParameterTypes = this.method.getGenericParameterTypes();
        if (genericParameterTypes == null || genericParameterTypes.length == 0)
            return true;
        return this.paramInfoList != null && this.paramInfoList.size() == genericParameterTypes.length;
    }

    private void setParamInfo(int index, ParamInfo paramInfo)
    {
        if (this.paramInfoList == null)
            this.paramInfoList = new LinkedList<ParamInfo>();
        if (index >= this.paramInfoList.size())
        {
            if (!ValueUtils.isEmpty(paramInfo.getName()))
                this.paramInfoList.add(paramInfo);
        }
        else
            this.paramInfoList.set(index, paramInfo);
        paramInfo.setMethodInfo(this);
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append("Method: " + this.method.getName());
        if (this.paramInfoList != null)
        {
            for (ParamInfo paramInfo : this.paramInfoList)
            {
                if (this.paramInfoList.size() > 1)
                    info.append(Environment.LINETAB_SEPARATOR);
                else
                    info.append(" ");
                info.append(paramInfo);
            }
        }
        return info.toString();
    }
}
