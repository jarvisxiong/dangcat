package org.dangcat.web.servlet;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.web.invoke.InvokeProcess;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 执行进度查询。
 *
 * @author dangcat
 */
public class InvokeProcessServlet extends ServiceServletBase {
    private static final String CANCEL = "cancel";
    private static final String JNDI_NAME = "jndiName";
    private static final long serialVersionUID = 1L;

    @Override
    protected void executeService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jndiName = request.getParameter(JNDI_NAME);
        String invokeProcessName = jndiName + "/" + InvokeProcess.class.getSimpleName();
        HttpSession httpSession = request.getSession();
        InvokeProcess invokeProcess = (InvokeProcess) httpSession.getAttribute(invokeProcessName);
        if (invokeProcess == null)
            throw new Exception("No invoke process found at " + jndiName);

        if (ValueUtils.parseBoolean(request.getParameter(CANCEL), Boolean.FALSE))
            invokeProcess.cancel();

        ResponseUtils.responseResult(response, invokeProcess.getInvokeResults());

        if (invokeProcess.isFinishedAll())
            httpSession.removeAttribute(invokeProcessName);
    }
}
