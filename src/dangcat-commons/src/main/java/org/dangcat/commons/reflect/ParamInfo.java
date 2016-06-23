package org.dangcat.commons.reflect;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ParamInfo {
    private Class<?> classType = null;
    private Method method = null;
    private MethodInfo methodInfo = null;
    private String name = null;
    private List<Class<?>> parameterizedClassList = null;

    public ParamInfo(Method method) {
        this.method = method;
    }

    public ParamInfo(Method method, String name) {
        this.method = method;
        this.name = name;
    }

    public void addParameterizedClass(Class<?> parameterizedClass) {
        if (this.parameterizedClassList == null)
            this.parameterizedClassList = new LinkedList<Class<?>>();
        this.parameterizedClassList.add(parameterizedClass);
    }

    public Class<?> getClassType() {
        return classType;
    }

    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

    public Class<?>[] getClassTypes() {
        Set<Class<?>> classTypeSet = new LinkedHashSet<Class<?>>();
        classTypeSet.add(this.getClassType());
        if (this.parameterizedClassList != null)
            classTypeSet.addAll(this.parameterizedClassList);
        return classTypeSet.toArray(new Class[0]);
    }

    public Method getMethod() {
        return method;
    }

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    protected void setMethodInfo(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public Class<?>[] getParameterizedClasses() {
        return this.parameterizedClassList == null ? null : this.parameterizedClassList.toArray(new Class<?>[0]);
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        info.append("Param: name = " + this.getName());
        info.append(", classType = " + this.getClassType());
        if (this.parameterizedClassList != null) {
            int index = 1;
            for (Class<?> classType : this.parameterizedClassList) {
                info.append(", ClassType[" + (index++) + "]: ");
                info.append(classType);
            }
        }
        return info.toString();
    }
}
