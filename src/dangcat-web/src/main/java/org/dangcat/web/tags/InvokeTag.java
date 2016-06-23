package org.dangcat.web.tags;

import org.apache.log4j.Logger;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.serialize.json.JsonSerializer;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.dangcat.web.servlet.RequestParser;
import org.dangcat.web.servlet.ServiceCaller;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务调用标签。
 * @author dangcat
 * 
 */
public class InvokeTag extends SimpleTagSupport
{
    protected static final Logger logger = Logger.getLogger(InvokeTag.class);
    private String jndiName = null;
    private String method = "GET";

    private ServiceCaller createServiceCaller() throws JspException, IOException
    {
        String jndiName = this.getJndiName();
        if (jndiName.indexOf("/") == -1)
        {
            ServiceContext serviceContext = ServiceContext.getInstance();
            jndiName = serviceContext.getServiceInfo().getJndiName() + "/" + jndiName;
        }

        ServiceCaller serviceCaller = new ServiceCaller();
        this.parseJndiName(serviceCaller, jndiName);
        RequestParser.parseMethod(this.getMethod(), serviceCaller);
        if (this.getJspBody() != null)
        {
            StringWriter body = new StringWriter();
            this.getJspBody().invoke(body);
            serviceCaller.setContentData(body.toString());
        }
        return serviceCaller;
    }

    @Override
    public void doTag() throws JspTagException
    {
        if (ValueUtils.isEmpty(this.getJndiName()) && ValueUtils.isEmpty(this.getMethod()))
            throw new JspTagException("The invoke tag jndiName or method can't be empty.");

        try
        {
            // 构建服务调用对象。
            ServiceCaller serviceCaller = this.createServiceCaller();
            // 调用服务方法。
            Object result = this.invoke(serviceCaller);
            // 反馈执行结果。
            this.writeResult(result);
        }
        catch (Exception e)
        {
            logger.error(this, e);

            Throwable rootCause = null;
            if (e instanceof ServletException)
                rootCause = ((ServletException) e).getRootCause();
            throw new JspTagException(e.getMessage(), rootCause);
        }
    }

    public String getJndiName()
    {
        return this.jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public String getMethod()
    {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    private Object invoke(ServiceCaller serviceCaller) throws Exception
    {
        // 定位目标服务。
        ServiceInfo serviceInfo = ServiceFactory.getServiceLocator().getServiceInfo(serviceCaller.getJndiName());
        if (serviceInfo == null || serviceInfo.getInstance() == null)
            throw new JspTagException("The request " + this.getJndiName() + " and method " + this.getMethod() + " can't find serviceInfo.");

        // 找到执行的方法。
        MethodInfo methodInfo = serviceInfo.getServiceMethodInfo().getMethodInfo(serviceCaller.getMethod());
        if (methodInfo == null)
            throw new JspTagException("The request " + serviceCaller + " can't find destination.");

        return serviceCaller.invoke(serviceInfo.getInstance(), methodInfo);
    }

    private void parseJndiName(ServiceCaller serviceCaller, String requestURI)
    {
        List<String> resultList = new ArrayList<String>();
        String[] urlParts = requestURI.split("/");
        for (String urlPart : urlParts)
        {
            if (!ValueUtils.isEmpty(urlPart))
                resultList.add(urlPart);
        }

        if (resultList.size() >= 2)
        {
            serviceCaller.setJndiName(resultList.get(0) + "/" + resultList.get(1));
            if (resultList.size() > 2)
            {
                serviceCaller.setResourceId(ValueUtils.parseInt(resultList.get(2)));
                if (serviceCaller.getResourceId() == null)
                    serviceCaller.setMethod(resultList.get(2));
            }
        }
    }

    private void writeResult(Object result) throws IOException
    {
        PageContext pageContext = (PageContext) this.getJspContext();
        if (result == null)
            pageContext.getOut().println("null");
        else if (!ReflectUtils.isConstClassType(result.getClass()))
        {
            String data = JsonSerializer.serialize(result);
            pageContext.getOut().println(data);
        }
        else
            pageContext.getOut().println(result);
    }
}
