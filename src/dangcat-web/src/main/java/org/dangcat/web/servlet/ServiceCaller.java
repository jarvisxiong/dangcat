package org.dangcat.web.servlet;

import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.reflect.ParamInfo;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.serialize.json.JsonDeserializer;
import org.dangcat.web.upload.UploadContent;

import java.io.*;
import java.util.Map;

public class ServiceCaller {
    private static final String ID = "id";
    private String contentData = null;
    private String jndiName = null;
    private String method = null;
    private String paramsData = null;
    private String queryString = null;
    private String requestMethod = null;
    private String requestURI = null;
    private Integer resourceId = null;
    private UploadContent uploadContent = null;

    public ServiceCaller() {
    }

    public String getContentData() {
        return contentData;
    }

    public void setContentData(String contentData) {
        this.contentData = contentData;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParamsData() {
        return paramsData;
    }

    public void setParamsData(String paramsData) {
        this.paramsData = paramsData;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    private Reader getReader() throws IOException {
        if (this.getParamsData() != null)
            return new StringReader(this.getParamsData());
        if (this.getContentData() != null)
            return new StringReader(this.getContentData());
        return null;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public UploadContent getUploadContent() {
        return uploadContent;
    }

    public void setUploadContent(UploadContent uploadContent) {
        this.uploadContent = uploadContent;
    }

    public Object invoke(Object instance, MethodInfo methodInfo) throws Exception {
        Object[] paramValues = this.parseParamValues(methodInfo);
        return methodInfo.invoke(instance, paramValues);
    }

    private Object parseParamValue(ParamInfo paramInfo) throws IOException {
        Class<?> classType = paramInfo.getClassType();
        Object value = this.parseUploadContentValue(classType);

        // 资源标识映射。
        if (value == null && Integer.class.isAssignableFrom(classType) && (this.getResourceId() != null || (this.getParamsData() == null && this.getContentData() == null)))
            value = this.getResourceId();

        // 使用查询字串映射简单对象参数。
        if (value == null && !ReflectUtils.isConstClassType(classType)) {
            Object instance = ReflectUtils.newInstance(classType);
            Reader reader = this.getReader();
            Class<?>[] classTypes = paramInfo.getClassTypes();
            if (reader != null)
                value = JsonDeserializer.deserialize(reader, classTypes, instance);
        }
        return value;
    }

    private Object[] parseParamValues(MethodInfo methodInfo) throws IOException {
        ParamInfo[] paramInfos = methodInfo.getParamInfos();
        if (paramInfos == null || paramInfos.length == 0)
            return null;

        if (paramInfos.length == 1) {
            Object value = this.parseParamValue(paramInfos[0]);
            if (value != null)
                return new Object[]{value};
        }

        // 参数映射表产生参数实例。
        Map<String, Class<?>[]> paramClassTypeMap = methodInfo.getParamClassTypeMap();
        Map<String, Object> paramValuesMap = JsonDeserializer.deserialize(this.getReader(), paramClassTypeMap);
        if (this.resourceId != null)
            paramValuesMap.put(ID, this.resourceId);
        Object[] paramValues = new Object[paramClassTypeMap.size()];
        int index = 0;
        for (String paramName : paramClassTypeMap.keySet()) {
            Object value = null;
            if (this.uploadContent != null && paramName.equalsIgnoreCase(this.uploadContent.getFieldName())) {
                Class<?>[] classTypes = paramClassTypeMap.get(paramName);
                if (classTypes != null && classTypes.length == 1)
                    value = this.parseUploadContentValue(classTypes[0]);
            } else
                value = paramValuesMap.get(paramName);
            paramValues[index++] = value;
        }
        return paramValues;
    }

    private Object parseUploadContentValue(Class<?> classType) throws IOException {
        Object value = null;
        if (this.uploadContent != null) {
            if (classType.equals(File.class))
                value = this.uploadContent.getFile();
            else if (InputStream.class.isAssignableFrom(classType))
                value = this.uploadContent.getInputStream();
            else if (byte[].class.equals(classType))
                value = this.uploadContent.getBytes();
        }
        return value;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        info.append("RequestURI = " + this.getRequestURI());
        if (this.getQueryString() != null)
            info.append(", QueryString = " + this.getQueryString());
        info.append(", RequestMethod = " + this.getRequestMethod());
        info.append(", Method = " + this.getMethod());
        info.append(", JndiName = " + this.getJndiName());
        if (this.getResourceId() != null)
            info.append(", ResourceId = " + this.getResourceId());
        if (this.getParamsData() != null) {
            info.append("\r\nParams:\r\n");
            info.append(this.getParamsData());
        }
        if (this.getContentData() != null) {
            info.append("\r\nContentData:\r\n");
            info.append(this.getContentData());
        }
        return info.toString();
    }
}
