package org.dangcat.web.servlet;

import org.dangcat.commons.serialize.json.JsonSerializer;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.web.upload.UploadContent;
import org.dangcat.web.upload.UploadManager;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class RequestParser {
    private static final String CACHE_FALSE = "_";
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_PUT = "PUT";
    private static final String REST_FORM = ".jsf";
    private static final String REST_QUERY = "rest";
    private static final String SERVICE_DELETE = "delete";
    private static final String SERVICE_QUERY = "query";
    private static final String SERVICE_SAVE = "save";
    private static final String SERVICE_VIEW = "view";

    public static ServiceCaller createServiceCaller(HttpServletRequest request) throws Exception {
        ServiceCaller serviceCaller = new ServiceCaller();
        parseRequest(request, serviceCaller);
        parseJndiName(request, serviceCaller);
        parseParamsData(request, serviceCaller);
        parseMethod(request, serviceCaller);
        parseContentData(request, serviceCaller);
        return serviceCaller;
    }

    public static boolean isExecuteRequest(HttpServletRequest request) {
        return !isFormRequest(request) && !isQueryRequest(request);
    }

    public static boolean isFormRequest(HttpServletRequest request) {
        String method = request.getRequestURI();
        return !ValueUtils.isEmpty(method) && method.endsWith(REST_FORM);
    }

    public static boolean isQueryRequest(HttpServletRequest request) {
        Enumeration<String> paramNameEnumeration = request.getParameterNames();
        while (paramNameEnumeration.hasMoreElements()) {
            String paramName = paramNameEnumeration.nextElement();
            if (REST_QUERY.equalsIgnoreCase(paramName))
                return true;
        }
        return false;
    }

    private static void parseContentData(HttpServletRequest request, ServiceCaller serviceCaller) throws Exception {
        if (UploadManager.isMultipart(request)) {
            UploadContent uploadContent = UploadManager.getInstance().upload(request);
            serviceCaller.setUploadContent(uploadContent);
            if (uploadContent != null && !uploadContent.getParams().isEmpty()) {
                String data = JsonSerializer.serialize(uploadContent.getParams());
                serviceCaller.setContentData(data);
            }
        } else if (request.getContentLength() > 0) {
            StringBuilder data = new StringBuilder();
            BufferedReader bufferedReader = request.getReader();
            String line = null;
            while ((line = bufferedReader.readLine()) != null)
                data.append(line);
            serviceCaller.setContentData(data.toString());
        }
    }

    private static void parseJndiName(HttpServletRequest request, ServiceCaller serviceCaller) {
        List<String> resultList = parseRequestUrlParts(request);

        if (resultList.size() >= 3) {
            serviceCaller.setJndiName(resultList.get(1) + "/" + resultList.get(2));
            if (resultList.size() > 3) {
                serviceCaller.setResourceId(ValueUtils.parseInt(resultList.get(3)));
                if (serviceCaller.getResourceId() == null)
                    serviceCaller.setMethod(resultList.get(3));
            }
        }
    }

    private static void parseMethod(HttpServletRequest request, ServiceCaller serviceCaller) {
        parseMethod(request.getMethod(), serviceCaller);
    }

    public static void parseMethod(String method, ServiceCaller serviceCaller) {
        if (METHOD_GET.equalsIgnoreCase(method)) {
            if (serviceCaller.getResourceId() != null)
                serviceCaller.setMethod(SERVICE_VIEW);
            else if (serviceCaller.getMethod() == null) {
                if (serviceCaller.getResourceId() != null)
                    serviceCaller.setMethod(SERVICE_VIEW);
                else
                    serviceCaller.setMethod(SERVICE_QUERY);
            }
        } else if (METHOD_DELETE.equalsIgnoreCase(method))
            serviceCaller.setMethod(SERVICE_DELETE);
        else if (METHOD_PUT.equalsIgnoreCase(method)) {
            if (serviceCaller.getMethod() == null)
                serviceCaller.setMethod(SERVICE_SAVE);
            serviceCaller.setResourceId(null);
        }
    }

    private static void parseParamsData(HttpServletRequest request, ServiceCaller serviceCaller) {
        if (request.getQueryString() != null) {
            StringWriter writer = null;
            Enumeration<?> enumeration = request.getParameterNames();
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();

                if (REST_QUERY.equalsIgnoreCase(name) || CACHE_FALSE.equalsIgnoreCase(name))
                    continue;

                if (writer == null) {
                    writer = new StringWriter();
                    writer.write("{");
                } else
                    writer.write(",");
                writer.write("\"");
                writer.write(name);
                writer.write("\"");
                writer.write(":");
                writer.write("\"");
                writer.write(request.getParameter(name));
                writer.write("\"");
            }
            if (writer != null) {
                writer.write("} ");
                serviceCaller.setParamsData(writer.toString());
            }
        }
    }

    private static void parseRequest(HttpServletRequest request, ServiceCaller serviceCaller) {
        serviceCaller.setRequestURI(request.getRequestURI());
        serviceCaller.setRequestMethod(request.getMethod());
        if (!ValueUtils.isEmpty(request.getQueryString())) {
            try {
                serviceCaller.setQueryString(URLDecoder.decode(request.getQueryString(), ResponseUtils.DEFAULT_CHARSET) + "\"");
            } catch (UnsupportedEncodingException e) {
            }
        }
    }

    private static List<String> parseRequestUrlParts(HttpServletRequest request) {
        List<String> resultList = new ArrayList<String>();
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (!ValueUtils.isEmpty(contextPath))
            requestURI = requestURI.substring(contextPath.length());
        String[] urlParts = requestURI.split("/");
        for (String urlPart : urlParts) {
            if (!ValueUtils.isEmpty(urlPart))
                resultList.add(urlPart);
        }
        return resultList;
    }
}
