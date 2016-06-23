package org.dangcat.web.servlet;

import org.apache.log4j.Logger;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Service Servlet¡£
 * @author dangcat
 * 
 */
public class ServiceServletBase extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    protected final Logger logger = Logger.getLogger(this.getClass());

    protected void clear()
    {
        ServiceContext.remove();
    }

    protected void executeService(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        super.service(request, response);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            this.executeService(request, response);
        }
        catch (ServiceException e)
        {
            ResponseUtils.responseException(response, e);
        }
        catch (Exception e)
        {
            ResponseUtils.responseException(response, e);
            if (logger.isDebugEnabled())
                logger.error(this, e);
            else
                logger.error(e);
        }
        finally
        {
            this.clear();
        }
    }
}
