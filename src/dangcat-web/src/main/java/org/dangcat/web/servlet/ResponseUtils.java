package org.dangcat.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dangcat.commons.serialize.json.JsonResult;
import org.dangcat.commons.serialize.json.JsonSerializer;
import org.dangcat.commons.utils.LocaleUtils;
import org.dangcat.framework.serialize.json.ServiceExceptionSerializer;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServicePrincipal;

import com.google.gson.stream.JsonWriter;

public class ResponseUtils
{
    public static final String DEFAULT_CHARSET = "utf-8";
    public static final String LOGIN_LOCALE = "locale";

    public static ServiceContext createServiceContext(HttpServletRequest request, HttpServletResponse response, ServicePrincipal servicePrincipal) throws IOException
    {
        ServiceContext serviceContext = ServiceContext.getInstance();
        if (serviceContext == null || serviceContext.getServicePrincipal() == null)
        {
            HttpSession httpSession = request.getSession();
            Locale locale = (Locale) httpSession.getAttribute(Locale.class.getSimpleName());
            if (locale == null)
            {
                locale = LocaleUtils.parse(request.getParameter(LOGIN_LOCALE));
                httpSession.setAttribute(Locale.class.getSimpleName(), locale);
            }
            serviceContext = new ServiceContext(httpSession.getId(), servicePrincipal, locale);
            serviceContext.addParam(HttpServletRequest.class, request);
            serviceContext.addParam(HttpServletResponse.class, response);
            serviceContext.addParam(HttpSession.class, httpSession);
            ServiceContext.set(serviceContext);
        }
        return serviceContext;
    }

    public static void responseException(HttpServletResponse response, Exception exception) throws IOException
    {
        response.setContentType(ContentType.getType(ContentType.json, DEFAULT_CHARSET));
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(DEFAULT_CHARSET);
        JsonWriter jsonWriter = new JsonWriter(response.getWriter());
        ServiceExceptionSerializer.serialize(jsonWriter, exception);
    }

    public static void responseResult(HttpServletResponse response, Object result) throws IOException
    {
        responseResult(response, result, ContentType.getType(ContentType.json, DEFAULT_CHARSET));
    }

    public static void responseResult(HttpServletResponse response, Object result, String contentType) throws IOException
    {
        responseResult(response, result, ContentType.getType(ContentType.json, DEFAULT_CHARSET), DEFAULT_CHARSET);
    }

    public static void responseResult(HttpServletResponse response, Object result, String contentType, String encoding) throws IOException
    {
        if (response.getContentType() == null && result != null)
        {
            response.setContentType(contentType);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding(encoding);
            Writer writer = response.getWriter();
            if (result instanceof JsonResult)
            {
                JsonResult jsonResult = (JsonResult) result;
                writer.write(jsonResult.getContent());
            }
            else
                JsonSerializer.serialize(result, writer);
            writer.flush();
        }
    }
}
