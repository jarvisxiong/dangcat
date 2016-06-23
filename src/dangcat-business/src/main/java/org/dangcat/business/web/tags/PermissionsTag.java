package org.dangcat.business.web.tags;

import org.apache.log4j.Logger;
import org.dangcat.commons.reflect.Permission;
import org.dangcat.framework.service.PermissionProvider;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.framework.service.impl.ServiceInfo;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.Collection;
import java.util.LinkedList;

/**
 * È¨ÏÞÊä³ö¡£
 * @author dangcat
 * 
 */
public class PermissionsTag extends SimpleTagSupport
{
    protected static final Logger logger = Logger.getLogger(PermissionsTag.class);
    private static final String PROPERTY_NAME = "permissions";

    @Override
    public void doTag() throws JspTagException
    {
        try
        {
            ServiceContext serviceContext = ServiceContext.getInstance();
            if (serviceContext != null)
            {
                Collection<String> permissions = null;
                ServiceInfo serviceInfo = serviceContext.getServiceInfo();
                PermissionProvider permissionProvider = serviceInfo.getPermissionProvider();
                if (permissionProvider != null)
                {
                    ServicePrincipal servicePrincipal = serviceContext.getServicePrincipal();
                    for (Permission permission : permissionProvider.getPermissionMap().values())
                    {
                        if (!servicePrincipal.hasPermission(permission.getValue()))
                            continue;
                        if (permissions == null)
                            permissions = new LinkedList<String>();
                        permissions.add(permission.getName());
                    }
                }

                if (permissions != null && permissions.size() > 0)
                {
                    JspWriter jspWriter = this.getJspContext().getOut();
                    jspWriter.write(PROPERTY_NAME);
                    jspWriter.write(":[");
                    int count = 0;
                    for (String value : permissions)
                    {
                        if (count > 0)
                            jspWriter.write(",");
                        jspWriter.write("\"");
                        jspWriter.write(value);
                        jspWriter.write("\"");
                        count++;
                    }
                    jspWriter.write("],");
                }
            }
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
}
